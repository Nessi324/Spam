package de.bht.fpa.mail.gruppe6.model.applicationLogic;

import de.bht.fpa.mail.gruppe6.model.data.Email;
import de.bht.fpa.mail.gruppe6.model.data.Folder;
import java.io.File;
import java.util.List;

public interface ApplicationLogicIF {

    Folder getTopFolder();

    void loadContent(Folder folder);

    List<Email> search(String pattern);//List

    void loadEmails(Folder folder);

    void changeDirectory(File file);

    void saveEmails(File file);

}
