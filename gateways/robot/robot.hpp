#ifndef __IS_GW_ROBOT_HPP__
#define __IS_GW_ROBOT_HPP__

#include <is/is.hpp>
#include <is/msgs/common.hpp>
#include <is/msgs/robot.hpp>
#include "../neuronsController/robot.hpp"

namespace is {
namespace gw {

using namespace is::msg::common;
using namespace is::msg::robot;

template <typename ThreadSafeRobotDriver>
struct Robot {
  is::Connection is;

  Robot(std::string const& name, std::string const& uri, ThreadSafeRobotDriver & robot) : is(is::connect(uri)) {
    // clang-format off
    
    //auto as = is::Connection(uri, "metadata");
    //auto client = is::ServiceClient(as.channel, "metadata");
    //client.request("device.add_info", is::msgpack(robot.get_info(name)));

    is::neuronscontroller::Robot robcont;
    robcont.add(name, robot.getSerialPort());

    auto thread = is::advertise(uri, name, {
      {
        "set_speed", [&](is::Request request) -> is::Reply {
          robot.set_speed(is::msgpack<Speed>(request));
          return is::msgpack(status::ok);
        }
      },
      {
        "get_speed", [&](is::Request) -> is::Reply {
          return is::msgpack(robot.get_speed());
        }
      },
      {
        "set_pose", [&](is::Request request) -> is::Reply {
          robot.set_pose(is::msgpack<Pose>(request));
          return is::msgpack(status::ok);
        }
      },
      {
        "set_sample_rate", [&](is::Request request) -> is::Reply {
          robot.set_sample_rate(is::msgpack<SamplingRate>(request));
          return is::msgpack(status::ok);
        }
      },
      {
        "get_sample_rate", [&](is::Request) -> is::Reply {
          return is::msgpack(robot.get_sample_rate());
      }
      },
      {
        "get_info", [&](is::Request) -> is::Reply{
          return is::msgpack(robot.get_info(name));
        }
      }

    });
    // clang-format on

    
    while (1) {
      robot.wait();
      is.publish(name + ".pose", is::msgpack(robot.get_pose()));
      is.publish(name + ".timestamp", is::msgpack(robot.get_last_timestamp()));
    }

    thread.join();
  }
};

}  // ::gw
}  // ::is

#endif  // __IS_GW_ROBOT_HPP__
