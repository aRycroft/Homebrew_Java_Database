package database;

import java.io.*;
public class DBStorage implements DBStorageInterface {

   final private File dataDirectory;
   final private String extension = ".csv";

   public DBStorage(String folderName) throws DataStorageException{
      dataDirectory = new File(folderName + File.separator);
      if(!dataDirectory.exists()) {
         if(!dataDirectory.mkdir()){
            throw new DataStorageException("Failed to create data directory " + folderName + File.separator);
         }
      }
   }

   public void addDatabaseFolder(String name) throws DataStorageException{
      String dir = dataDirectory.getPath() + File.separator + name + File.separator;
      File dbDir = new File(dir);
      if(!dbDir.exists()) {
         if(!dbDir.mkdir()){
            throw new DataStorageException("Failed to create database folder " + dbDir.getAbsolutePath());
         }
      }
   }

   public void removeDatabaseFolder(String name) throws DataStorageException{
      String dir = dataDirectory.getPath() + File.separator + name + File.separator;
      File dbDir = new File(dir);
      String[] tablesInDb = dbDir.list();
      if(tablesInDb != null) {
         for (String table : tablesInDb) {
            removeTableFile(name, table);
         }
      }
      if(!dbDir.delete()){
         throw new DataStorageException("Failed to delete database folder " + dbDir.getAbsolutePath());
      }
   }

   @Override
   public void addTableFile(String currentDb, String name) throws DataStorageException{
      String dir = dataDirectory.getPath() + File.separator + currentDb + File.separator + name;
      if(!dir.endsWith(extension)) dir += extension;
      File tableFile = new File(dir);
      try {
         if(!tableFile.exists()) {
            if(!tableFile.createNewFile()){
               throw new DataStorageException("Failed to create table file " + tableFile.getAbsolutePath());
            }
         }
      } catch (IOException ioe){
         throw new DataStorageException(ioe.getMessage());
      }
   }

   @Override
   public void removeTableFile(String currentDb, String name) throws DataStorageException {
      String dir = dataDirectory.getPath() + File.separator + currentDb + File.separator + name;
      if(!dir.endsWith(extension)) dir += extension;
      File tableFile = new File(dir);
      if(!tableFile.delete()){
         throw new DataStorageException("Failed to delete table file " + tableFile.getAbsolutePath());
      }
   }

   @Override
   public void updateTableContents(String currentDb, String table, String data) throws DataStorageException{
      try{
         String dir = dataDirectory.getPath() + File.separator + currentDb + File.separator + table + extension;
         FileWriter fileWriter = new FileWriter(dir);
         BufferedWriter writer = new BufferedWriter(fileWriter);
         writer.write(data);
         writer.flush();
         writer.close();
         fileWriter.close();
      } catch (IOException ioe){
         throw new DataStorageException(ioe.getMessage());
      }
   }

   @Override
   public void getAllFromFile(DBHandlerInterface database)throws DataStorageException {
      try {
         File[] databases = dataDirectory.listFiles();
         if(databases == null) return;
         for(File databaseFolder: databases){
            database.addDatabase(databaseFolder.getName(), false);
            database.setCurrentDatabase(databaseFolder.getName());
            getTableData(database, databaseFolder);
         }
         database.setCurrentDatabase(null);
      }
      catch (ImpossibleCommandException e){
         throw new DataStorageException(e.getMessage());
      }
   }


   private void getTableData(DBHandlerInterface database, File databaseFolder) throws DataStorageException{
      File[] filesInDatabase = databaseFolder.listFiles();
      if(filesInDatabase == null) return;
      for(File table: filesInDatabase){
         try {
            database.addTable(table.getName().replace(extension, ""), false);
            getEntryDataFromTable(database, table);
         } catch (ImpossibleCommandException e){
            throw new DataStorageException(e.getMessage());
         }
      }
   }

   private void getEntryDataFromTable(DBHandlerInterface database, File table) throws DataStorageException{
      try {
         FileReader fileReader = new FileReader(table);
         BufferedReader reader = new BufferedReader(fileReader);
         String tableName = table.getName().replace(extension, "");

         String columnLine = reader.readLine();
         /*Empty file*/
         if(columnLine == null) return;
         for(String column: columnLine.split(",")){
            database.addColumnToTable(tableName, column, false);
         }
         String line = reader.readLine();
         while(line != null) {
            database.insertValues(tableName, line.split(","),true, false);
            line = reader.readLine();
         }
         reader.close();
         fileReader.close();
      } catch (ImpossibleCommandException| IOException  e){
         throw new DataStorageException(e.getMessage());
      }
   }
}
