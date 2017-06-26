package ru.maximkulikov.validationemaillist;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingException;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;


/**
 * Класс делает запросы к SMTP серверу для проверки существования перечня почтовых ящиков
 *
 * @author Maxim Kulikov
 * @since 18.06.2017
 */
public class TalkWithSMTP implements Runnable {

    private static final int SERVER_PORT = 25;
    private HBox hbox;
    private ProgressBar midprogress;
    private PrintStream ps = null;
    private DataInputStream dis = null;
    private Domain domain;
    private List<List<String>> emails = new ArrayList();

    public TalkWithSMTP(Domain domain) {

        this.domain = domain;
        hbox = new HBox();

        midprogress = new ProgressBar();
        midprogress.setProgress(0.0);

        List<String> inside = new ArrayList<>();
        int count = 0;
        for (String email : domain.getEmails()) {
            if (count == 10) {
                count = 0;
                this.emails.add(new ArrayList<>(inside));
                inside.clear();
            }

            inside.add(email);
            count++;
        }
    }

    public String receive() throws IOException {
        String readstr;
        readstr = this.dis.readLine();
     //   System.out.println("SMTP respons: " + readstr);
        return readstr;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Thread " + domain.getName());


        System.out.println("Starting " + Thread.currentThread().getName());

        if (Validator.gui != null) {

            Platform.runLater(() -> {

                Label l = new Label();
                l.setText(domain.getName());
                l.setMinWidth(50.0);
                l.setPrefWidth(50.0);
                l.setMaxWidth(50.0);

                hbox.getChildren().add(l);
                hbox.getChildren().add(midprogress);

                Validator.gui.getVbProgress().getChildren().add(midprogress);
            });


        }

        String[] attr = new String[1];
        try {

            attr = domain.getAttr().get().toString().split(" ");
        } catch (NamingException e) {
            e.printStackTrace();
        }


        String HELO = "HELO " + Validator.property.getProperty(C.MX_DOMAIN);
        String MAIL_FROM = "MAIL FROM:<" + Validator.property.getProperty(C.MAIL_FROM) + ">";

        double count = 0.0;

        for (List<String> partOfemails : emails) {

            if (Validator.gui != null) {
                double finalCount = count;
                Platform.runLater(() -> midprogress.setProgress(finalCount / emails.size()));
            }

            Socket smtp = null;

            try {
                smtp = new Socket(attr[1], SERVER_PORT);
                OutputStream os = smtp.getOutputStream();
                ps = new PrintStream(os);
                InputStream is = smtp.getInputStream();
                dis = new DataInputStream(is);
            } catch (IOException e) {
                System.out.println("Error connection: " + e);
            }

            try {

                receive();

                send(HELO);
                sleep(5);
                receive();

                send(MAIL_FROM);
                sleep(5);
                receive();

                for (String email : partOfemails) {

                    send("RCPT TO:<" + email + ">");
                    sleep(200);
                    String answer = receive();

                    if (answer.startsWith("250 ")) {
                        Validator.addFinalGoodEmailToFinalGoodListPlease(email);
                    } else {
                        System.out.println("NOT 250: " + email + " " + answer);
                        Validator.addFinalBadEmailtoFinalBadListPlease(email + ";" + answer);
                    }
                }

                smtp.close();
            } catch (IOException e) {
                System.out.println("Error sending: " + e);
            }
            count++;
        }

        if (Validator.gui != null) {
            System.out.println("......removing hbox" + domain.getName());

            Platform.runLater(() -> {
                hbox.getChildren().clear();
                Validator.gui.getVbProgress().getChildren().remove(hbox);
            });
        }

    }

    public void send(String str) throws IOException {
        this.ps.println(str);
        this.ps.flush();
        //   System.out.println("Java sent: " + str);
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
