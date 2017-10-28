import com.labviros.is.Connection;
import com.labviros.is.msgs.camera.CompressedImage;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Created by picoreti on 08/04/17.
 */
public class Publisher {
    private static final Logger log = LogManager.getLogger(Publisher.class.getName());

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        final Connection connection = new Connection("amqp://guest:guest@localhost");

        int i = 0;
        while (true) {
            CompressedImage image = new CompressedImage();
            image.setFormat(Integer.toString(i));
            image.getPropertiesBuilder().expiration("33");

            connection.publish("data", "webcam.frame", image);
            i++;
        }
    }
}
