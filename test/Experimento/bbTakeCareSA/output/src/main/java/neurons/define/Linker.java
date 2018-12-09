package neurons.define;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.tuple.Tuple;
import com.labviros.is.Message;
import com.labviros.is.msgs.common.Status;
import java.lang.Exception;
import java.lang.Throwable;
import neurons.core.AbstractNeuronsTopology;
import neurons.core.ServiceDelivery;
import neurons.stream.storm.InterNeuron;
import org.drools.RuntimeDroolsException;

public class Linker extends InterNeuron {
  Linker() {
    super.drlResource = "neurons/define/TakeCareBBService.drl";
  }

  public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
    try {
      Message ms = (Message) tuple.getValueByField("resource");
      Status status = new Status(ms);
      ServiceDelivery servicedelivery = (ServiceDelivery) AbstractNeuronsTopology.platform.getDeviceInFocus();
      servicedelivery.setStatus(status);
      try {
        super.UpdateFact(servicedelivery);
      } catch (Exception e) {
        super.AddFact(servicedelivery);
      }
    } catch (Throwable t) {
      t.printStackTrace();
      throw new RuntimeDroolsException(t.getMessage());
    }
  }
}
