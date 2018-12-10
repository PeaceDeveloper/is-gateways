#ifndef __IS_DRIVER_PIONEER_HPP__
#define __IS_DRIVER_PIONEER_HPP__

#include <is/logger.hpp>
#include <is/msgs/common.hpp>
#include <is/msgs/geometry.hpp>
#include <is/msgs/robot.hpp>

#include <cmath>
#include <chrono>
#include <memory>
#include <string>

#include <atomic>
#include <condition_variable>
#include <mutex>
#include <thread>

#ifndef isnan
#if __cplusplus <= 199711L  // c++98 or older
  #define isnan(x) ::isnan(x)
#else
  #define isnan(x) std::isnan(x)
#endif
#endif

//#include <Aria/Aria.h>

namespace is {
namespace driver {

using namespace std::chrono;
using namespace is::msg::robot;
using namespace is::msg::common;
using namespace is::msg::geometry;

constexpr double pi() {
  return std::atan(1) * 4;
}
auto deg2rad = [](double deg) { return deg * (pi() / 180.0); };
auto rad2deg = [](double rad) { return rad * (180.0 / pi()); };

struct Robocop {
  //ArRobot robot;
  std::string* connection;

  Timestamp last_timestamp;
  std::atomic<int64_t> period_ms;
  std::atomic<int64_t> delay_ms;

  bool ready;
  std::mutex mutex;
  std::condition_variable condition;
  std::thread thread;
  std::atomic<bool> running;

  //gets and sets
  int port;
  int get_port(){
    return port;
  }
  void set_port(int Port){
    port = Port;
  }

  std::string serialport;
  std::string get_serial_port(){
    return serialport;  
  }
  void set_serial_port(std::string const& SerialPort){
    serialport = SerialPort;
  }

  std::string hostname;
  std::string get_host_name(){
    return hostname;  
  }
  void set_host_name(std::string const& Hostname){
    hostname = Hostname;
  }

  void set_max_speed(Speed const& speed) {
    double x = speed.linear;
    x++;
  }

  Speed get_max_speed() {
    double linear = 4.0;
    double angular = 1.0;
    return {linear, deg2rad(angular)};
  }


  //end gets and sets

  Robocop() : connection(nullptr) {}
  Robocop(std::string const& port) { connect(port); }
  Robocop(std::string const& hostname, int port) { connect(hostname, port); }

  Pose pose, poseTo;  
  int inc = 2;
  virtual ~Robocop() {
    running = false;
    thread.join();
    if (connection != nullptr) {
      delete connection;
    }
  }

  void connect(std::string const& port) {
    set_serial_port(port);
    initialize();
  }

  void connect(std::string const& hostname, int port) {
    std::string hstname = hostname;
    port = port;
    initialize();
  }

  void set_speed(Speed const& speed) {
    double x = speed.linear;
    x++;
  }

  Speed get_speed() {
    double linear = 2.0;
    double angular = 1.0;
    return {linear, deg2rad(angular)};
  }

  Pose get_pose() {
    
    if (pose.position.x == poseTo.position.x)
      inc = 0;      
    if (pose.position.x > poseTo.position.x)
      inc = -1;
    if (pose.position.x < poseTo.position.x)
      inc = 1;
    /*
    if (pose.position.x > 20)
      inc = -2;
    if (pose.position.x < -20)
      inc = 2;*/
    pose.position.x = pose.position.x + inc;
    pose.position.y = 0;
    pose.heading = deg2rad(2);
    //std::this_thread::sleep_for(std::chrono::milliseconds(1000));
    return pose;
  }
  
  Info get_info(std::string name){
    Info info;
    info.name = name;
    info.type = "robot";
    return info;
  }

  void set_pose(Pose const& p) {
    //robot.moveTo(ArPose(pose.position.x, pose.position.y, rad2deg(pose.heading)));
    std::cout << "setting pose" << std::endl;
    std::cout << p.position.x << std::endl;
    poseTo = p;
  }

  void set_sample_rate(SamplingRate const& rate) {
    if (rate.period) {
      period_ms = std::max<int64_t>(100, *rate.period);
    } else if (rate.rate) {
      period_ms = std::max<int64_t>(100, 1000.0 / *rate.rate);
    }
  }

  SamplingRate get_sample_rate() {
    SamplingRate rate;
    rate.period = period_ms;
    rate.rate = static_cast<double>(1000.0 / period_ms);
    return rate;
  }

  void set_delay(Delay const& delay) {
    if (delay.milliseconds <= period_ms) {
      delay_ms = delay.milliseconds;
    }
  }

  Timestamp get_last_timestamp() { return last_timestamp; }

  void wait() {
    std::unique_lock<std::mutex> lock(mutex);
    condition.wait(lock, [this] { return ready; });
    ready = false;
  }

 private:
  void initialize() {
    period_ms = 100;
    delay_ms = 399;
    pose.position.x = 2;
    pose.position.y = 0;
    pose.position.z = 0;    
    pose.heading = deg2rad(2);

    poseTo.position.x = 0;
    poseTo.position.y = 0;
    poseTo.position.z = 0;    
    poseTo.heading = deg2rad(0);

    auto loop = [this]() {
      running = true;
      while (running) {
        auto now = std::chrono::system_clock::now();
        last_timestamp = Timestamp();
        std::this_thread::sleep_until(now + milliseconds(period_ms + delay_ms));
        {
          std::unique_lock<std::mutex> lock(mutex);
          ready = true;
        }
        condition.notify_one();
      }
    };

    thread = std::thread(loop);
  }
};

}  // ::driver
}  // ::is

#endif  // __IS_DRIVER_PIONEER_HPP__
