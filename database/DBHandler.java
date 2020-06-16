package database;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHandler implements DBHandlerInterface {
   private Database currentDatabase;
   final private HashMap<String, Database> databases;
   private DBStorageInterface dataStorage;

   public DBHandler(){
      currentDatabase = null;
      databases = new HashMap<String, Database>();
      try {
         dataStorage = new DBStorage("DataStore");
         dataStorage.getAllFromFile(this);
      } catch (DataStorageException e){
         System.err.print(e);
      }
   }

   @Override
   public void addDatabase(String name, boolean saveToFile) throws ImpossibleCommandException, DataStorageException {
      if(databases.containsKey(name)) throw new ImpossibleCommandException( name + " already exists");
      databases.put(name, new Database(name));
      if(saveToFile) dataStorage.addDatabaseFolder(name);
   }

   @Override
   public void dropDatabase(String name, boolean saveToFile) throws ImpossibleCommandException, DataStorageException {
      if(databases.containsKey(name)) {
         databases.remove(name);
         if(saveToFile) dataStorage.removeDatabaseFolder(name);
         setCurrentDatabase(null);
      }
      else throw new ImpossibleCommandException(name + " database doesn't exist.");
   }

   @Override
   public void setCurrentDatabase(String name) throws ImpossibleCommandException {
      if(name == null) currentDatabase = null;
      else if(!databases.containsKey(name)) throw new ImpossibleCommandException("Unknown database");
      currentDatabase = databases.get(name);
   }

   @Override
   public void addTable(String name, boolean saveToFile) throws ImpossibleCommandException, DataStorageException {
      if(currentDatabase == null) throw new ImpossibleCommandException("No database in use.");
      if(saveToFile)dataStorage.addTableFile(currentDatabase.getName(), name);
      currentDatabase.addTable(name);
   }

   @Override
   public void dropTable(String name, boolean saveToFile) throws ImpossibleCommandException, DataStorageException {
      if(currentDatabase == null) throw new ImpossibleCommandException("No database in use.");
      if(saveToFile)dataStorage.removeTableFile(currentDatabase.getName(), name);
      currentDatabase.dropTable(name);
   }

   @Override
   public void addColumnToTable(String tableName, String columnName, boolean saveToFile) throws ImpossibleCommandException, DataStorageException {
      if (columnName == null || tableName == null) throw new ImpossibleCommandException("Column or table name is null");
      getTableByName(tableName).addColumnToTable(columnName);
      if(saveToFile) {
         dataStorage.updateTableContents(currentDatabase.getName(), tableName,selectValues(tableName, new String[]{"*"}, ""));
      }
   }

   @Override
   public void removeColumnFromTable(String tableName, String columnName, boolean saveToFile) throws ImpossibleCommandException, DataStorageException {
      getTableByName(tableName).removeColumnFromTable(columnName);
      if(saveToFile) {
         dataStorage.updateTableContents(currentDatabase.getName(), tableName,selectValues(tableName, new String[]{"*"}, ""));
      }
   }

   @Override
   public void insertValues(String tableName, String[] values, boolean withId, boolean saveToFile) throws ImpossibleCommandException, DataStorageException {
      if(withId) getTableByName(tableName).insertValuesIntoTableWithId(values);
      else getTableByName(tableName).insertValuesIntoTable(values);
      if(saveToFile){
         dataStorage.updateTableContents(currentDatabase.getName(), tableName,selectValues(tableName, new String[]{"*"}, ""));
      }
   }

   @Override
   public String selectValues(String tableName, String[] values, String conditions) throws ImpossibleCommandException {
      return getTableByName(tableName).selectValues(values, conditions);
   }

   @Override
   public void updateValues(String tableName, String[] toUpdate, String conditions, boolean saveToFile) throws ImpossibleCommandException, DataStorageException {
      getTableByName(tableName).updateValues(toUpdate, conditions);
      if(saveToFile) {
         dataStorage.updateTableContents(currentDatabase.getName(), tableName, selectValues(tableName, new String[]{"*"}, ""));
      }
   }

   @Override
   public void deleteValues(String tableName, String conditions, boolean saveToFile) throws ImpossibleCommandException, DataStorageException {
      getTableByName(tableName).removeValues(conditions);
      if(saveToFile) {
         dataStorage.updateTableContents(currentDatabase.getName(), tableName, selectValues(tableName, new String[]{"*"}, ""));
      }
   }

   @Override
   public String joinTables(String table1, String table2, String attrib1, String attrib2) throws ImpossibleCommandException {
      String[] tables = {table1, table2};
      String[] attribs = {attrib1, attrib2};
      Table joinTable = new Table();
      ArrayList<Entry> records = new ArrayList<Entry>();
      for(int i = 0; i < 2; i++) {
         Table table = getTableByName(tables[i]);
         if (!table.getColumns().contains(attribs[i])) {
            throw new ImpossibleCommandException("table " + tables[i] + " doesn't contain " + attribs[i]);
         }
         /*Add columns to new jointable*/
         for (int j = 1; j < table.getColumns().size(); j++) {
            joinTable.addColumnToTable(tables[i] + "." + table.getColumns().get(j));
         }
         /*Then add records by concatenating entries*/
         if(records.isEmpty()) records = table.getRecords();
         else {
            for (Entry e1 : records) {
               for (Entry e2 : table.getRecords()) {
                  if (e1.getColumnData(attrib1).equals(e2.getColumnData(attrib2))) {
                     joinTable.addEntry(e1.concatEntry(e2, tables[0], tables[1], attrib1));
                  }
               }
            }
         }
      }
      return joinTable.selectValues(new String[]{"*"}, "").replace("'", "");
   }

   private Table getTableByName(String name) throws ImpossibleCommandException {
      if(currentDatabase == null) throw new ImpossibleCommandException("No database in use");
      return currentDatabase.getTableByName(name);
   }
}
