package Server.Database;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by KUBA on 2016-08-10.
 */

//owners

public class FileUser {
    public static final String FILE_ID_COLNAME = "file_id";
    public static final String OWNER_ID_COLNAME = "owner_id";

    @DatabaseField(generatedId = true)
    Integer id;

    @DatabaseField(foreign = true, columnName = OWNER_ID_COLNAME)
    private User user;

    @DatabaseField(foreign = true, columnName = FILE_ID_COLNAME)
    File file;

    public FileUser(){
    }

    public FileUser(File file, User user){
        this.file = file;
        this.user = user;
    }

    public User getUser(){
        return user;
    }
}
