import com.labviros.is.Connection;
import com.labviros.is.Message;
import com.labviros.is.ServiceProvider;
import com.labviros.is.msgs.camera.CompressedImage;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Created by picoreti on 09/04/17.
 */
public class ServiceExample {
    private static final Logger log = LogManager.getLogger(ServiceExample.class.getName());

    public CompressedImage doNothing(CompressedImage request) {
        return request;
    }

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        final Connection connection = new Connection("amqp://guest:guest@localhost");
        final ServiceProvider<ServiceExample> provider = connection.advertise(new ServiceExample());

        CompressedImage image = new CompressedImage();
        image.setFormat("format");
        image.getPropertiesBuilder().expiration("3000");

        String id = connection.client().request("service_example.do_nothing", image);
        Message reply = connection.client().receiveDiscardOthers(id, 3, TimeUnit.SECONDS);

        if (reply != null) {
            CompressedImage replyImage = new CompressedImage(reply);
            log.info(replyImage.getFormat());
        } else {
            log.info("Reply timeout");
        }

    }
}
