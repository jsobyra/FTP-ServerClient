package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;

/**
 * Created by KUBA on 2016-08-03.
 */
public class ClientConnection extends Application{

    public static void main(String[] args) throws IOException, InterruptedException{
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException{
        Pane page = FXMLLoader.load(ClientConnection.class.getResource("Resources/login.fxml"));
        Scene scene = new Scene(page);
        primaryStage.setScene(scene);

        primaryStage.setTitle("FTP client - Login Window");
        primaryStage.show();
    }
}













    /*
    public static void main(String[] args){

        //working
        //String server = "ftp.icm.edu.pl";
        //int port = 21;


        String hostname = "localhost";
        int port = 9000;
        File file = new File("C:\\Users\\KUBA\\Desktop\\Pod Mocnym Aniołem.avi");

        try{
            new ServerConnection("localhost", 9000);
            ServerConnection.getInstance().login("Kuba", "1234");
            String response = ServerConnection.getInstance().listCurrentDirectory();
            System.out.println(response);
        } catch (IOException e){
            System.out.println("Error");
        }


/*
        try{
            Socket socket = new Socket(hostname, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;

            while((fromServer = in.readLine()) != null){
                System.out.println("Server: " + fromServer);
                if(fromServer.equals("Bye."))
                    break;

                fromUser = stdIn.readLine();
                if(fromUser != null){
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
            }
        } catch (IOException e){
            System.out.println("Nie udalo sie!!! :(");
        }*/


