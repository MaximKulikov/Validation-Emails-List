package ru.maximkulikov.validationemaillist;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Class with All Application constant in one place
 *
 * @author Maxim Kulikov
 * @since 24.06.2017
 */
public class C {
    public static final String MAIL_FROM = "mailFrom";

    public static final String MX_DOMAIN = "mxDomain";

    public static final String SUB_LIST  = "subList";

    public static final String UNSUB_LIST = "unsubList";

    public static final String WHITE_LIST = "whiteList";

    public static final String  Project_WEBSITE = "https://github.com/Trinion/Validation-Emails-List";

    public static void openBrowser(String link) {

        String os = System.getProperty("os.name").toLowerCase();


        if (os.indexOf("mac") >= 0) {

            Runtime rt = Runtime.getRuntime();
            try {
                rt.exec("open " + link);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {

            String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
                    "netscape", "opera", "links", "lynx"};

            StringBuffer cmd = new StringBuffer();
            for (int i = 0; i < browsers.length; i++)
                cmd.append((i == 0 ? "" : " || ") + browsers[i] + " \"" + link + "\" ");
            Runtime rt = Runtime.getRuntime();
            try {
                rt.exec(new String[]{"sh", "-c", cmd.toString()});
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (os.startsWith("windows")) {

            try {
                Desktop.getDesktop().browse(new URI(link));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

    }
}
