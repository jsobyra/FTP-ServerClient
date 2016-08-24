package Server.Command;

import Server.PassiveTask;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by KUBA on 2016-08-15.
 */
public class ListCommand extends Command{

    public ListCommand(String[] args, SessionManager sessionManager){
        super(sessionManager);
        if(args.length != 0){
            throw new IllegalArgumentException("501 Syntax error in parameters or arguments.");
        }
    }

    @Override
    public String execute(){
        InputStream inputStream = IOUtils.toInputStream(getListOfFiles(), Charset.defaultCharset());
        PassiveTask writeTask = new PassiveTask("write");
        writeTask.setInputStream(inputStream);
        try{
            getSessionManager().getBlockingQueue().put(writeTask);
        } catch (InterruptedException e){
            System.out.println("Error when putting LIST task on queue!");
        }
        return "150 Opening ASCII mode data connection for "  + getRelativePath();
    }

    private String getRelativePath(){
        Path absolutePath = getSessionManager().getCurrentDirectory();
        Path pathBase = Paths.get(".");
        return pathBase.relativize(absolutePath).toString();
    }

    private String getListOfFiles(){
        File directory = getSessionManager().getCurrentDirectory().toFile();
        StringBuilder stringBuilder = new StringBuilder();
        File[] listFiles = directory.listFiles();
        if(listFiles != null){
            for(File file : listFiles){
                appendOwnerAndDirectory(stringBuilder, file);
                appendSize(stringBuilder, file);
                appendTime(stringBuilder, file);
                appendName(stringBuilder, file);
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }



    private void appendName(StringBuilder stringBuilder, File file){
        stringBuilder.append(file.getName());
        stringBuilder.append("\t");
    }

    private void appendSize(StringBuilder stringBuilder, File file){
        stringBuilder.append(file.length());
        stringBuilder.append("\t");
    }

    private void appendTime(StringBuilder stringBuilder, File file){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM\tdd\tyyyy");
        Date date = new Date(file.lastModified());
        String formatDate = simpleDateFormat.format(date);
        stringBuilder.append(formatDate);
        stringBuilder.append("\t");
    }

    private void appendOwnerAndDirectory(StringBuilder stringBuilder, File file){
        if(file.isDirectory()){
            stringBuilder.append("d");
            stringBuilder.append(directoryPermissions(file));
        }
        else{
            stringBuilder.append("-");
            stringBuilder.append(filePermissions(file));
        }
        stringBuilder.append("\t");
    }

    private String directoryPermissions(File file){
        return "rw-rw";
    }

    private String filePermissions(File file){
        return "rw-rw";
    }
}
