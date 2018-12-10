COMPILER = g++
FLAGS = -std=c++14 -O3 -Wall -Werror -Wextra

SO_DEPS = $(shell pkg-config --libs --cflags libSimpleAmqpClient msgpack librabbitmq opencv)
SO_DEPS += -lboost_program_options -lpthread -ldl -lm -lrt

all: bb_chorando

clean:
	rm bb_chorando
	
bb_chorando: service.cpp 
	$(COMPILER) $^ -o $@ $(FLAGS) $(SO_DEPS)
