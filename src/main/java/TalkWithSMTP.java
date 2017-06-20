import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.naming.NamingException;

/**
 * Validation-List-of-Emails
 * Created by maxim on 6/18/2017.
 */
public class TalkWithSMTP implements Runnable {

    private static final int SERVER_PORT = 25;

    private Domain domain;

    public TalkWithSMTP(Domain domain) {

        this.domain = domain;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Thread " + domain.getName());


        System.out.println("Starting " + Thread.currentThread().getName());

        String[] attr = new String[1];
        try {

            attr = domain.getAttr().get().toString().split(" ");
        } catch (NamingException e) {
            e.printStackTrace();
        }

        try (Socket socket = new Socket(attr[1], SERVER_PORT)) {
            {


                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                oos.writeUTF("hello");


                String answer = ois.readUTF();
                System.out.println(answer);




            }
        } catch (IOException e) {

        }



        for (String email : domain.getEmails()) {

            Main.addFinalGoodEmailToFinalGoodLIstPlease(email);

        }
/*

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/

        // System.out.println("Complite " + Thread.currentThread().getName());



    }
}
