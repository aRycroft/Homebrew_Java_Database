package database;

public interface DBStorageInterface {
   void getAllFromFile(DBHandlerInterface database)throws DataStorageException;
   void addDatabaseFolder(String name)throws DataStorageException;
   void removeDatabaseFolder(String name)throws DataStorageException;
   void addTableFile(String currentDb, String name)throws DataStorageException;
   void removeTableFile(String currentDb, String name)throws DataStorageException;
   void updateTableContents(String currentDb, String table, String data)throws DataStorageException;
   }
