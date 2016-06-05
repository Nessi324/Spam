package de.bht.fpa.mail.gruppe6.model.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Simone Strippgen
 */
public class Folder extends Component {

    private ArrayList<Email> emails;
    private final ArrayList<Component> content = new ArrayList<Component>();
    private boolean expandable;

    public Folder(File path, boolean expandable) {
        super(path);
        emails = new ArrayList<Email>();
        this.expandable = expandable;
    }

    @Override
    public void addComponent(Component comp) {
        content.add(comp);
    }

    @Override
    public List<Component> getComponents() {
        return content;
    }

    @Override
    public boolean isExpandable() {
        return expandable;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void addEmail(Email message) {
        emails.add(message);
    }
}
