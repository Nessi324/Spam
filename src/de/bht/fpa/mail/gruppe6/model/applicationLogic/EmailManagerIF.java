package de.bht.fpa.mail.gruppe6.model.applicationLogic;

import de.bht.fpa.mail.gruppe6.model.data.Folder;
import java.io.File;

/**
 *
 * @author Nessi
 */
public interface EmailManagerIF {

    public void loadEmails(Folder f);
    public void saveEmails(File file);
}
