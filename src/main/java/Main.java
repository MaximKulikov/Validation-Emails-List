import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * Validation-List-of-Emails
 *
 * @author Maxim Kulikov
 * @since 17.06.2017
 */
public class Main {

    private static final String EMAIL_PATTERN = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+";

    private static final String SIMPLE_PATTERN = "@{1}";
    private static String DECODEDPATH = "InEmails.txt";
    private static ExecutorService service = Executors.newFixedThreadPool(Integer.parseInt(System.getenv("NUMBER_OF_PROCESSORS")));
    private static List<String> finalGoodEmails = new ArrayList<>();
    private Map<String, Domain> inEmails = new HashMap<>();
    private Set<String> blackEmailsSet = new HashSet<>();
    private List<Future> futureList = new ArrayList<>();

    public static void main(String[] args) {

        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            DECODEDPATH = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Main mail = new Main();
        mail.execute();
    }

    static Attribute doLookup(String hostName) throws NamingException {
        Hashtable env = new Hashtable();
        env.put("java.naming.factory.initial",
                "com.sun.jndi.dns.DnsContextFactory");
        DirContext ictx = new InitialDirContext(env);
        Attributes attrs =
                ictx.getAttributes(hostName, new String[]{"MX"});
        Attribute attr = attrs.get("MX");
        if (attr == null) return (null);
        return (attr);
    }

    public static synchronized void addFinalGoodEmailToFinalGoodLIstPlease(String email) {
        finalGoodEmails.add(email);
    }

    public void addBlackEmail(String s) {
        this.blackEmailsSet.add(s);
    }

    private void addInMap(String email) {


        String[] temp = email.split("@");
        String domain = temp[1];

        if (!inEmails.containsKey(domain)) {

            inEmails.put(domain, new Domain(domain));

        }

        inEmails.get(domain).addEmails(email);

    }

    private File createNewFile() {

        Date d = new Date();
        File goodFile = new File("GoodEmails_" + d.getTime() + ".txt");

        try {
            boolean created = goodFile.createNewFile();
            System.out.println(created);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return goodFile;
    }

    private boolean doesContainAt(String line) {

        return line.contains("@");
    }

    private void execute() {

        //Получаем доступ к файлу


        File file = new File("inEmails.txt");

        Set<String> tempSet = new HashSet<>();

        try (FileReader fis = new FileReader(file);
             BufferedReader br = new BufferedReader(fis)) {


            String line = "";

            while ((line = br.readLine()) != null) {

                tempSet.add(line.trim());

            }

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
//TODO Удалить из сета уже известные плохие адреса

        for (String s : tempSet) {

            if (doesContainAt(s)) {

                addInMap(s);

            } else {

                addBlackEmail(s);

            }

        }

        // Проверить домены на валидность
        for (String domain : inEmails.keySet()) {
            try {
                Attribute attributes = doLookup(domain);

                if (attributes != null) {
                    inEmails.get(domain).setAttr(attributes);

                    // Отправляем на проверку адреса текущего домена
                    Future f = service.submit(new TalkWithSMTP(inEmails.get(domain)));

                    futureList.add(f);


                } else {

                    for (String email : inEmails.get(domain).getEmails()) {
                        blackEmailsSet.add(email);
                    }


                }

            } catch (NamingException e) {

            }
        }

        //Ждем завершения всех заданий

        while (futureList.size() > 0) {
            if (futureList.get(0).isDone()) {
                futureList.remove(0);
            }
        }


        //У нас сформированы оба списка с адресами

        System.out.println("End of PROGRAM?");

        // Сохраняем хороший список

        File fileName = createNewFile();

        saveAllToFile(fileName);

        System.out.println("is it now end?");



    }

    private void saveAllToFile(File fileName) {

        try (FileWriter out = new FileWriter(fileName)
             ; BufferedWriter bw = new BufferedWriter(out)) {

            for (String finalGoodEmail : finalGoodEmails) {
                bw.write(finalGoodEmail);
                bw.newLine();
            }
            bw.flush();
            bw.close();
            out.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
