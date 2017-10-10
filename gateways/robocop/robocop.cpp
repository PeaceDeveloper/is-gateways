#include <boost/program_options.hpp>
#include <signal.h>
#include <iostream>
#include "../../drivers/robocop/robocop.hpp"
#include "../robot/robot.hpp"

namespace po = boost::program_options;

void sigint(int a)
{
    std::cout << a << std::endl;
    std::cout << "exiting" << std::endl;
    exit(0);
}

int main(int argc, char* argv[]) {
  signal(SIGINT, sigint);
  //declaração de variáveis
  std::string uri; //url do broken
  std::string entity; //nome da entidade
  std::string robot_serial_port; //porta serial que se concetará o robô?
  std::string robot_hostname; //host name do robô?
  int robot_port; //porta do robô?

  //permite o usuáio interativamente selecionar as opções de conexão com o robô
  po::options_description description("Allowed options");
  auto&& options = description.add_options();
  options("help,", "show available options");
  options("uri,u", po::value<std::string>(&uri)->default_value("amqp://localhost"), "broker uri");
  options("entity,e", po::value<std::string>(&entity), "entity name");
  options("serialport,s", po::value<std::string>(&robot_serial_port), "robot serial port");
  options("hostname,h", po::value<std::string>(&robot_hostname), "robot hostname");
  options("port,p", po::value<int>(&robot_port), "robot port");

  po::variables_map vm;
  po::store(po::parse_command_line(argc, argv, description), vm);
  po::notify(vm);

  if (vm.count("help") || !vm.count("entity")) {
    std::cout << description << std::endl;
    return 1;
  }
  
  //declarando a varíavel robot do tipo driver
  is::driver::Robocop robot;

  if (vm.count("serialport") && !vm.count("hostname")) {
    //conexão com o robo pela sua porta serial caso o hostname não tenha sido informado pelo usuário
    robot.connect(robot_serial_port);
  } else if (vm.count("hostname") && vm.count("port")) {
    //conexão com o robô pelo hostname e porta
    robot.connect(robot_hostname, robot_port);
  } else {
    //retorna erro
    std::cout << description << std::endl;
    return 1;
  }

  //instanciando um gateway robot utilizando o driver do robo robocop. É passando como parâmetro
  //nessa instanciação o nome dessa entidade, o endereço do broker e o driver instanciado
  is::gw::Robot<is::driver::Robocop> gw(entity, uri, robot);
  return 0;
}