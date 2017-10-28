#ifndef __IS_NEURONSCONTROLLER_ROBOT_HPP__
#define __IS_NEURONSCONTROLLER_ROBOT_HPP__

#include <iostream>
#include <memory>
#include <cstdio>
#include <rethinkdb.h>
#include <chrono>

#include <is/msgs/common.hpp>
#include <is/msgs/geometry.hpp>
#include <is/msgs/robot.hpp>

namespace is{
	namespace neuronscontroller{

		namespace R = RethinkDB;
		using namespace std::chrono;
		using namespace is::msg::robot;
		using namespace is::msg::common;
		using namespace is::msg::geometry;

		struct Robot{
				std::unique_ptr<R::Connection> conn;

				Robot(){
					//conexão com o servidor de banco de dados
					conn = R::connect("localhost", 28015);

					if (!conn) {
						std::cerr << "Could not connect to server\n";
						//return 1;
					}
					
					std::cout << "Connected" << std::endl;
					R::Cursor databases = R::db_list().run(*conn);
					std::string dbneurons = "neurons";
					bool hasdbneurons = false;
					for (R::Datum const& db : databases) {
						std::cout << *db.get_string() << '\n';
						if (dbneurons == *db.get_string()){
							hasdbneurons = true;
							std::cout << "There is a database called neurons" << '\n';
							break;
						}
					}
					if (!hasdbneurons){
						R::db_create(dbneurons).run(*conn);
						std::cout << "Database neurons created" << '\n';
					}
					//Recriar as tabelas pois o ambiente inteligente está sendo reiniciado
					//Será criado uma interface para o gerenciamento desses dispositivos
					try{
						R::db(dbneurons).table_drop("robots").run(*conn);
					}catch (RethinkDB::Error &err) {
						std::cout << err.message.c_str() << std::endl;
					}
					try{
						R::db(dbneurons).table_create("robots").run(*conn);
						R::db(dbneurons).table("robots").index_create("time").run(*conn);
					}catch (RethinkDB::Error &err) {
						std::cout << err.message.c_str() << std::endl;
					}
				};

				//função para recuperar os gateways dos robôs
				void getRobots(){
					R::Cursor cursor = R::db("neurons").table("robots").
					filter(R::row["serialport"] > 11).run(*conn);
					for (R::Datum& user : cursor) {
						printf("%s\n", user.as_json().c_str());
					}
				}


				//função para adicionar o gateway do robo
				void add(std::string const& name, std::string const& serialport, Speed const& speed){

					double x = speed.linear;
					std::cout << "Velocidade linear " + std::to_string(x) << std::endl;
					double y = speed.angular;
					std::cout << "Velocidade angular " + std::to_string(y) << std::endl;
					
					milliseconds ms = duration_cast< milliseconds >(
						system_clock::now().time_since_epoch()
					);
					long now = ms.count();
					std::cout << "porta serial:" << std::endl;
					std::cout << serialport << std::endl;

					std::cout << "adding at: " + std::to_string(now) << std::endl;
					try{
						R::db("neurons").table("robots").insert(
							R::json("{ \"name\": \""+ name +"\""
							+ ", \"serialport\":\""+ serialport + "\"" +
							", \"time\":" + std::to_string(now) +
							", \"max_linear_speed\":" + std::to_string(speed.linear) +
							", \"max_angular_speed\":" + std::to_string(speed.angular) + "}"
						)).run(*conn);
					}catch (RethinkDB::Error &err) {
						std::cout << err.message.c_str() << std::endl;
					}
				}

				//função para remover o gateway do robo

				//função para atualizar o gateway do robô

				//função para adicionar as instancias do gateway do robo

				//funções de busca inteligente
			
			};
	}
}
#endif //__IS_NEURONSCONTROLLER_ROBOT_HPP__

