package neurons.define;
import com.labviros.is.msgs.robot.Pose;
import neurons.core.AbstractResource;
import neurons.core.Robot;
import neurons.platform.visiot.PoseResource;
import neurons.sa.scene.Fact;
import neurons.sa.scene.IDefineSAServices;
import neurons.sa.scene.SituationAware;

public class DefineSituation implements IDefineSAServices{	 
	
	public static void main(String[] args) {
		new DefineSituation().on(args[0], true);
	}
	
	@Override
	public IDefineSAServices getThis(){
		return new DefineSituation();
	}

	@Override
	public SituationAware on(String pathToProject, Boolean isWriteToFile) {
		SituationAware situationAware = new SituationAware(this);
		situationAware.setName("PlayOne");
		situationAware.setMotorNeuron(Position.class);		
		Fact<Pose, Robot, PoseResource> fact1 = 
				new Fact<Pose, Robot, PoseResource>("f1", 
						AbstractResource.class, pathToProject);
		fact1.setWhenClause(robot -> robot.getPosition().getX() == 10.0, isWriteToFile);		
		fact1.setEntity(Robot.class);
		fact1.setResource(PoseResource.class);
		fact1.setProperty(Pose.class);
		fact1.setFilterThing("rob1");
		situationAware.addFact(fact1);		
		return situationAware;
	}
}