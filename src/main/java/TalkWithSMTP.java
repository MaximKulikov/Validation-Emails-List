/**
 * Validation-List-of-Emails
 * Created by maxim on 6/18/2017.
 */
public class TalkWithSMTP implements Runnable {


    private Domain domain;

    public TalkWithSMTP(Domain domain) {

        this.domain = domain;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Thread " + domain.getName());

        System.out.println("Starting " + Thread.currentThread().getName());

        for (String email : domain.getEmails()) {

            Main.addFinalGoodEmailToFinalGoodLIstPlease(email);

        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Complite " + Thread.currentThread().getName());



    }
}
