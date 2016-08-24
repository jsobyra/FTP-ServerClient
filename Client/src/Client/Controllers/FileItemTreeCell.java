package Client.Controllers;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * Created by KUBA on 2016-08-24.
 */
public class FileItemTreeCell extends TreeCell<FileItem> {

    public FileItemTreeCell(){
        this.setOnDragOver(event -> {
            TreeView source = ((TreeCell) event.getGestureSource()).getTreeView();
            TreeView target = this.getTreeView();
            if(!source.equals(target)){
                event.acceptTransferModes(TransferMode.COPY);
                event.consume();
            }
        });

        this.setOnDragDetected(event -> {
            if(!this.getTreeItem().getValue().isDir()){
                Dragboard dragboard = this.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.putString(this.getItem().getPath());
                dragboard.setContent(content);
                event.consume();
            }
        });
    }

    @Override
    protected void updateItem(FileItem item, boolean empty){
        super.updateItem(item, empty);
        if(empty || (item == null)){
            setText(null);
            setGraphic(null);
        }
        else {
            setText(item.toString());
            setGraphic(item.getIcon());
        }
    }
}



























































