package Controller;

import Model.DatabaseHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;

import java.sql.SQLException;

/**
 * Created by KUBA on 2016-08-07.
 */
public class AdminPanelController {
    DatabaseHelper databaseHelper;

    @FXML
    Button performButton;
    @FXML
    TextField firstField;
    @FXML
    TextField secondField;
    @FXML
    Label firstFieldLabel;
    @FXML
    Label secondFieldLabel;
    @FXML
    ChoiceBox actionChoice;

    @FXML
    private void initialize() throws SQLException{
        databaseHelper = new DatabaseHelper();
        databaseHelper.initializeDatabase();
        adjustControlls();
    }

    @FXML
    private void handleActionChoice(ActionEvent actionEvent){
        adjustControlls();
    }

    @FXML
    private void handlePerformButton(ActionEvent actionEvent) throws SQLException{
        switch (actionChoice.getValue().toString()){
            case "Add user":
                databaseHelper.addUser(firstField.getText(), secondField.getText());
                break;
            case "Add group":
                databaseHelper.addGroup(firstField.getText());
                break;
            case "Add user to group":
                databaseHelper.addGroupUser(firstField.getText(), secondField.getText());
                break;
            case "Delete user":
                databaseHelper.deleteUser(firstField.getText());
                break;
            case "Delete group":
                databaseHelper.deleteGroup(firstField.getText());
                break;
            case "Delete user in group":
                databaseHelper.deleteGroupUser(firstField.getText(), secondField.getText());
                break;
        }
    }

    private void adjustControlls(){
        switch (actionChoice.getValue().toString()){
            case "Add user":
                firstField.setText("");
                secondField.setText("");
                firstFieldLabel.setText("Username");
                secondFieldLabel.setText("Password");
                secondField.setVisible(true);
                break;
            case "Add group":
                firstField.setText("");
                secondField.setText("");
                firstFieldLabel.setText("Group");
                secondFieldLabel.setText("");
                secondField.setVisible(false);
                break;
            case "Add user to group":
                firstField.setText("");
                secondField.setText("");
                firstFieldLabel.setText("Username");
                secondFieldLabel.setText("Group");
                secondField.setVisible(true);
                break;
            case "Delete user":
                firstField.setText("");
                secondField.setText("");
                firstFieldLabel.setText("Username");
                secondFieldLabel.setText("");
                secondField.setVisible(false);
                break;
            case "Delete group":
                firstField.setText("");
                secondField.setText("");
                firstFieldLabel.setText("Group");
                secondFieldLabel.setText("");
                secondField.setVisible(false);
                break;
            case "Delete user in group":
                firstField.setText("");
                secondField.setText("");
                firstFieldLabel.setText("Username");
                secondFieldLabel.setText("Group");
                secondField.setVisible(true);
                break;
        }
    }
}