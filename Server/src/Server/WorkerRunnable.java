package Server;

import Server.Command.Command;
import Server.Command.CommandCreator;
import Server.Command.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Created by KUBA on 2016-08-02.
 */
public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    private SessionManager sessionManager = new SessionManager();
    private volatile boolean isRunning = true;

    public WorkerRunnable(Socket clientSocket) throws SocketException{
        this.clientSocket = clientSocket;
        sessionManager = new SessionManager();
        //clientSocket.setSoTimeout(30 * 1000);
    }

    public void setRunning(boolean isRunning){
        this.isRunning = isRunning;
    }

    @Override
    public void run(){
        try{
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;

            sessionManager.setCurrentThread(this);
            out.println("220 Hello");

            while((inputLine = in.readLine()) != null){
                out.println(processInput(inputLine));

                if(!isRunning)
                    return;
            }
        } catch (SocketException e){
            System.out.println("Connection reset");
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    private String processInput(String input){

        String[] parts = input.trim().split("\\s+");
        String commandName = parts[0];
        String[] arguments = Arrays.copyOfRange(parts, 1, parts.length);
        Command command;
        try{
            command = CommandCreator.makeCommand(commandName, arguments, sessionManager);
        } catch (IllegalArgumentException e){
            return e.getMessage();
        }
        String responseMessage;
        responseMessage = command.execute();


        sessionManager.getCommandHistory().add(command);
        return responseMessage;

    }
}
