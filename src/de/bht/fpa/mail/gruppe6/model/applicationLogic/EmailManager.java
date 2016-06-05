package de.bht.fpa.mail.gruppe6.model.applicationLogic;

import de.bht.fpa.mail.gruppe6.model.data.Email;
import de.bht.fpa.mail.gruppe6.model.data.Folder;
import java.io.File;
import java.io.FileFilter;
import javax.xml.bind.JAXB;

/**
 *
 * @author Nessi
 */
public class EmailManager implements EmailManagerIF {

    public EmailManager() {
    }

    @Override
    public void loadEmails(Folder f) {
        if (f.getEmails().isEmpty()) {
            File file = new File(f.getPath());
            FileFilter filter = (File name) -> name.getName().endsWith(".xml");
            for (File x : file.listFiles(filter)) {
                Email email = JAXB.unmarshal(x, Email.class);
                if (!email.toString().contains("false")) {
                    f.addEmail(email);
                }
            }
        }
    }
}
