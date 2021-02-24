package ru.maximkulikov.validationemaillist;

import java.util.ArrayList;
import java.util.List;
import javax.naming.directory.Attribute;

/**
 * Contain data about each domain
 *
 * @author Maxim Kulikov
 * @since 18.06.2017
 */
public class Domain {

    private Attribute attr;

    private String name;

    private List<String> emails = new ArrayList();

    public String getName() {
        return name;
    }

    /**
     * @param name Domain name
     */
    public Domain(String name) {

        this.name = name;
    }

    /**
     * Add associated with current domain email to list
     *
     * @param email
     */
    public void addEmails(String email) {
        this.emails.add(email);
    }

    /**
     * @return MX records from domain lookup
     */
    public Attribute getAttr() {
        return attr;
    }

    /**
     * @param attr MX records from domain lookup
     */
    public void setAttr(Attribute attr) {
        this.attr = attr;
    }

    /**
     * @return List of all emails from current domain
     */
    public List<String> getEmails() {
        return emails;
    }
}
