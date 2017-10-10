#ifndef __IS_NEURONSCONTROLLER_ROBOT_HPP__
#define __IS_NEURONSCONTROLLER_ROBOT_HPP__

#include <iostream>
#include <memory>
#include <cstdio>
#include <rethinkdb.h>

namespace is{
	namespace neuronscontroller{

		namespace R = RethinkDB;

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

				//funcção para recuperar os gateways dos robôs

				//função para adicionar o gateway do robo
				void add(std::string const& name){

					R::db("neurons").table("robots").insert(
						R::json("{\"id\": 1,\"serialport\": 12, \"name\": \" "+ name +" \"}")).run(*conn);
					
					R::Cursor cursor = R::db("neurons").table("robots").
						filter(R::row["serialport"] > 11).run(*conn);
					for (R::Datum& user : cursor) {
					printf("%s\n", user.as_json().c_str());
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

