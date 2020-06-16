package commands;

import database.DBHandlerInterface;
import database.DataStorageException;
import database.ImpossibleCommandException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DropCommand extends Command {

   protected DropCommand(String query) {
      super(query);
   }

   @Override
   public String executeCommand(DBHandlerInterface database) throws ImpossibleCommandException, MyParseException, DataStorageException {
      Pattern p = Pattern.compile("DROP\\s*(TABLE|DATABASE)\\s*("
              +RegEnums.NAME.getRegex()+")\\s*;", Pattern.CASE_INSENSITIVE);
      Matcher m = p.matcher(query);
      if(m.find()){
         String tableOrDatabase = m.group(1).toUpperCase();
         String itemName = m.group(2);
         if(tableOrDatabase.equals("TABLE")) database.dropTable(itemName, true);
         else database.dropDatabase(itemName, true);
         return "OK";
      } else throw new MyParseException(ErrorFinder.getError(query));
   }
}
