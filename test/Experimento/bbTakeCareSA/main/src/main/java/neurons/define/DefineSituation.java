package neurons.define;
import com.labviros.is.msgs.common.Status;
import neurons.core.AbstractResource;
import neurons.core.ServiceDelivery;
import neurons.platform.visiot.StatusResource;
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
		situationAware.setName("TakeCare");
		situationAware.setMotorNeuron(Position.class);
		Fact<Status, ServiceDelivery, StatusResource> fact = 		
		new Fact<Status, ServiceDelivery, StatusResource>("f1", AbstractResource.class,
		 pathToProject);
		fact.setWhenClause(service -> service.getValue() == "Cry" 
				|| service.getValue() == "Alone" , isWriteToFile);
		fact.setEntity(ServiceDelivery.class);
		fact.setResource(StatusResource.class);
		fact.setProperty(Status.class);
		fact.setFilterThing("bbStatusService");
		situationAware.addFact(fact);		
		return situationAware;
	}
}