#include <boost/algorithm/string/classification.hpp>
#include <boost/algorithm/string/split.hpp>
#include <boost/program_options.hpp>
#include <cmath>
#include <chrono>
#include <iostream>
#include <is/is.hpp>
#include <is/msgs/common.hpp>
#include <is/msgs/robot.hpp>
#include <chrono>
#include <thread>

using namespace is::msg::robot;
using namespace is::msg::common;
using namespace std::chrono_literals;
namespace po = boost::program_options;

constexpr double pi() {
  return std::atan(1) * 4;
}
auto deg2rad = [](double deg) { return deg * (pi() / 180.0); };
auto rad2deg = [](double rad) { return rad * (180.0 / pi()); };

int main(int argc, char* argv[]) {
  std::string uri;
  std::string entity;
  int64_t period;
  po::options_description description("Allowed options");
  auto&& options = description.add_options();
  options("help,", "show available options");
  options("uri,u", po::value<std::string>(&uri)->default_value("amqp://localhost"), "broker uri");
  options("period,t", po::value<int64_t>(&period)->default_value(100), "sampling period in ms");
  po::variables_map vm;
  po::store(po::parse_command_line(argc, argv, description), vm);
  po::notify(vm);
  if (vm.count("help")) {
    std::cout << description << std::endl;
    return 1;
  }
  auto is = is::connect(uri);
  auto client = is::make_client(is);
  is::logger()->info("make client");
  auto status = is.subscribe("bbStatusService.info", "sa-data");
  is::logger()->info("subscrito");

  while(1){
    auto statusEncode = Status{"Alone", "O bebê está sozinho"};
    //is.publish("bbAloneService.info", is::msgpack(status::ok), "sa-data");
    is.publish("bbStatusService.info", is::msgpack(statusEncode), "sa-data");
    is::logger()->info("enviou mensagem");
    std::this_thread::sleep_for(std::chrono::milliseconds(1000));
    is::logger()->info("aguardando mensagem");
    std::this_thread::sleep_for(std::chrono::milliseconds(1000));
    auto status_msg = is.consume(status);
    is::logger()->info("mensagem consumida");
    std::this_thread::sleep_for(std::chrono::milliseconds(1000));
    auto statusDecoded = is::msgpack<Status>(status_msg);
    is::logger()->info("Situação {}", statusDecoded.value);
    is::logger()->info("Motivo: {}", statusDecoded.why);
    std::this_thread::sleep_for(std::chrono::milliseconds(1000));
  }
  return 0;
}
