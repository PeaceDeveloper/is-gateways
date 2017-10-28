import com.labviros.is.Connection;
import com.labviros.is.Message;
import com.labviros.is.msgs.camera.CompressedImage;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by picoreti on 10/04/17.
 */
public class Subscriber {
    private static final Logger log = LogManager.getLogger(Subscriber.class.getName());

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        final Connection connection = new Connection("amqp://guest:guest@localhost");
        ArrayBlockingQueue<Message> queue = connection.subscribe("data", "webcam.frame");

        while (true) {
            Message message = queue.take();
            CompressedImage image = new CompressedImage(message);
            log.info(image.getFormat());
        }
    }
}
