#ifndef __IS_NEURONSCONTROLLER_ROBOT_HPP__
#define __IS_NEURONSCONTROLLER_ROBOT_HPP__

#include <iostream>
#include <memory>
#include <cstdio>
#include <rethinkdb.h>
#include <chrono>

namespace is{
	namespace neuronscontroller{

		namespace R = RethinkDB;
		using namespace std::chrono;

		struct Robot{
				std::unique_ptr<R::Connection> conn;

				Robot(){
					conn = R::connect("localhost", 28015);
					if (!conn) {
						std::cerr << "Could not connect to server\n";
						//return 1;
						}
					
						std::cout << "Connected" << std::endl;
						R::Cursor databases = R::db_list().run(*conn);
						for (R::Datum const& db : databases) {
							std::cout << *db.get_string() << '\n';
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
				void add(std::string const& name, std::string const& serialport){
					
					milliseconds ms = duration_cast< milliseconds >(
						system_clock::now().time_since_epoch()
					);
					long now = ms.count();
					std::cout << "porta serial:" << std::endl;
					std::cout << serialport << std::endl;

					std::cout << "adding at: " + std::to_string(now) << std::endl;
					try{
						R::db("neurons").table("robots").insert(
							R::json("{ \"serialport\":\""+ serialport + "\"" +
							", \"time\":" + std::to_string(now) +
							", \"name\": \""+ name +"\" }")).run(*conn);
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

