package Server.Database;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by KUBA on 2016-08-10.
 */
public class File {

    public final static String ID_COLNAME = "_fileId";
    public final static String NAME_COLNAME = "_filePath";
    public final static String USER_READ= "_userRead";
    public final static String USER_WRITE = "_userWrite";
    public final static String GROUP_READ = "_groupRead";
    public final static String GROUP_WRITE = "_groupPath";

    @DatabaseField(generatedId = true, columnName = ID_COLNAME)
    private Integer fileId;

    @DatabaseField(columnName = NAME_COLNAME)
    private String filePath;


    @DatabaseField(columnName = USER_READ)
    private boolean userRead = true;

    @DatabaseField(columnName = USER_WRITE)
    private boolean userWrite = true;

    @DatabaseField(columnName = GROUP_READ)
    private boolean groupRead = false;

    @DatabaseField(columnName = GROUP_WRITE)
    private boolean groupWrite = false;


    public File(){
    }

    public File(String filePath){
        this.filePath = filePath;
    }

    public Integer getId(){
        return fileId;
    }

    public String getPath(){
        return filePath;
    }

    public boolean getUserRead(){
        return userRead;
    }

    public boolean getUserWrite(){
        return userWrite;
    }

    public boolean getGroupRead(){
        return groupRead;
    }

    public boolean getGroupWrite(){
        return groupWrite;
    }

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public void setUserRead(boolean userRead){
        this.userRead = userRead;
    }

    public void setUserWrite(boolean userWrite){
        this.userWrite = userWrite;
    }

    public void setGroupRead(boolean groupRead){
        this.groupRead = groupRead;
    }

    public void setGroupWrite(boolean groupWrite){
        this.groupWrite = groupWrite;
    }
}