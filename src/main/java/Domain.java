import java.util.ArrayList;
import java.util.List;
import javax.naming.directory.Attribute;

/**
 * Validation-List-of-Emails
 * Created by maxim on 6/18/2017.
 */
public class Domain {

    Attribute attr;
    private String name;
    private List<String> emails = new ArrayList();

    public String getName() {
        return name;
    }

    public Domain(String name) {

        this.name = name;
    }

    public void addEmails(String s) {
        this.emails.add(s);
    }

    public Attribute getAttr() {
        return attr;
    }

    public void setAttr(Attribute attr) {
        this.attr = attr;
    }

    public List<String> getEmails() {
        return emails;
    }
}
