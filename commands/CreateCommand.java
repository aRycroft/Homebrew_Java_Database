package commands;

import database.DBHandlerInterface;
import database.DataStorageException;
import database.ImpossibleCommandException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CreateCommand extends Command {

   protected CreateCommand(String query) {
      super(query);
   }

   @Override
   public String executeCommand(DBHandlerInterface database) throws ImpossibleCommandException, MyParseException, DataStorageException {
      Pattern p = Pattern.compile("CREATE\\s*(TABLE|DATABASE)\\s*([a-zA-Z0-9]+)\\s*;" +
              "|CREATE\\s*TABLE\\s*("+RegEnums.NAME.getRegex()+")\\s*\\" +
              "("+RegEnums.ATTRIBLIST.getRegex()+"\\)\\s*;", Pattern.CASE_INSENSITIVE);
      Matcher m = p.matcher(query);
      if(m.find()){
         String itemName, tableOrDatabase, attribs;
         /*If group 1 is null then table is being created with columns*/
         if(m.group(1) != null){
            tableOrDatabase = m.group(1).toUpperCase();
            itemName = m.group(2);
            if(tableOrDatabase.equals("DATABASE")) database.addDatabase(itemName, true);
            else database.addTable(itemName, true);
         }
         else{
            itemName = m.group(3);
            attribs = m.group(4);
            addTableWithColumns(database, itemName, attribs);
         }
         return "OK";
      } else throw new MyParseException(ErrorFinder.getError(query));
   }

   private void addTableWithColumns(DBHandlerInterface database, String tableName, String attribs) throws ImpossibleCommandException, DataStorageException {
      String[] attribList = attribs.split("\\s*,\\s*");
      database.addTable(tableName, true);
      for(int i = 0; i < attribList.length - 1; i++){
         String column = attribList[i];
         database.addColumnToTable(tableName, column, false);
      }
      /*Only save to file after all columns have been added*/
      database.addColumnToTable(tableName, attribList[attribList.length - 1], true);
   }
}
