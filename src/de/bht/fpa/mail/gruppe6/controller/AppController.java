package de.bht.fpa.mail.gruppe6.controller;

import de.bht.fpa.mail.gruppe6.model.applicationLogic.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import de.bht.fpa.mail.gruppe6.model.data.Component;
import de.bht.fpa.mail.gruppe6.model.data.Email;
import de.bht.fpa.mail.gruppe6.model.data.Email.Importance;
import de.bht.fpa.mail.gruppe6.model.data.Folder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
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
        configureTree(startDirectory);
        directoryTree.getSelectionModel().selectedItemProperty().addListener((obs, old_val, new_val) -> showEmail(new_val));
        directoryTree.getSelectionModel().selectedItemProperty().addListener((obs, old_val, new_val) -> generateTable(new_val));
        tableinfo = FXCollections.observableArrayList();
        inItTable();
        searchField.textProperty().addListener((e) -> filterList());
        tableview.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> 
        { displayEmail(oldValue, newValue);});
    
    }
    public void inItTable() {
        importance.setCellValueFactory(new PropertyValueFactory<>("importance"));
        received.setCellValueFactory(new PropertyValueFactory<>("received"));
        read.setCellValueFactory(new PropertyValueFactory<>("read"));
        sender.setCellValueFactory(new PropertyValueFactory<>("sender"));
        subject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        recipients.setCellValueFactory(new PropertyValueFactory<>("receiverTo"));
    }

    public void configureTree(File file) {
        reader = new FolderManager(file);
        Component component = reader.getTopFolder();
        rootNode = new TreeItem<Component>(component);
        ImageView image = new ImageView(open);
        rootNode.setGraphic(image);
        rootNode.setExpanded(true);
        rootNode.getValue().getComponents().forEach((Component c) -> {
            rootNode.getChildren().add(buildTreeNode(c));
        });
        directoryTree.setRoot(rootNode);
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

    public void handleExpand(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        BooleanProperty bb = (BooleanProperty) observable;
        TreeItem<Component> t = (TreeItem<Component>) bb.getBean();
        //if the new value is == true the item got expanded
        if (newValue) {
            //we get the effected TreeItem, wich got expanded
            ImageView image = new ImageView(open);
            t.setGraphic(image);
            //we expand the Node
            expandNode(t);
        }
        else if (!newValue) {
            ImageView image = new ImageView(close);
            t.setGraphic(image);
        }
    }

    public TreeItem<Component> buildTreeNode(Component c) {
        TreeItem item = new TreeItem<Component>(c);
        ImageView image = new ImageView(close);
        item.setGraphic(image);
        if (c.isExpandable()) {
            //attach event handler, wich takes effect if the TreeItem gets expanded
            item.expandedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                handleExpand(observable, oldValue, newValue);
            });
            item.getChildren().add(loading);
        }
        else if (!c.isExpandable()) {
            image = new ImageView(open);
            item.setGraphic(image);

        }
        return item;

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
        //Menu parentMenu = it.getParentMenu();
        if (it instanceof Menu) {

        }
        else if (it instanceof MenuItem) {
            switch (it.getText()) {
                case "Open":
                    selectDirectory();
                    break;
                case "History":
                    showHistory(e);
                    break;
            }
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
        configureTree(newfile);
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
            configureTree(s);
        }
    }

    private void showEmail(TreeItem<Component> folder) {
        if (folder != null) {
            Folder f = (Folder) folder.getValue();
            mails.loadEmails(f);
            System.out.println("\n\nDer Ordner befindet sich in " + f.getPath());
            System.out.println("Sie haben " + f.getEmails().size() + " Emails in diesem Fach :)");
            if (f.getEmails().size() > 0) {
                for (Email x : f.getEmails()) {
                    System.out.println(x);
                }
            }
        }
    }

    private void generateTable(TreeItem<Component> folder) {
        if (folder != null) {
            Folder f = (Folder) folder.getValue();
            numberOfMails.setText(f.getEmails().size() + "");
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
        }
    }

    private void filterList() {
        ersatz = FXCollections.observableArrayList();
        String suche = searchField.getText();
        for (Email x : tableinfo) {
            if (x.toString().toLowerCase().contains(suche)) {
                String type = x.getImportance();
                Importance imp = Importance.valueOf(type);
                Email emaildata = new Email(x.getSender(), x.getReceiverListTo(), x.getSubject(), x.getText(), imp);
                emaildata.setRead(x.getRead());
                emaildata.setReceived(x.getReceived());
                ersatz.add(emaildata);
            }
        }
        tableview.setItems(ersatz);

    }

    private void displayEmail(Email oldValue, Email newValue) {
        if(oldValue== newValue){}
        else {
            textflow.getChildren().clear();
            Text text = new Text(newValue.getSender()+" \n"+
                    newValue.getSubject() +"\n" 
                    +newValue.getReceived()+"\n"+
                    newValue.getReceiver());
            text.setFont(Font.font("System", FontWeight.NORMAL, 12));
            textflow.setLineSpacing(10);
            textflow.getChildren().add(text);
            textarea.clear();
            textarea.appendText("Betreff: " +newValue.getSubject()+" \n"+
            "Datum: " +newValue.getReceived() +"\n"+
            "Von: " +newValue.getSender()+
            "   Antwort an: " +newValue.getReceiverCC()+"\n"+
            "An: " +newValue.getReceiverTo()+"\n"+
            "Nachricht: \n"+newValue.getText());
        }
    
    }
}
