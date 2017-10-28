#include "../include/is.hpp"
#include "../include/msgs/common.hpp"
#include <vector>

using namespace is::msg::common;

Infos infos;

is::Reply add_info(is::Request req) {
  Info info = is::msgpack<Info>(req);  
  infos.infos.push_back(info);
  return is::msgpack(infos);
}

//recupera as informações das entidades e serviços dessas entidades
//esse serviço pode ser utilizado por um serviço superior para realizar operações de mapeamento
is::Reply get_infos(is::Request) {
  return is::msgpack(infos);
}

using namespace std::chrono_literals;

int main(int , char* []) {

  std::string uri = "amqp://guest:guest@localhost:5672";
  std::thread thread([=] () { 
    is::ServiceProvider service("device", is::make_channel(uri), "metadata");
    service.expose("add_info", add_info);
    service.expose("get_infos", get_infos);
    service.listen();
  });

  auto is = is::Connection(uri, "metadata");
  auto client = is::ServiceClient(is.channel, "metadata"); 

  Infos value;
  for (;;) {

    auto req_id = client.request("device.get_infos", is::msgpack(0));
    auto reply = client.receive_for(1s, req_id, is::policy::discard_others);

    if (reply == nullptr) {
      is::log::error("Request {} timeout!", req_id);
    } else {
      value = is::msgpack<Infos>(reply);
      if (value.infos.size() > 0){
        is::log::info("Reply: {}", value.infos[0].name);
      }else{
        is::log::info("sem entidades");
      }
    }

    std::this_thread::sleep_for(1s);
  }
  
}

