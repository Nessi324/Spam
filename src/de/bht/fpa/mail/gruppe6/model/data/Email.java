package de.bht.fpa.mail.gruppe6.model.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Email {
    public static final DateFormat FORMAT = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, Locale.GERMANY);
    private String sender;
    private ArrayList<String> receiverTo;
    private ArrayList<String> receiverCC;
    private ArrayList<String> receiverBCC;
    private String subject;
    private String text;
    private Importance importance;
    private Date sent;
    private Date received;
    private boolean read;

    public Email() {
        this.receiverTo = new ArrayList<String>();
        this.receiverCC = new ArrayList<String>();
        this.receiverBCC = new ArrayList<String>();
        this.subject = "";
        this.text = "";
        this.importance = Importance.LOW;
        this.sent = Calendar.getInstance().getTime();
        this.received = Calendar.getInstance().getTime();
        this.read = false;
    }

    public Email(String sender, ArrayList<String> receiverTo, String subject, String txt, Importance imp) {
        this.sender = sender;
        this.receiverTo = receiverTo;
        this.receiverCC = new ArrayList<String>();
        this.receiverBCC = new ArrayList<String>();
        this.subject = subject;
        this.text = txt;
        if (imp != null) {
            this.importance = imp;
        } else {
            imp = Importance.LOW;
        }
        this.sent = Calendar.getInstance().getTime();
        this.received = Calendar.getInstance().getTime();
        this.read = false;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String s) {
        sender = s;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String b) {
        subject = b;
    }

    public String getText() {
        return text;
    }

    public void setText(String t) {
        text = t;
    }

    public String getImportance() {
        return importance.toString();
    }

    public void setImportance(Importance d) {
        if (d != null) {
            importance = d;
        } else {
            d = Importance.NORMAL;
        }
    }

    private String generateString(ArrayList<String> list) {
        Iterator<String> it = list.iterator();
        String emp = "";
        if (it.hasNext()) {
            emp = it.next();
        }
        while (it.hasNext()) {
            emp = emp + ", " + it.next();
        }
        return emp;
    }

    public ArrayList<String> generateList(String receiver) {
        String[] result = receiver.split(", ");
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(result));
        return list;
    }

    public ArrayList<String> getReceiverListTo() {
        return receiverTo;
    }

    public ArrayList<String> getReceiverListCC() {
        return receiverCC;
    }

    public ArrayList<String> getReceiverListBCC() {
        return receiverBCC;
    }

    public void setReceiverTo(String r) {
        receiverTo = generateList(r);
    }

    public void setReceiverCC(String r) {
        receiverCC = generateList(r);
    }

    public void setReceiverBCC(String r) {
        receiverBCC = generateList(r);
    }

    public String getReceiverTo() {
        return generateString(receiverTo);
    }

    public String getReceiverCC() {
        return generateString(receiverCC);
    }

    public String getReceiverBCC() {
        return generateString(receiverBCC);
    }

    public String getReceiver() {
        String cc = getReceiverCC();
        String recs = getReceiverTo();
        if (cc.length() != 0) {
            recs = recs + ", " + cc;
        }
        return recs;
    }

    public boolean getRead() {
        return read;
    }

    public void setRead(boolean w) {
        read = w;
    }
    
    public String getReceived() {
        return FORMAT.format(received);
    }
    
    public void setReceived(Date date) {
        received = date;
    }
    
    public void setReceived(String date) {
        try {
            received = FORMAT.parse(date);
        } catch (ParseException ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getSent() {
        return FORMAT.format(sent);
    }

    public void setSent(Date date) {
        sent = date;
    }

    public void setSent(String date) {
        try {
            sent = FORMAT.parse(date);
        } catch (ParseException ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[Email: ");
        s.append("sender=").append(sender).append(" ");
        s.append("received=").append(getReceived()).append(" ");
        s.append("subject=").append(subject).append(" ");
        s.append("read=").append(read).append(" ");
        s.append("importance=").append(importance).append(" ");

        s.append("receiver=(");
        s.append(getReceiver());
        s.append(")");
        s.append("]").append(" ");
        return s.toString();
    }
    
    public static enum Importance {

        HIGH, NORMAL, LOW
    }
}