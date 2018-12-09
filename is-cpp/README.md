Neurons Framework Experiment
===================

Situation-aware Neurons Service on IoT messaging middleware C++. 

Installing Dependencies
-----------------
On linux run the **install** script to install all the dependencies. 
**(!Tested only on Ubuntu 14.04 and 16.04!)**.

```shell
cd scripts
chmod 755 install
./install
```

Using the library
-----------------
Everything is header only, just include the headers in your project, for instance if
you copy the files to **/usr/local/include/is** (this is done automatically by the install script).

```c++
#include <is/is.hpp>
// Everything will be under the is namespace
```
Will include all the necessary files to use the library.

The messaging layer is implemented using the the 
[amqp 0.9.1](https://www.rabbitmq.com/specification.html) protocol, 
requiring a broker to work. We recommend using [RabbitMQ](https://www.rabbitmq.com/).

The broker can be easily instantiated with [Docker](https://www.docker.com/) with the following command:
```c++
docker run -d -m 512M -p 15672:15672 -p 5672:5672 picoreti/rabbitmq:latest
```
To install docker run: 
```shell
curl -sSL https://get.docker.com/ | sh
```

Using the Neurons Framework
-----------------

Sample application that consumes the sample Neurons service
-----------------
```shell
cd ../../test/sa-service
./service
```

Sample Neurons Service
-----------------
```shell
cd ../../gateways/neuronsController
rethinkdb
cd ../robocop
./robocop -e rob1 -s 21
cd ../../test/neurons_framework/neurons-cli
mvn package
cd target
java -jar neurons.jar -s [ProjectName]
java -jar neurons.jar -b [ProjectName]
cd [ProjectName]/output/target
java -jar output-0.0.1-SNAPSHOT
```
