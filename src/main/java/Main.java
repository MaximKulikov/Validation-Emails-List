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

    private static String PROCESS_FILE = "InEmails.txt";

    private static String UNSUBSCRIBED_FILE = "UnSubEmails.txt";

    private static int NUMBER_OF_PROCESSORS = Integer.parseInt(System.getenv("NUMBER_OF_PROCESSORS"));

    private static ExecutorService service;


    //Список хороших адресов, после обработки
    private static List<String> finalGoodEmails = new ArrayList<>();

    //Список адресов, с которыми происходит магия
    private Map<String, Domain> inEmails = new HashMap<>();

    //Список неадекватных адресов
    private Set<String> blackEmailsSet = new HashSet<>();

    //Список задач на обработку доменов
    private List<Future> futureList;

    //Список отписанных адресов
    private List<String> unsubscriberEmails = new ArrayList<>();

    public static void main(String[] args) {


        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        service = Executors.newFixedThreadPool(NUMBER_OF_PROCESSORS * 4);

        try {
            PROCESS_FILE = URLDecoder.decode(path, "UTF-8");
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

    private void addInMap(String email) throws ArrayIndexOutOfBoundsException {


        String[] temp = email.split("@");
        String domain = temp[1];

        if (!inEmails.containsKey(domain)) {

            inEmails.put(domain, new Domain(domain));

        }

        inEmails.get(domain).addEmails(email);

    }

    private File createNewFile(String path) {


        File goodFile = new File(path);

        try {
            boolean created = goodFile.createNewFile();
            System.out.println(created);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return goodFile;
    }

    private boolean doesMatchPattern(String line) {

        return line.contains("@");
    }

    private void execute() {

        //Получаем доступ к файлу


        Set<String> processSet = loadEmailsToProcess(new File(PROCESS_FILE));

        Set<String> unsubccribedList = loadEmailsToProcess(new File(UNSUBSCRIBED_FILE));

        processSet.removeAll(unsubccribedList);

        //Простая проверка паттерна адреса
        filterListFirstStage(processSet);

        //Проверка домена на существование MX
        futureList = filterListSecondStage();

        // Проверить домены на валидность


        //Ждем завершения третей стадии проверки

        while (futureList.size() > 0) {
            System.out.println("Size is " + futureList.size());
            if (futureList.get(0).isDone()) {
                futureList.remove(0);
            }

        }

        //У нас сформированы оба списка с адресами

        System.out.println("End of PROGRAM?");

        // Сохраняем хороший список
        Date d = new Date();
        File goodFile = createNewFile("GoodEmails_" + d.getTime() + ".txt");
        File badFile = createNewFile("BadEmails_" + d.getTime() + ".txt");

        ArrayList<String> list = new ArrayList<>();
        list.addAll(blackEmailsSet);

        saveAllGoodToFile(goodFile, finalGoodEmails);
        saveAllGoodToFile(badFile, list);


        System.out.println("is it now end?");
        System.exit(0);


    }

    private List<Future> filterListSecondStage() {
List<Future> futureList = new ArrayList<>();

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
                        addBlackEmail(email);
                    }


                }

            } catch (NamingException e) {

            }
        }
        return futureList;
    }

    private void filterListFirstStage(Set<String> processSet) {

        for (String s : processSet) {
            if (doesMatchPattern(s)) {
                try {
                    addInMap(s);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Exception " + s);
                }
            } else {
                addBlackEmail(s);
            }
        }
    }

    private Set<String> loadEmailsToProcess(File file) {

        Set<String> temp = new HashSet<>();

        try (FileReader fis = new FileReader(file);
             BufferedReader br = new BufferedReader(fis)) {


            String line = "";

            while ((line = br.readLine()) != null) {

                temp.add(line.trim());

            }

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return temp;
    }

    private void loadUnsubscruberList() {

    }


    private void saveAllGoodToFile(File fileName, List<String> list) {

        try (FileWriter out = new FileWriter(fileName)
             ; BufferedWriter bw = new BufferedWriter(out)) {

            for (String email : list) {
                bw.write(email);
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
