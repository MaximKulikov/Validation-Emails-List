package ru.maximkulikov.validationemaillist;

import java.io.*;
import java.util.*;
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
public class Validator {

    public static final Properties property = new Properties();

    private static int NUMBER_OF_PROCESSORS = Integer.parseInt(System.getenv("NUMBER_OF_PROCESSORS"));

    private static ExecutorService service;


    //Список хороших адресов, после обработки
    private static List<String> finalGoodEmails = new ArrayList<>();

    //Список адресов, с которыми происходит магия
    private Map<String, Domain> inEmails = new HashMap<>();

    //Список неадекватных адресов
    private static Set<String> blackEmailsSet = new HashSet<>();

    //Список задач на обработку доменов
    private List<Future> futureList;

    //Список отписанных адресов
    private List<String> unsubscriberEmails = new ArrayList<>();

    public static void main(String[] args) {

        System.setProperty("java.net.preferIPv4Stack", "true");
        service = Executors.newFixedThreadPool(NUMBER_OF_PROCESSORS);

        loadCongig();

        new Validator().execute();

    }

    private static void loadCongig() {
        FileInputStream fis;


        try {
            fis = new FileInputStream("config.properties");
            property.load(fis);

        } catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует!");
        }
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

    public static synchronized void addFinalGoodEmailToFinalGoodListPlease(String email) {

        finalGoodEmails.add(email);
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


        Set<String> processSet = loadEmailsToProcess(new File(property.getProperty("subList")));

        Set<String> unsubscribedList = loadEmailsToProcess(new File(property.getProperty("unsubList")));

        processSet.removeAll(unsubscribedList);

        //Простая проверка паттерна адреса
        filterListFirstStage(processSet);

        //Проверка домена на существование MX
        futureList = filterListSecondStage();


        while (futureList.size() > 0) {
            //     System.out.println("Size is " + futureList.size());
            if (futureList.get(0).isDone()) {
                futureList.remove(0);
            }

        }

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

                    Future f = service.submit(new TalkWithSMTP(inEmails.get(domain)));

                    futureList.add(f);

                } else {

                    for (String email : inEmails.get(domain).getEmails()) {
                        addFinalBadEmailtoFinalBadListPlease(email + ";" + "440 Почтового сервера не существует");
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
                addFinalBadEmailtoFinalBadListPlease(s + ";" + "440 Не является адресом электронной почты");
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
            System.exit(1);
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

    public static synchronized void addFinalBadEmailtoFinalBadListPlease(String email) {
        blackEmailsSet.add(email);
    }
}
