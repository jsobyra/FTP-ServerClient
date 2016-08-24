package Client.Controllers;

import Client.ServerCommunication.ServerConnection;
import Client.ServerCommunication.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import sun.reflect.generics.tree.Tree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;

/**
 * Created by KUBA on 2016-08-20.
 */
public class FillezillauserpanelController {

    @FXML
    TreeView<FileItem> remoteTree;
    @FXML
    TreeView<FileItem> localTree;

    @FXML
    private void initialize() throws IOException{
        initializeRemoteTree();
        initializeLocalTree();
    }

    @FXML
    private void remoteTreeClickAction() throws IOException{
        TreeItem<FileItem> treeItem = remoteTree.getSelectionModel().getSelectedItem();
        if(treeItem != null){
            clickRemoteItem(treeItem);
        }
    }

    @FXML
    private void localTreeClickAction() throws IOException{
        TreeItem<FileItem> treeItem = localTree.getSelectionModel().getSelectedItem();
        if(treeItem != null){
            clickLocalItem(treeItem);
        }
    }

    private void clickRemoteItem(TreeItem<FileItem> treeItem) throws IOException{
        FileItem file = treeItem.getValue();
        if(file.isDir()){
            if(!file.getPath().equals(SessionManager.getInstance().getRemotePath()))
                ServerConnection.getInstance().changeRemoteDirectory(file.getPath());
        }
        else{
            if(!file.getParent().equals(SessionManager.getInstance().getRemotePath()))
                ServerConnection.getInstance().changeRemoteDirectory(file.getParent());
        }
        if(file.isDir() && file.isNotListed()){
            listItemsInRemoteDirectory(file, treeItem);
        }
    }

    private void clickLocalItem(TreeItem<FileItem> treeItem) throws IOException{
        FileItem file = treeItem.getValue();
        if(file.isDirectory()){
            SessionManager.getInstance().setLocalPath(file.getPath());
        }
        else{
            SessionManager.getInstance().setLocalPath(file.getParent());
        }
        if(file.isDirectory() && file.isNotListed()){
            listItemsInLocalDirectory(file, treeItem);
        }
    }

    private void initializeRemoteTree() throws IOException{
        File remoteRoot = new File("/");
        TreeItem<FileItem> rootItem = new TreeItem<>(new FileItem(remoteRoot));
        remoteTree.setRoot(rootItem);
        remoteTree.setShowRoot(false);
        rootItem.setExpanded(true);
        listItemsInRemoteDirectory(remoteRoot, rootItem);
        SessionManager.getInstance().setRemotePath("/");
        initializeRemoteTreeContextMenu();
        initializeRemoteDragAndDrop();
    }

    private void initializeLocalTree() throws IOException{
        File home = Paths.get(System.getProperty("user.home")).toFile();
        TreeItem<FileItem> rootItem = new TreeItem<>(new FileItem(home));
        localTree.setRoot(rootItem);
        localTree.setShowRoot(false);
        rootItem.setExpanded(true);
        listItemsInLocalDirectory(home, rootItem);
        SessionManager.getInstance().setLocalPath(home.getPath());
        initializeLocalTreeContextMenu();
        initializeLocalDragAndDrop();
    }

    private void initializeRemoteDragAndDrop(){
        remoteTree.setCellFactory(treeView -> {
            TreeCell<FileItem> treeCell = new FileItemTreeCell();
            treeCell.setOnDragDropped(event -> {
                try {
                    Dragboard dragboard = event.getDragboard();
                    clickRemoteItem(treeCell.getTreeItem());
                    sendToServer(new File(dragboard.getString()));
                    event.setDropCompleted(true);
                    event.consume();
                } catch (IOException e){
                    System.out.println("Error when dragging and dropping on remote tree!!!");
                }
            });
            return treeCell;
        });
    }

    private void initializeLocalDragAndDrop(){
        localTree.setCellFactory(treeView -> {
            TreeCell<FileItem> treeCell = new FileItemTreeCell();
            treeCell.setOnDragDropped(event -> {
                try {
                    Dragboard dragboard = event.getDragboard();
                    clickLocalItem(treeCell.getTreeItem());
                    downloadRemoteFile(new File(dragboard.getString()));
                    event.setDropCompleted(true);
                    event.consume();
                } catch (IOException e){
                    System.out.println("Error when dragging and dropping on remote tree!!!");
                }
            });
            return treeCell;
        });
    }

    private void listItemsInRemoteDirectory(File directory, TreeItem<FileItem> rootItem) throws IOException{
        ServerConnection.getInstance().changeRemoteDirectory(directory.getPath());
        Scanner response = new Scanner(ServerConnection.getInstance().listCurrentDirectory());
        String currentLine;
        while(response.hasNextLine()){
            currentLine = response.nextLine();
            TreeItem<FileItem> fileItemTreeItem = FileItem.convertStringLine(currentLine, directory);
            if(fileItemTreeItem != null){
                System.out.println(fileItemTreeItem.getValue().getPath());
                rootItem.getChildren().add(fileItemTreeItem);
            }

        }
        rootItem.getValue().setListed(true);
    }

    private void listItemsInLocalDirectory(File directory, TreeItem<FileItem> rootItem){
        File[] files = directory.listFiles();
        if(files != null){
            for(File file : files){
                if(file.getName().startsWith("."))
                    continue;
                TreeItem<FileItem> fileItemTreeItem = new FileItem(file).getTreeItem();
                rootItem.getChildren().add(fileItemTreeItem);
            }
        }
        rootItem.getValue().setListed(true);
    }

    private void initializeRemoteTreeContextMenu(){
        ContextMenu remoteContextMenu = new ContextMenu();
        MenuItem sendClient = new MenuItem("Send to Client");
        MenuItem delete = new MenuItem("Delete");
        MenuItem newDirectory = new MenuItem("New directory");
        remoteContextMenu.getItems().addAll(sendClient, delete, newDirectory);
        remoteTree.setContextMenu(remoteContextMenu);
        sendClient.setOnAction(event -> downloadRemoteFile(remoteTree.getSelectionModel().getSelectedItem().getValue()));
        delete.setOnAction(event -> deleteRemoteFile(remoteTree.getSelectionModel().getSelectedItem().getValue()));
        newDirectory.setOnAction(event -> newRemoteDirectory());
    }

    private void initializeLocalTreeContextMenu(){
        ContextMenu localContextMenu = new ContextMenu();
        MenuItem sendServer = new MenuItem("Send to server");
        MenuItem delete = new MenuItem("Delete");
        localContextMenu.getItems().addAll(sendServer, delete);
        localTree.setContextMenu(localContextMenu);
        sendServer.setOnAction(event -> sendToServer(localTree.getSelectionModel().getSelectedItem().getValue()));
        delete.setOnAction(event -> deleteLocalFile(localTree.getSelectionModel().getSelectedItem()));
    }

    private void sendToServer(File file){
        try{
            ServerConnection.getInstance().uploadFileToServer(file);
            String path = SessionManager.getInstance().getRemotePath();
            System.out.println("Path: " + path);
            TreeItem<FileItem> newFile = new FileItem(path + "/" + file.getName()).getTreeItem();
            TreeItem<FileItem> parent = FileItem.findNode(Paths.get(path).toString(), remoteTree.getRoot());
            parent.getChildren().add(newFile);
        } catch (IOException e){
            System.out.println("Error uploading file!!!");
        }
    }

    private void newRemoteDirectory(){
        TextInputDialog dialog = new TextInputDialog("...");
        dialog.setContentText("Please enter new directory name: ");
        Optional<String> response = dialog.showAndWait();
        if(response.isPresent())
            makeNewRemoteDir(response.get());
    }

    private void makeNewRemoteDir(String name){
        try{
            String parentDirectory = SessionManager.getInstance().getRemotePath();
            FileItem newDirectory = new FileItem(parentDirectory + "/" + name);
            newDirectory.setDir(true);
            ServerConnection.getInstance().makeNewRemoteDir(newDirectory.toString());
            System.out.println(Paths.get(parentDirectory));
            System.out.println(Paths.get(parentDirectory).toString());
            TreeItem<FileItem> parent = FileItem.findNode(Paths.get(parentDirectory).toString(), remoteTree.getRoot());
            parent.getChildren().add(newDirectory.getTreeItem());
        } catch (IOException e){
            System.out.println("Cannot create a new remote directory!!!");
        }
    }

    private void deleteRemoteFile(File file){
        try {
            if(ServerConnection.getInstance().deleteRemoteFile(file)){
                TreeItem<FileItem> treeItem = FileItem.findNode(file.getPath(), remoteTree.getRoot());
                if(treeItem != null)
                    treeItem.getParent().getChildren().remove(treeItem);
            }
        } catch (IOException e){
            System.out.println("Error deleting file " + file.getName());
        }
    }

    private void deleteLocalFile(TreeItem<FileItem> file){
        file.getValue().delete();
        file.getParent().getChildren().remove(file);
    }

    private void downloadRemoteFile(File file) {
        try {
            File fileDownload = ServerConnection.getInstance().downloadRemoteFile(file);
            if(fileDownload != null) {
                TreeItem<FileItem> parent = FileItem.findNode(fileDownload.getParent(), localTree.getRoot());
                if(parent != null)
                    parent.getChildren().add(new FileItem(fileDownload).getTreeItem());
            }
        } catch (IOException e) {
            System.out.println("Error downloading file!!");
        }
    }



}
