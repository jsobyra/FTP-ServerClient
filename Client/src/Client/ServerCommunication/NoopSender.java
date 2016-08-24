package Client.ServerCommunication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TimerTask;

/**
 * Created by KUBA on 2016-08-14.
 */
public class NoopSender extends TimerTask{
    private final BufferedReader inCommand;
    private final PrintWriter outCommand;

    public NoopSender(BufferedReader bufferedReader, PrintWriter printWriter){
        this.inCommand = bufferedReader;
        this.outCommand = printWriter;
    }

    @Override
    public void run(){
        synchronized (inCommand){
            ServerConnection.getInstance().sendCommand("NOOP");
            try {
                ServerConnection.getInstance().readAnswer();
            } catch (IOException e){
                System.out.println("Error reading answer for NOOP command");
            }
        }

    }
}
