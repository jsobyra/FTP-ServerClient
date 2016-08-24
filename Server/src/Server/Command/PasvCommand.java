package Server.Command;

import Server.PassiveConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * Created by KUBA on 2016-08-11.
 */
public class PasvCommand extends Command{
    private int passivePort;


    public PasvCommand(String[] arguments, SessionManager sessionManager){
        super(sessionManager);
        if(arguments.length != 0){
            throw new IllegalArgumentException("501 Syntax error in parameters or arguments.");
        }
    }

    @Override
    public String execute(){
        String address = "";
        try{
            PassiveConnection passiveConnection = new PassiveConnection(getSessionManager(), createServerSocket(), passivePort);
            getSessionManager().setPassiveConnection(passiveConnection);
            new Thread(passiveConnection).start();
            address = InetAddress.getLocalHost().getHostAddress().replace('.', ',');
        } catch (UnknownHostException e){
            System.out.println("Error get localhost adress IP");
        } catch (IOException e){
            System.out.println("PASV command: no free port found");
        }
        return "227 Entering Passive Mode (" + address + ",4," + passivePort%256 + ")";
    }

    private ServerSocket createServerSocket() throws IOException{
        for(passivePort = 1025; passivePort < 65000; passivePort++){
            try {
                return new ServerSocket(passivePort);
            } catch (IOException e){

            }
        }
        throw new IOException();
    }
}











































































