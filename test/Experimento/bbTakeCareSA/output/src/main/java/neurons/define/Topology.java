package neurons.define;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import java.lang.String;
import neurons.core.AbstractNeuronsTopology;
import neurons.core.AbstractPlatform;
import neurons.core.IConnection;
import neurons.core.IInterNeuron;
import neurons.core.ISensorialNeuron;
import neurons.core.ServiceDelivery;
import neurons.platform.visiot.VisIoTConnection;
import neurons.stream.storm.NeuronsTopology;

public class Topology {
  public static void main(String[] args) {
    IConnection conn = new VisIoTConnection("/home/tiago/Documents/is-gateways/test/Experimento/bbTakeCareSA");
    AbstractPlatform p = conn.Map();;
    ServiceDelivery servicedelivery = p.getAnyServiceDelivery(thing -> thing.getName().equals("bbStatusService"));
    p.setDeviceInFocus(servicedelivery);
    AbstractNeuronsTopology builder = new NeuronsTopology();
    NeuronsTopology.platform = p;
    ISensorialNeuron s = new Spout();
    IInterNeuron i = new Linker();
    builder.newSynapse("synapse", s);
    builder.newInterNeuronGroup("inter", i).fieldsGrouping("synapse", new Fields("entityName"));
    Config config = new Config();
    config.setDebug(true);
    LocalCluster localCluster = new LocalCluster();
    localCluster.submitTopology("situation-aware-topology", config, builder.createNeurons());
    Utils.sleep(600000);
    localCluster.shutdown();
  }
}
