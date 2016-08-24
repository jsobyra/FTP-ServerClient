package Client.Controllers;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Created by KUBA on 2016-08-20.
 */


public class FileItem extends File{
    private boolean isDir;
    private boolean isListed = false;

    public FileItem(String pathname){
        super(pathname);
    }
    public FileItem(File file){
        super(file.getPath());
    }

    public void setListed(boolean isListed){
        this.isListed = isListed;
    }

    public void setDir(boolean isDir){
        this.isDir = isDir;
    }

    public boolean isListed(){
        return isListed;
    }

    public boolean isDir(){
        return isDir;
    }

    public boolean isNotListed(){
        return !isListed;
    }

    public static Node getFolderIcon(){
        return new ImageView(new Image(FileItem.class.getClassLoader().getResourceAsStream("Client/Resources/folderIcon.png")));
    }

    private static Node getFileIcon(){
        return new ImageView(new Image(FileItem.class.getClassLoader().getResourceAsStream("Client/Resources/fileIcon.png")));
    }

    public static TreeItem<FileItem> convertStringLine(String line, File directory){
        String filename = getFilename(line);
        if(filename.equals(".") || filename.equals(".."))
            return null;

        String pathname = directory.getPath() + "/" + filename;
        FileItem fileItem = new FileItem(pathname);
        if(line.charAt(0) == 'd'){
            fileItem.isDir = true;
            return new TreeItem<>(fileItem, getFolderIcon());
        }
        else{
            fileItem.isDir = false;
            return new TreeItem<>(fileItem, getFileIcon());
        }
    }

    public Node getIcon(){
        return isDir() ? getFolderIcon() : getFileIcon();
    }

    private static String getFilename(String line){
        String[] parts = line.split("\\s+");
        return String.join(" ", Arrays.copyOfRange(parts, 5, parts.length));
    }

    @Override
    public String toString(){
        return this.getName();
    }

    public TreeItem<FileItem> getTreeItem(){
        if(this.isDir() || this.isDirectory())
            return new TreeItem<>(this, getFolderIcon());
        else
            return new TreeItem<>(this, getFileIcon());
    }

    public static TreeItem<FileItem> findNode(String path, TreeItem<FileItem> root){
        String rootPath = root.getValue().getPath();
        if(rootPath.equals(path))
            return root;
        for(TreeItem<FileItem> file : root.getChildren()){
            if(path.startsWith(file.getValue().getPath())){
                return findNode(path, file);
            }

        }
        return null;
    }
}

