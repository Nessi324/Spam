package de.bht.fpa.mail.gruppe6.controller;

import de.bht.fpa.mail.gruppe6.model.applicationLogic.EmailManager;
import de.bht.fpa.mail.gruppe6.model.applicationLogic.EmailManagerIF;
import de.bht.fpa.mail.gruppe6.model.applicationLogic.FolderManager;
import de.bht.fpa.mail.gruppe6.model.applicationLogic.FolderManagerIF;
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
import de.bht.fpa.mail.gruppe6.model.data.Folder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AppController implements Initializable {

    @FXML
    private TreeView<Component> directoryTree;
    @FXML
    private Menu file;

    private EmailManagerIF mails = new EmailManager();
    private File startDirectory = new File(System.getProperty("user.home"));
    private FolderManagerIF reader;
    private TreeItem<Component> rootNode;// The rootNode of the TreeView
    private static ArrayList<String> historyData = new ArrayList<String>();
    private static final TreeItem<String> loading = new TreeItem<String>(); //Dummy tree item, used to fill other TreeItems with a children to enable the expand arrow
    private final Image open = new Image(getClass().getResourceAsStream("/de/bht/fpa/mail/gruppe6/pic/open.png"));
    private final Image close = new Image(getClass().getResourceAsStream("/de/bht/fpa/mail/gruppe6/pic/closed.png"));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureMenue(file, (e) -> handleAll(e));
        configureTree(startDirectory);
        directoryTree.getSelectionModel().selectedItemProperty().addListener((obs, old_val, new_val) -> showEmail(new_val));

    }

    /**
     * Initializing the TreeView configuration
     *
     * @param x the new Directory for the TreeView
     */
    public void configureTree(File file) {
        reader = new FolderManager(file);
        //creads the home directory and store it into component
        Component component = reader.getTopFolder();
        //create the rootNode from the component
        rootNode = new TreeItem<Component>(component);
        //set the image and set sizes
        ImageView image = new ImageView(open);
        rootNode.setGraphic(image);

        //expand the root node by default
        rootNode.setExpanded(true);

        rootNode.getValue().getComponents().forEach((Component c) -> {
            rootNode.getChildren().add(buildTreeNode(c));
        });
        //set the root to our TreeView
        directoryTree.setRoot(rootNode);
    }

    /**
     * Expands the give Node(TreeItem)
     *
     * @param node The tree item that should get expanded
     * @return void
     */
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

    /**
     * Event handler for expand
     *
     * @param observable
     * @param oldValue
     * @param newValue
     */
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
        else if(!c.isExpandable()){
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
}
