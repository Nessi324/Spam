package de.bht.fpa.mail.gruppe6.model.applicationLogic;

import de.bht.fpa.mail.gruppe6.controller.AppController;
import de.bht.fpa.mail.gruppe6.model.data.Email;
import de.bht.fpa.mail.gruppe6.model.data.Folder;
import java.io.File;
import java.io.FileFilter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author Nessi
 */
public class EmailManager implements EmailManagerIF{
    
    private static ObservableList<Email> ersatz;
    
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
    @Override
       public void saveEmails(File file) {
        try {
            
            JAXBContext context = JAXBContext.newInstance(Email.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ObservableList<Email> tableinfo = AppController.getTableinfo();
            if (tableinfo != null && tableinfo.size() > 0 && file != null) {
                int times = 1;
                for (Email x : tableinfo) {
                    File newpath = new File(file.getPath() + "\\email" + times + ".xml");
                    m.marshal(x, newpath);
                    times = times + 1;
                }
            }
        } catch (JAXBException ex) {
            Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @Override
       public ObservableList<Email> search(String pattern) {
        ersatz = FXCollections.observableArrayList();
        for (Email x : AppController.tableinfo) {
            if (x.toString().toLowerCase().contains(pattern) || x.getText().contains(pattern)) {
                String type = x.getImportance();
                Email.Importance imp = Email.Importance.valueOf(type);
                Email emaildata = new Email(x.getSender(), x.getReceiverListTo(), x.getSubject(), x.getText(), imp);
                emaildata.setRead(x.getRead());
                emaildata.setReceived(x.getReceived());
                ersatz.add(emaildata);
            }
        }
        return ersatz;
    }
}
