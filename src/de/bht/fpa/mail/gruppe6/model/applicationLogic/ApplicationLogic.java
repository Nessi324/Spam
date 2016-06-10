
package de.bht.fpa.mail.gruppe6.model.applicationLogic;

import de.bht.fpa.mail.gruppe6.model.data.Email;
import de.bht.fpa.mail.gruppe6.model.data.Folder;
import java.io.File;
import javafx.collections.ObservableList;

public class ApplicationLogic implements ApplicationLogicIF {

    private FolderManager folder;
    private EmailManager mails;

    public ApplicationLogic(File file) {
        folder = new FolderManager(file);
        mails = new EmailManager();
    }

    @Override
    public Folder getTopFolder() {
        return folder.getTopFolder();
    }

    @Override
    public void loadContent(Folder newfolder) {
        folder.loadContent(newfolder);
    }

    @Override
    public ObservableList<Email> search(String pattern) {
        return mails.search(pattern);
    }

    @Override
    public void loadEmails(Folder folder) {
        mails.loadEmails(folder);
    }

    @Override
    public void changeDirectory(File file) {
    folder.changeDirectory(file);
    }
    
    @Override
    public void saveEmails(File file) {
        mails.saveEmails(file);
    }

}
