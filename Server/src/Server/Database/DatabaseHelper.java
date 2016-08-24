package Server.Database;

import Server.Command.ChmodCommand;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.*;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KUBA on 2016-08-07.
 */
public class DatabaseHelper {


    private static DatabaseHelper instance = new DatabaseHelper();
    private final static String DATABASE_URL = "jdbc:mysql://localhost:3306/demo";
    private final static String admin = "root";
    private final static String password = "1234";
    private Dao<Group, Integer> groupDao;
    private Dao<User, Integer> userDao;
    private Dao<File, Integer> fileDao;
    private Dao<GroupUser, Integer> groupUserDao;
    private Dao<FileUser, Integer> fileUserDao;
    JdbcConnectionSource connectionSource;


    private DatabaseHelper(){
        try {
            connectionSource = null;
            connectionSource = new JdbcConnectionSource(DATABASE_URL, admin, password);
            userDao = DaoManager.createDao(connectionSource, User.class);
            groupDao = DaoManager.createDao(connectionSource, Group.class);
            fileDao = DaoManager.createDao(connectionSource, File.class);
            fileUserDao = DaoManager.createDao(connectionSource, FileUser.class);
            groupUserDao = DaoManager.createDao(connectionSource, GroupUser.class);
            TableUtils.createTableIfNotExists(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, Group.class);
            TableUtils.createTableIfNotExists(connectionSource, File.class);
            TableUtils.createTableIfNotExists(connectionSource, FileUser.class);
            TableUtils.createTableIfNotExists(connectionSource, GroupUser.class);
        }
        catch (SQLException e){
            System.out.println("Connection with database not possible!");
        }
    }

    public static DatabaseHelper getInstance(){
        return instance;
    }

    public User getUser(String name){
        List<User> users = new ArrayList<>();
        try{
            QueryBuilder<User, Integer> qb = userDao.queryBuilder();
            Where where = qb.where();
            SelectArg selectArg = new SelectArg();
            selectArg.setValue(name);
            where.eq(User.NAME_COLNAME, selectArg);
            PreparedQuery<User> preparedQuery = qb.prepare();
            users = userDao.query(preparedQuery);
        } catch (SQLException e){
            System.out.println("Problem with database during retrieving the user!");
        }

        if(users.size() == 0)
            return null;
        return users.get(0);
    }

    public boolean validatePassword(User user, String password){
        if(user.getPassword().equals(password))
            return true;
        else
            return false;
    }

    public boolean canUserWriteToFile(String filePath, User user){

        try {
            System.out.println("Filepath: " + filePath);
            QueryBuilder<File, Integer> fileQB = fileDao.queryBuilder();
            Where<File, Integer> where = fileQB.where();
            SelectArg selectArg = new SelectArg();
            selectArg.setValue(filePath);
            where.eq(File.NAME_COLNAME, selectArg);
            PreparedQuery<File> preparedQuery = fileQB.prepare();
            List<File> files = fileDao.query(preparedQuery);
            File file = files.get(0);
            return file.getGroupWrite() && isUserInGroup(user.getId(), file.getId()) || file.getUserWrite() && checkOwner(user.getId(), file.getId());
        } catch (SQLException e){
            System.out.println("Cannot check permission in database!");
        }
        return false;
    }

    public void changePermissions(String filename, ChmodCommand.Permissions permissions){
        File file = getFile(filename);
        if(file == null){
            System.out.println("Such a file does not exist!");
            return;
        }
        file.setUserRead(permissions.userRead);
        file.setUserWrite(permissions.userWrite);
        file.setGroupRead(permissions.groupRead);
        file.setGroupWrite(permissions.groupWrite);

        try {
            fileDao.update(file);
        } catch (SQLException e){
            System.out.println("Permissions do not changed due to problems with updating!");
        }
    }

    public File getFile(String filename){
        try {
            QueryBuilder<File, Integer> qb = fileDao.queryBuilder();
            Where where = qb.where();
            SelectArg selectArg = new SelectArg();
            selectArg.setValue(filename);
            where.eq(File.NAME_COLNAME, selectArg);
            PreparedQuery<File> preparedQuery = qb.prepare();
            List<File> files = fileDao.query(preparedQuery);

            return files.get(0);
        } catch (SQLException e){
            System.out.println("Cannot find given file!");
        }
        return null;
    }

    public boolean isUserInGroup(int userId, int fileId) throws SQLException{
        //find owner
        User owner = owner(userId, fileId);
        if(owner == null)
            return false;
        int ownerId = owner.getId();

        //look if owner is in the same group as user

        QueryBuilder<GroupUser, Integer> userGroupQB = groupUserDao.queryBuilder();
        Where where1 = userGroupQB.where();
        SelectArg selectArg1 = new SelectArg();
        selectArg1.setValue(userId);
        SelectArg selectArg2 = new SelectArg();
        selectArg2.setValue(fileId);
        where1.eq(GroupUser.USER_ID_COLNAME, selectArg1);
        where1.or();
        where1.eq(GroupUser.USER_ID_COLNAME, selectArg2);
        PreparedQuery<GroupUser> preparedQuery1 = userGroupQB.prepare();
        List<GroupUser>  groupUsers = groupUserDao.query(preparedQuery1);
        if(groupUsers.size() != 0)
            return true;
        else
            return false;
    }

    private User owner(int userId, int fileId){
        try{
            QueryBuilder<FileUser, Integer> fileUserQB = fileUserDao.queryBuilder();
            Where where = fileUserQB.where();
            SelectArg selectArg = new SelectArg();
            selectArg.setValue(userId);
            where.eq(FileUser.FILE_ID_COLNAME, selectArg);
            PreparedQuery<FileUser> preparedQuery = fileUserQB.prepare();
            List<FileUser> fileUsers = fileUserDao.query(preparedQuery);
            if(fileUsers.size() > 0)
                return fileUsers.get(0).getUser();
            else
                return null;
        } catch (SQLException e){
            System.out.println("Cannot find owner!");
        }
        return null;
    }

    private boolean checkOwner(int userId, int fileId){
        User owner = owner(userId, fileId);
        if(owner == null)
            return false;
        return true;
    }

    public boolean deleteFile(String path){
        File file = getFile(path);
        if(file == null){
            System.out.println("Cannot delete file because it does not exist: " + path);
            return false;
        }
        try{
            fileDao.delete(file);
        } catch (SQLException e){
            System.out.println("Problem with file deleting!");
            return false;
        }
        return true;
    }

    public boolean insertNewFileIfNotExists(String filename, User user){
        if(existFile(filename)){
            System.out.println("Cannot insert file because it already exists: " + filename);
            return false;
        }

        File file = new File();
        file.setFilePath(filename);
        file.setUserRead(true);
        file.setUserWrite(true);
        file.setGroupRead(false);
        file.setGroupWrite(false);

        FileUser fileUser = new FileUser(file, user);

        try{
            fileDao.create(file);
            fileUserDao.create(fileUser);
        } catch (SQLException e){
            System.out.println("Problem when saving changes to database!");
            return false;
        }
        return true;
    }

    private boolean existFile(String filename){
        try {
            QueryBuilder<File, Integer> qb = fileDao.queryBuilder();
            Where where = qb.where();
            SelectArg selectArg = new SelectArg();
            selectArg.setValue(filename);
            where.eq(File.NAME_COLNAME, selectArg);
            PreparedQuery<File> preparedQuery = qb.prepare();
            List<File> files = fileDao.query(preparedQuery);

            if(files.size() == 0)
                return false;
            return true;
        } catch (SQLException e){
            System.out.println("Problem with validating operation on database!");
        }
        return true;
    }


    /*

    public void initializeDatabase() throws SQLException{
        connectionSource = null;
        connectionSource = new JdbcConnectionSource(DATABASE_URL, user, password);
        userDao = DaoManager.createDao(connectionSource, User.class);
        groupDao = DaoManager.createDao(connectionSource, Group.class);
        groupUserDao = DaoManager.createDao(connectionSource, GroupUser.class);
        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, Group.class);
        TableUtils.createTableIfNotExists(connectionSource, GroupUser.class);
    }


    public void closeDatabase() throws SQLException, IOException{
        if (connectionSource != null) {
            connectionSource.close();
        }
    }

    public void addUser(String name, String password) throws SQLException {
        if(!existUser(name)){
            userDao.create(new User(name, password));
        }
    }

    public void addGroup(String groupName) throws SQLException{
        if(!existGroup(groupName)){
            groupDao.create(new Group(groupName));
        }
    }

    public void addGroupUser(String name, String groupname) throws SQLException{
        if(!existGroupUser(name, groupname)){

            QueryBuilder<User, Integer> qb = userDao.queryBuilder();
            Where where = qb.where();
            where.eq(User.NAME_COLNAME, name);
            PreparedQuery<User> preparedQuery = qb.prepare();
            List<User> users = userDao.query(preparedQuery);

            QueryBuilder<Group, Integer> qb1 = groupDao.queryBuilder();
            Where where1 = qb1.where();
            where1.eq(Group.GROUPNAME_COLNAME, groupname);
            PreparedQuery<Group> preparedQuery1 = qb1.prepare();
            List<Group> groups = groupDao.query(preparedQuery1);

            groupUserDao.create(new GroupUser(groups.get(0), users.get(0)));
        }
    }

    public void deleteUser(String name) throws SQLException{
        if(existUser(name)){
            DeleteBuilder<GroupUser, Integer> qb1 = groupUserDao.deleteBuilder();
            Where where1 = qb1.where();
            where1.eq(GroupUser.USER_ID_COLNAME, findUserID(name));
            PreparedDelete<GroupUser> preparedDelete1 = qb1.prepare();
            groupUserDao.delete(preparedDelete1);

            DeleteBuilder<User, Integer> qb = userDao.deleteBuilder();
            Where where = qb.where();
            where.eq(User.NAME_COLNAME, name);
            PreparedDelete<User> preparedDelete = qb.prepare();
            userDao.delete(preparedDelete);
        }
    }

    public void deleteGroup(String groupName) throws SQLException{
        if(existGroup(groupName)){

            DeleteBuilder<GroupUser, Integer> qb1 = groupUserDao.deleteBuilder();
            Where where1 = qb1.where();
            where1.eq(GroupUser.GROUP_ID_COLNAME, findGroupID(groupName));
            PreparedDelete<GroupUser> preparedDelete1 = qb1.prepare();
            groupUserDao.delete(preparedDelete1);

            DeleteBuilder<Group, Integer> qb = groupDao.deleteBuilder();
            Where where = qb.where();
            where.eq(Group.GROUPNAME_COLNAME, groupName);
            PreparedDelete<Group> preparedDelete = qb.prepare();
            groupDao.delete(preparedDelete);
        }
    }

    public void deleteGroupUser(String name, String groupname) throws SQLException{
        if(existGroupUser(name, groupname)){
            DeleteBuilder<GroupUser, Integer> qb = groupUserDao.deleteBuilder();
            Where where = qb.where();
            where.eq(GroupUser.GROUP_ID_COLNAME, findGroupID(groupname));
            where.and();
            where.eq(GroupUser.USER_ID_COLNAME, findUserID(name));
            PreparedDelete<GroupUser> preparedDelete = qb.prepare();
            groupUserDao.delete(preparedDelete);
        }
    }

    private boolean existUser(String name) throws SQLException{
        QueryBuilder<User, Integer> qb = userDao.queryBuilder();
        Where where = qb.where();
        where.eq(User.NAME_COLNAME, name);
        PreparedQuery<User> preparedQuery = qb.prepare();
        List<User> users = userDao.query(preparedQuery);

        if(users.size() == 0)
            return false;
        return true;
    }

    private boolean existGroupUser(String name, String groupname) throws SQLException{

        Integer userId = findUserID(name);
        if(userId == null)
            return false;

        Integer groupId = findGroupID(groupname);
        if(groupId == null)
            return false;

        QueryBuilder<GroupUser, Integer> groupuserQB = groupUserDao.queryBuilder();
        Where where = groupuserQB.where();
        where.eq(GroupUser.USER_ID_COLNAME, userId);
        where.and();
        where.eq(GroupUser.GROUP_ID_COLNAME, groupId);
        PreparedQuery<GroupUser> preparedQuery = groupuserQB.prepare();
        List<GroupUser> groupusers = groupUserDao.query(preparedQuery);

        if(groupusers.size() == 0)
            return false;
        return true;
    }

    private boolean existGroup(String groupName) throws SQLException{
        QueryBuilder<Group, Integer> qb = groupDao.queryBuilder();
        Where where = qb.where();
        where.eq(Group.GROUPNAME_COLNAME, groupName);
        PreparedQuery<Group> preparedQuery = qb.prepare();
        List<Group> groups = groupDao.query(preparedQuery);

        if(groups.size() == 0)
            return false;
        return true;
    }

    private Integer findUserID(String name) throws SQLException{
        QueryBuilder<User, Integer> userQB = userDao.queryBuilder();
        Where where = userQB.where();
        where.eq(User.NAME_COLNAME, name);
        PreparedQuery<User> preparedQuery = userQB.prepare();
        List<User> users = userDao.query(preparedQuery);

        if(users.size() == 0)
            return null;

        return users.get(0).getId();
    }

    private Integer findGroupID(String groupName) throws SQLException{
        QueryBuilder<Group, Integer> groupQB = groupDao.queryBuilder();
        Where where1 = groupQB.where();
        where1.eq(Group.GROUPNAME_COLNAME, groupName);
        PreparedQuery<Group> preparedQuery1 = groupQB.prepare();
        List<Group> groups = groupDao.query(preparedQuery1);

        if(groups.size() == 0)
            return null;

        return groups.get(0).getId();
    }*/
}








