package neurons.define;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import java.io.IOException;
import java.lang.Exception;
import java.lang.InterruptedException;
import java.util.Map;
import neurons.core.AbstractNeuronsTopology;
import neurons.core.Robot;
import neurons.platform.visiot.PoseResource;
import neurons.stream.storm.SensorialNeuron;
import org.apache.log4j.BasicConfigurator;

public class Spout extends SensorialNeuron {
  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    BasicConfigurator.configure();
    super.outputCollector = collector;
    try {
      Robot robot = (Robot) AbstractNeuronsTopology.platform.getDeviceInFocus();
      PoseResource poseresource = (PoseResource) robot.getPoseResource();
      super.configure(poseresource.configure());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void nextTuple() {
    try {
      Robot robot = (Robot) AbstractNeuronsTopology.platform.getDeviceInFocus();
      super.emit(robot.getName());
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    outputFieldsDeclarer.declare(new Fields("entityName", "resource"));
  }
}
