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
public class discoverEntitiesRequest {
    private static final Logger log = LogManager.getLogger(discoverEntitiesRequest.class.getName());
    
    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        final Connection connection = new Connection("amqp://guest:guest@localhost");
        
        
        Info info = new Info();
        info.setName("rob1");
        info.setType("robot");
        info.getPropertiesBuilder().expiration("3000");

        //add an info
        String id = connection.client().request("discover_entities.add_info", info);
        Message reply = connection.client().receiveDiscardOthers(id, 3, TimeUnit.SECONDS);
        

        if (reply != null) {
            Info replyInfo = new Info(reply);
            log.info(replyInfo.getName());
        } else {
            log.info("Reply timeout");
        }
    }
}
