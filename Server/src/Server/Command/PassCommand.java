package Server.Command;

import Server.Database.DatabaseHelper;
import Server.Database.User;

/**
 * Created by KUBA on 2016-08-09.
 */
public class PassCommand extends Command {
    private String username;
    private String password;

    public PassCommand(String[] arguments, SessionManager sessionManager){
        super(sessionManager);
        if(arguments.length != 1){
            throw new IllegalArgumentException("501 Syntax error in parameters or arguments.");
        }
        if(!isAfterUserCommand()){
            throw new IllegalArgumentException("503 Bad sequence of commands");
        }
        password = arguments[0];
    }

    @Override
    public String execute(){
        User user = DatabaseHelper.getInstance().getUser(username);
        System.out.println(user.getName());
        if(user == null){
            System.out.println("Blad1");
            return "430 Invalid username or password";
        }

        if(DatabaseHelper.getInstance().validatePassword(user, password)){
            getSessionManager().setLoggedUser(user);
            return "230 User logged in";
        }
        else{
            System.out.println("Blad2");
            return "430 Invalid username or password";
        }

    }

    private boolean isAfterUserCommand(){
        if(getSessionManager().getCommandHistory().isEmpty()){
            System.out.println("Dupa");
            return false;
        }


        Command earlierCommand = getSessionManager().getCommandHistory().get(0);
        if(earlierCommand.getClass() == UserCommand.class){
            username = ((UserCommand) earlierCommand).getUsername();
            return true;
        }
        else
            return false;
    }
}
