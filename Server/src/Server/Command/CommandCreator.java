package Server.Command;

/**
 * Created by KUBA on 2016-08-08.
 */
public class CommandCreator {

    public static Command makeCommand(String commandName, String[] arguments, SessionManager sessionHistory){

        switch (commandName){
            case "USER":
                return new UserCommand(arguments, sessionHistory);
            case "PASS":
                return new PassCommand(arguments, sessionHistory);
            case "QUIT":
                return new QuitCommand(arguments, sessionHistory);
            case "NOOP":
                return new NoopCommand(arguments, sessionHistory);
            case "PASV":
                return new PasvCommand(arguments, sessionHistory);
            case "STOR":
                return new StorCommand(arguments, sessionHistory);
            case "RETR":
                return new RetrCommand(arguments, sessionHistory);
            case "APPE":
                return new AppeCommand(arguments, sessionHistory);
            case "ABOR":
                return new AborCommand(arguments, sessionHistory);
            case "DELE":
                return new DeleCommand(arguments, sessionHistory);
            case "RMD":
                return new RmdCommand(arguments, sessionHistory);
            case "MKD":
                return new MkdCommand(arguments, sessionHistory);
            case "PWD":
                return new PwdCommand(arguments, sessionHistory);
            case "LIST":
                return new ListCommand(arguments, sessionHistory);
            case "CWD":
                return new CwdCommand(arguments, sessionHistory);
            case "CHMOD":
                return new ChmodCommand(arguments, sessionHistory);
            default:
                throw new NoSuchCommandException();
        }
    }
}






