package database;

public interface DBHandlerInterface {
   void addDatabase(String name, boolean saveToFile) throws ImpossibleCommandException, DataStorageException;
   void dropDatabase(String name, boolean saveToFile) throws ImpossibleCommandException, DataStorageException;
   void setCurrentDatabase(String name) throws ImpossibleCommandException;
   void addTable(String name, boolean saveToFile) throws ImpossibleCommandException, DataStorageException;
   void dropTable(String name, boolean saveToFile) throws ImpossibleCommandException, DataStorageException;
   void addColumnToTable(String tableName, String columnName, boolean saveToFile) throws ImpossibleCommandException, DataStorageException;
   void removeColumnFromTable(String tableName, String columnName, boolean saveToFile) throws ImpossibleCommandException, DataStorageException;
   void insertValues(String tableName, String[] values, boolean withId, boolean saveToFile) throws ImpossibleCommandException, DataStorageException;
   String selectValues(String tableName, String[] values, String conditions) throws ImpossibleCommandException;
   void updateValues(String tableName, String[] toUpdate, String conditions, boolean saveToFile) throws ImpossibleCommandException, DataStorageException ;
   void deleteValues(String tableName, String conditions, boolean saveToFile) throws ImpossibleCommandException, DataStorageException;
   String joinTables(String table1, String table2, String attrib1, String attrib2) throws ImpossibleCommandException;
}
