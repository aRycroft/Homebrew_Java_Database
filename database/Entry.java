package database;

import java.util.LinkedHashMap;

class Entry {
   final private LinkedHashMap<String, String> records;
   protected Entry(){
      records = new LinkedHashMap<String, String>();
   }
   protected void addToEntry(String column, String data){
      if(data == null) records.put(column, "");
      else records.put(column, data);
   }

   protected void changeEntry(String column, String data){
      records.replace(column, data);
   }

   protected void removeFromEntry(String column){
      records.remove(column);
   }

   protected LinkedHashMap<String, String> getAllData(){
      return records;
   }

   protected String getColumnData(String column) throws ImpossibleCommandException{
      if(!records.containsKey(column)) throw new ImpossibleCommandException(column + " attribute does not exist");
      return records.get(column);
   }

   /*Used when joining tables*/
   protected Entry concatEntry(Entry secondEntry, String tableName1, String tableName2, String onCondition){
      Entry newEntry = new Entry();
      LinkedHashMap<String, String> newRecords = secondEntry.getAllData();
      for(String column: records.keySet()){
         if(!column.equals(onCondition)) {
            String data = records.get(column);
            newEntry.addToEntry(tableName1 + "." + column, data);
         }
      }
      for(String column: newRecords.keySet()){
         if(!column.equals(onCondition)) {
            String data = newRecords.get(column);
            newEntry.addToEntry(tableName2 + "." + column, data);
         }
      }
      return newEntry;
   }

}
