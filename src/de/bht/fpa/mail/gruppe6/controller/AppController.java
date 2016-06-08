package de.bht.fpa.mail.gruppe6.controller;

import de.bht.fpa.mail.gruppe6.model.applicationLogic.*;
import java.net.URL;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.stage.*;
import de.bht.fpa.mail.gruppe6.model.data.Component;
import de.bht.fpa.mail.gruppe6.model.data.Email;
import de.bht.fpa.mail.gruppe6.model.data.Email.Importance;
import de.bht.fpa.mail.gruppe6.model.data.Folder;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class AppController implements Initializable {

    @FXML
    private TreeView<Component> directoryTree;
    @FXML
    private Menu file;
    @FXML
    private TableView<Email> tableview;
    @FXML
    private TableColumn<Email, String> importance;
    @FXML
    private TableColumn<Email, String> received;
    @FXML
    private TableColumn<Email, String> read;
    @FXML
    private TableColumn<Email, String> sender;
    @FXML
    private TableColumn<Email, String> recipients;
    @FXML
    private TableColumn<Email, String> subject;
    @FXML
    private TextField searchField;
    @FXML
    private Label numberOfMails;
    @FXML
    private TextArea textarea;
    @FXML
    private TextFlow textflow;

    private EmailManagerIF mails = new EmailManager();
    private File startDirectory = new File(System.getProperty("user.home"));
    private FolderManagerIF reader;
    private TreeItem<Component> rootNode;// The rootNode of the TreeView
    private static ArrayList<String> historyData = new ArrayList<String>();
    private static final TreeItem<String> loading = new TreeItem<String>(); //Dummy tree item, used to fill other TreeItems with a children to enable the expand arrow
    private final Image open = new Image(getClass().getResourceAsStream("/de/bht/fpa/mail/gruppe6/pic/open.png"));
    private final Image close = new Image(getClass().getResourceAsStream("/de/bht/fpa/mail/gruppe6/pic/closed.png"));
    private static ObservableList<Email> tableinfo;
    private static ObservableList<Email> ersatz;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureMenue(file, (e) -> handleAll(e));
        changeDirectory(startDirectory);
        directoryTree.getSelectionModel().selectedItemProperty().addListener((obs, old_val, new_val) -> showEmail(new_val));
        directoryTree.getSelectionModel().selectedItemProperty().addListener((obs, old_val, new_val) -> generateTable(new_val));
        tableinfo = FXCollections.observableArrayList();
        inItTable();
        searchField.textProperty().addListener((e) -> filterList());
        tableview.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> displayEmail(oldValue, newValue));
        numberOfMails.setText("0");
    }

    public void configureMenue(Menu menu, EventHandler<ActionEvent> handler) {
        for (MenuItem it : menu.getItems()) {
            if (!(it instanceof Menu)) {
                it.setOnAction(handler);
            }
        }
    }

    public void handleAll(ActionEvent e) {
        MenuItem it = (MenuItem) e.getSource();
        if (it instanceof MenuItem) {
            switch (it.getText()) {
                case "Open":
                    selectDirectory();
                    break;
                case "History":
                    showHistory(e);
                    break;
                case "Save":
                    emailWindow(e);
            }
        }
    }
    
    private void emailWindow(ActionEvent e) {
        Stage stage = new Stage();
        stage.setTitle("Open New Directory");
        DirectoryChooser fs = new DirectoryChooser();
        File file = fs.showDialog(stage);
        mails.saveEmails(file);
    }
    
    private void showEmail(TreeItem<Component> folder) {
        if (folder != null) {
            Folder f = (Folder) folder.getValue();
            mails.loadEmails(f);
            System.out.println("\n\nDer Ordner befindet sich in " + f.getPath());
            System.out.println("Sie haben " + f.getEmails().size() + " Emails in diesem Ordner :)");
            if (f.getEmails().size() > 0) {
                for (Email x : f.getEmails()) {
                    System.out.println(x);
                }
            }
        }
    }

    private void generateTable(TreeItem<Component> folder) {
        tableview.getItems().clear();
        if (folder != null) {
            Folder f = (Folder) folder.getValue();
            for (Email x : f.getEmails()) {
                if (x != null) {
                    String type = x.getImportance();
                    Importance imp = Importance.valueOf(type);
                    Email emaildata = new Email(x.getSender(), x.getReceiverListTo(), x.getSubject(), x.getText(), imp);
                    emaildata.setRead(x.getRead());
                    emaildata.setReceived(x.getReceived());
                    tableinfo.add(emaildata);
                }
            }
            tableview.setItems(tableinfo);
            numberOfMails.setText(tableinfo.size()+"");
        }
    }
    //Darstellungslogik

    private void filterList() {
        String pattern = searchField.getText();
        tableview.setItems(search(pattern));
        numberOfMails.setText(tableview.getItems().size()+"");

    }
    //Anwendungslogik

    private ObservableList<Email> search(String pattern) {
        ersatz = FXCollections.observableArrayList();
        for (Email x : tableinfo) {
            if (x.toString().toLowerCase().contains(pattern) || x.getText().contains(pattern)) {
                String type = x.getImportance();
                Importance imp = Importance.valueOf(type);
                Email emaildata = new Email(x.getSender(), x.getReceiverListTo(), x.getSubject(), x.getText(), imp);
                emaildata.setRead(x.getRead());
                emaildata.setReceived(x.getReceived());
                ersatz.add(emaildata);
            }
        }
        return ersatz;
    }

    public static ObservableList<Email> getTableinfo() {
        return tableinfo;
    }

    private void displayEmail(Email oldValue, Email newValue) {
        if (oldValue != newValue && newValue != null) {
            textflow.getChildren().clear();
            Text text = new Text(newValue.getSender() + " \n"
                    + newValue.getSubject() + "\n"
                    + newValue.getReceived() + "\n"
                    + newValue.getReceiver());
            text.setFont(Font.font("System", FontWeight.NORMAL, 12));
            textflow.getChildren().add(text);
            textarea.clear();
            String text2 = ("Betreff: " + newValue.getSubject() + " \n"
                    + "Datum: " + newValue.getReceived() + "\n"
                    + "Von: " + newValue.getSender()
                    + "   Antwort an: " + newValue.getReceiverCC() + "\n"
                    + "An: " + newValue.getReceiverTo() + "\n"
                    + "Nachricht: \n" + newValue.getText());
            textarea.appendText(text2);
        }
    }
    
    public void handleExpand(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        BooleanProperty bb = (BooleanProperty) observable;
        TreeItem<Component> t = (TreeItem<Component>) bb.getBean();
        if (newValue) {
            ImageView image = new ImageView(open);
            t.setGraphic(image);
            expandNode(t);
        }
        else if (!newValue) {
            ImageView image = new ImageView(close);
            t.setGraphic(image);
        }
    }

    public void changeDirectory(File file) {
        reader = new FolderManager(file);
        Component component = reader.getTopFolder();
        rootNode = new TreeItem<Component>(component);
        ImageView image = new ImageView(open);
        rootNode.setGraphic(image);
        rootNode.setExpanded(true);
        rootNode.getValue().getComponents().forEach((Component c) -> rootNode.getChildren().add(buildTreeNode(c)));
        directoryTree.setRoot(rootNode);
    }

    public TreeItem<Component> buildTreeNode(Component c) {
        TreeItem items = new TreeItem<Component>(c);
        ImageView image = new ImageView(close);
        items.setGraphic(image);
        if (c.isExpandable()) {
            //attach event handler, wich takes effect if the TreeItem gets expanded
            items.expandedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                handleExpand(observable, oldValue, newValue);
            });
            items.getChildren().add(loading);
        }
        else if (!c.isExpandable()) {
            image = new ImageView(open);
            items.setGraphic(image);

        }
        return items;

    }

    public void selectDirectory() {
        Stage stage = new Stage();
        stage.setTitle("Open New Directory");
        DirectoryChooser fs = new DirectoryChooser();
        File s = fs.showDialog(stage);
        if (s != null) {
            String y = s.toString();
            fs.setInitialDirectory(s);
            addHistory(y);
            changeDirectory(s);
        }
    }

    public void showHistory(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/bht/fpa/mail/gruppe6/view/HistoryWindow.fxml"));
            loader.setController(new HistoryController(this));
            Parent root = (Parent) loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 480, 373));
            stage.setTitle("History");
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(HistoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addHistory(String txt) {
        historyData.add(txt);
    }

    public ArrayList<String> getHistory() {
        return historyData;
    }

    public void historyAction(String x) {
        File newfile = new File(x);
        changeDirectory(newfile);
    }

    public void inItTable() {
        importance.setCellValueFactory(new PropertyValueFactory<>("importance"));
        received.setCellValueFactory(new PropertyValueFactory<>("received"));
        read.setCellValueFactory(new PropertyValueFactory<>("read"));
        sender.setCellValueFactory(new PropertyValueFactory<>("sender"));
        subject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        recipients.setCellValueFactory(new PropertyValueFactory<>("receiverTo"));
    }

    public void expandNode(TreeItem<Component> node) {
        //if the node does not have the dummy inside we already load its children and we can return
        if (!node.getChildren().get(0).equals(loading)) {
        }
        else {
            node.getChildren().remove(loading);
            Folder folder = (Folder) node.getValue();
            reader.loadContent(folder);
            //and we render the children
            folder.getComponents().forEach((Component c) -> {
                node.getChildren().add(buildTreeNode(c));
            });
        }
    }

//    public int compare(String t1, String t2) throws ParseException {
//        Date date1 = Email.FORMAT.parse(t1);
//        Date date2 = Email.FORMAT.parse(t2);
//        return Collator.getInstance().compare(date1, date2);
//    }
}
