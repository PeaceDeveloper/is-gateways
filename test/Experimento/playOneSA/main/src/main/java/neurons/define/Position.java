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
	
	private void PlayOne() {
		
	}
	
	private boolean NotFoundBetterMusicYet() {
		return false;
	}
	
	private void PlayTheBest() {
		
	}
	
	private void SaveMusic(long deactivationTimeStamp, long activationTimeStamp) {
		
	}
	
	
	@Override
	public void setActive() {
		super.setActive();
		try {
			if (this.NotFoundBetterMusicYet())
				this.PlayOne();
			else
				this.PlayTheBest();			
		} catch (Exception e) {			
			e.printStackTrace();
		}		
	}
	@Override
	public void setInactive() {
		super.setInactive();		
		try {
			this.SaveMusic(this.getDeactivation().getTimestamp(), this.getActivation().getTimestamp());
		} catch (Exception e) {			 
			e.printStackTrace();
		}		
	}		
}