package neurons.define;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import com.labviros.is.Message;
import com.labviros.is.msgs.common.Status;
import br.ufes.inf.lprm.situation.Role;
import neurons.core.AbstractNeuronsTopology;
import neurons.core.Robot;
import neurons.sa.scene.MotorNeuron;

//@Publish(host="host", port=4040)
public class Position extends MotorNeuron {
	
	public Position() {
		super();
		AbstractNeuronsTopology.platform.getConn().assertExchange("sa-data");
	}
	
	@Role(label = "f1")
	private Robot robot;

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public Robot getRobot() {
		return robot;
	}
	@Override
	public void setActive() {
		super.setActive();
		try {
			//publicar status da posição do robô
			Status status = new Status();
			status.setValue("Ativado");
			status.setWhy(this.robot.getName() + " alcançou a coordenada x = " 
			+ this.robot.getPose().getPosition().getX());
			AbstractNeuronsTopology.platform.getConn().publishServiceDelivery("robotAtPosition", status);						
			
			//alterar a pose do robô
			Robot robot = (Robot) AbstractNeuronsTopology.platform.getDeviceInFocus();
			Message reply = robot.sendPose(1, 1, 1, 2);
			Status s = new Status(reply);
			System.out.println("Resposta do sendpose " + s.getValue());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(robot.getName() + ": at position activated at " + new SimpleDateFormat("H:mm:ss").format(  new Date( this.getActivation().getTimestamp() ) ) );		
	}
	@Override
	public void setInactive() {
		super.setInactive();
		Status status = new Status();
		status.setValue("Desativado");
		try {
			status.setWhy(this.robot.getName() + " alcançou a coordenada x = " 
			+ this.robot.getPose().getPosition().getX());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			AbstractNeuronsTopology.platform.getConn().publish("sa-data", "robotAtPosition.info", status);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(robot.getName() + ": at position deactivated at " + new SimpleDateFormat("H:mm:ss").format(  new Date( this.getDeactivation().getTimestamp() ) )  + ". It lasted: " + TimeUnit.MILLISECONDS.toSeconds( (this.getDeactivation().getTimestamp() - this.getActivation().getTimestamp()) ) + " seconds" );		
	}	
}