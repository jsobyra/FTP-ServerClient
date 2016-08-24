package Client.Controllers;

import Client.ServerCommunication.ServerConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Created by KUBA on 2016-08-18.
 */
public class LoginController {

    @FXML
    private TextField serverAddressTextField;
    @FXML
    private TextField portTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void initialize() throws IOException{
        serverAddressTextField.setText("localhost");
        portTextField.setText(String.valueOf("9000"));
        usernameTextField.setText("Kuba");
        passwordField.setText("1234");
    }

    @FXML
    public void connectToServer(ActionEvent event) throws IOException{
        boolean succesfullLogin = isValidCredentials();
        if(succesfullLogin){
            Parent filezilla_page_parent = FXMLLoader.load(getClass().getClassLoader().getResource("Client/Resources/filezillauserpanel.fxml"));
            Scene filezilla_page_scene = new Scene(filezilla_page_parent);
            Stage app_stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            app_stage.hide();
            app_stage.setScene(filezilla_page_scene);
            app_stage.show();

        }
    }

    private boolean isValidCredentials(){
        boolean success = false;
        try{
            new ServerConnection(serverAddressTextField.getText(), Integer.parseInt(portTextField.getText()));
            success = ServerConnection.getInstance().login(usernameTextField.getText(), passwordField.getText());
        } catch (IOException e){
            System.out.println("Cannot connect to server!!!");
        }

        return success;
    }
}
