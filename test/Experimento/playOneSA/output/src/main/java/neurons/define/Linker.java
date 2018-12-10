package neurons.define;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.tuple.Tuple;
import com.labviros.is.Message;
import com.labviros.is.msgs.robot.Pose;
import java.lang.Exception;
import java.lang.Throwable;
import neurons.core.AbstractNeuronsTopology;
import neurons.core.Robot;
import neurons.stream.storm.InterNeuron;
import org.drools.RuntimeDroolsException;

public class Linker extends InterNeuron {
  Linker() {
    super.drlResource = "neurons/define/PlayOne.drl";
  }

  public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
    try {
      Message ms = (Message) tuple.getValueByField("resource");
      Pose pose = new Pose(ms);
      Robot robot = (Robot) AbstractNeuronsTopology.platform.getDeviceInFocus();
      robot.setPose(pose);
      try {
        super.UpdateFact(robot);
      } catch (Exception e) {
        super.AddFact(robot);
      }
    } catch (Throwable t) {
      t.printStackTrace();
      throw new RuntimeDroolsException(t.getMessage());
    }
  }
}
