package commands;

import database.DBHandlerInterface;
import database.DataStorageException;
import database.ImpossibleCommandException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AlterCommand extends Command {
   protected AlterCommand(String query) {
      super(query);
   }

   @Override
   public String executeCommand(DBHandlerInterface database) throws ImpossibleCommandException, MyParseException, DataStorageException {
      Pattern p = Pattern.compile("ALTER\\s*TABLE\\s*("+RegEnums.NAME.getRegex()+")\\s*(ADD|DROP)\\s*" +
                      "("+RegEnums.NAME.getRegex()+")\\s*;", Pattern.CASE_INSENSITIVE);
      Matcher m = p.matcher(query);
      if(m.find()){
         String tableName = m.group(1);
         String addOrDrop = m.group(2).toUpperCase();
         String columnName = m.group(3);
         if(addOrDrop.equals("ADD")) database.addColumnToTable(tableName, columnName, true);
         else database.removeColumnFromTable(tableName, columnName, true);
      } else throw new MyParseException(ErrorFinder.getError(query));
      return "OK";
   }
}
