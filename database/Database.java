package database;

import java.util.HashMap;

class Database {
   final private String name;
   final private HashMap<String, Table> tablesInDatabase;

   public Database(String name){
      tablesInDatabase = new HashMap<String, Table>();
      this.name = name;
   }

   protected String getName(){
      return name;
   }

   protected void addTable(String name) throws ImpossibleCommandException {
      if(tablesInDatabase.containsKey(name)) throw new ImpossibleCommandException("table " + name +" already exists");
      tablesInDatabase.put(name, new Table());
   }

   protected void dropTable(String name) throws ImpossibleCommandException{
      if(tablesInDatabase.containsKey(name)) tablesInDatabase.remove(name);
      else throw new ImpossibleCommandException(name + " table doesn't exist in database");
   }

   protected Table getTableByName(String name) throws ImpossibleCommandException{
      if(!tablesInDatabase.containsKey(name)) throw new ImpossibleCommandException("table " + name +" does not exist");
      else return tablesInDatabase.get(name);
   }
}
