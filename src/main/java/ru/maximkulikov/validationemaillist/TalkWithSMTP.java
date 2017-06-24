package ru.maximkulikov.validationemaillist;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;


/**
 * Класс делает запросы к SMTP серверу для проверки существования перечня почтовых ящиков
 *
 * @author Maxim Kulikov
 * @since 18.06.2017
 */
public class TalkWithSMTP implements Runnable {

    private static final int SERVER_PORT = 25;

    private PrintStream ps = null;

    private DataInputStream dis = null;

    private Domain domain;

    private List<List<String>> emails = new ArrayList();

    public TalkWithSMTP(Domain domain) {

        this.domain = domain;

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


        String HELO = "HELO " + Validator.property.getProperty(C.MX_DOMAIN);
        String MAIL_FROM = "MAIL FROM:<" + Validator.property.getProperty(C.MAIL_FROM) + ">";


        for (List<String> partOfemails : emails) {

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

                String openConnection = receive();

                send(HELO);
                sleep(5);
                String answerHelo = receive();

                send(MAIL_FROM);
                sleep(5);
                String answerMailFrom = receive();

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

        }

    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void send(String str) throws IOException {
        this.ps.println(str);
        this.ps.flush();
        //   System.out.println("Java sent: " + str);
    }

    public String receive() throws IOException {
        String readstr;
        readstr = this.dis.readLine();
        System.out.println("SMTP respons: " + readstr);
        return readstr;
    }

}
