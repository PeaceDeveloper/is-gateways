import com.labviros.is.Connection;
import com.labviros.is.Message;
import com.labviros.is.ServiceProvider;
import com.labviros.is.msgs.camera.CompressedImage;
import com.labviros.is.msgs.common.Info;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zamperini on 08/08/17.
 */
public class discoverEntities {
    private static final Logger log = LogManager.getLogger(discoverEntities.class.getName());

    private List<Info> infos;
    
    public Info addInfo(Info request) {
    	log.info("requesting a info " + request.getName());
    	infos.add(request);
        return request;
    }
    
    public Info rmInfo(Info request){
    	infos.remove(request);
    	return request;
    }
    
    public List<Info> getInfos(Message message){
    	return infos;
    }

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        final Connection connection = new Connection("amqp://guest:guest@localhost");
        final ServiceProvider<discoverEntities> provider = connection.advertise(new discoverEntities());       
    }
}
