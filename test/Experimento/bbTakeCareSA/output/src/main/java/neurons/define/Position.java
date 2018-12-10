package neurons.define;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import com.labviros.is.Message;
import com.labviros.is.msgs.common.Status;
import br.ufes.inf.lprm.situation.Role;
import neurons.core.AbstractNeuronsTopology;
import neurons.core.Robot;
import neurons.core.ServiceDelivery;
import neurons.sa.scene.MotorNeuron;

//@Publish(host="host", port=4040)
public class Position extends MotorNeuron {
	
	public Position() {
		super();
		AbstractNeuronsTopology.platform.getConn().assertExchange("sa-data");
	}
	
	@Role(label = "f1")
	private ServiceDelivery serviceDelivery;

	public void setServiceDelivery(ServiceDelivery serviceDelivery) {
		this.serviceDelivery = serviceDelivery;
	}

	public ServiceDelivery getServiceDelivery() {
		return serviceDelivery;
	}	
	
	@Override
	public void setActive() {
		super.setActive();
		Robot robot = null;
		try {			
			robot = (Robot)AbstractNeuronsTopology.platform
					.getAnyRobot(null);
			robot.sendPose(10, 1, 1, 2);
			
		} catch (Exception e) {			
			e.printStackTrace();
		}		
	}
	@Override
	public void setInactive() {
		super.setInactive();
		Robot robot = null;
		try {			
			robot = (Robot)AbstractNeuronsTopology.platform
					.getAnyRobot(null);
			robot.sendPose(0, 2, 2, 2);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}	
}