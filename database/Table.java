package database;

import java.util.ArrayList;
import java.util.ListIterator;

class Table {
   final private ArrayList<Entry> records;
   final private ArrayList<String> columns;
   private int currentId;

   protected Table(){
      records = new ArrayList<Entry>();
      columns = new ArrayList<String>();
      columns.add("id");
      currentId = 1;
   }

   protected ArrayList<String> getColumns(){
      return columns;
   }

   protected ArrayList<Entry> getRecords(){
      return records;
   }

   protected void addEntry(Entry e){
      records.add(e);
      e.addToEntry("id", Integer.toString(currentId++));
   }

   protected void addColumnToTable(String name){
      if(!columns.contains(name)) {
         columns.add(name);
         for (Entry e : records) {
            e.addToEntry(name, null);
         }
      }
   }

   protected void removeColumnFromTable(String name){
      columns.remove(name);
      for(Entry e: records){
         e.removeFromEntry(name);
      }
   }

   protected void insertValuesIntoTable(String[] newValues) throws ImpossibleCommandException{
      if(columns.size() - 1 != newValues.length) throw new ImpossibleCommandException("Insertion columns mismatch.");
      Entry entry = new Entry();
      entry.addToEntry("id", Integer.toString(currentId++));
      for(int i = 0; i < newValues.length; i++){
         String column = columns.get(i + 1);
         entry.addToEntry(column, newValues[i]);
      }
      records.add(entry);
   }

   protected void insertValuesIntoTableWithId(String[] newValues) throws ImpossibleCommandException{
      if(columns.size() != newValues.length) throw new ImpossibleCommandException("Insertion columns mismatch.");
      Entry entry = new Entry();
      currentId++;
      for(int i = 0; i < newValues.length; i++){
         String column = columns.get(i);
         entry.addToEntry(column, newValues[i]);
      }
      records.add(entry);
   }

   protected String selectValues(String[] values, String conditions) throws ImpossibleCommandException{
      StringBuilder response = new StringBuilder();
      if(values[0].equals("*")) values = columns.toArray(new String[0]);
      /*Add columns with , seperator*/
      for(int i = 0; i < values.length - 1; i++) {
         response.append(values[i]).append(",");
      }
      response.append(values[values.length - 1]);
      response.append("\n");
      /*Add records if they meet conditions*/
      for(Entry e: records) {
         if (DBCalculator.checkAgainstConditions(e, conditions)) {
            for (int i = 0; i < values.length - 1; i++) {
               response.append(e.getColumnData(values[i])).append(",");
            }
            response.append(e.getColumnData(values[values.length - 1]));
            response.append("\n");
         }
      }
      return response.toString();
   }

   protected void updateValues(String[] toUpdate, String conditions) throws ImpossibleCommandException{
      for(Entry e: records){
         if(DBCalculator.checkAgainstConditions(e, conditions)){
            for(int i = 0; i < toUpdate.length; i += 2){
               e.changeEntry(toUpdate[i], toUpdate[i + 1]);
            }
         }
      }
   }

   protected void removeValues(String conditions) throws ImpossibleCommandException{
      ListIterator<Entry> itr = records.listIterator();
      while(itr.hasNext()){
         if(DBCalculator.checkAgainstConditions(itr.next(), conditions)){
            itr.remove();
         }
      }
   }
}
