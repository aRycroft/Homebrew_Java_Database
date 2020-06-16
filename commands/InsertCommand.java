package commands;

import database.DBHandlerInterface;
import database.DataStorageException;
import database.ImpossibleCommandException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class InsertCommand extends Command {

   protected InsertCommand(String query) {
      super(query);
   }

   @Override
   public String executeCommand(DBHandlerInterface database) throws ImpossibleCommandException, MyParseException, DataStorageException {
      Pattern p = Pattern.compile("INSERT\\s+INTO\\s+("+RegEnums.NAME.getRegex()+")\\s+VALUES\\s*" +
              "\\(\\s*"+RegEnums.VALUELIST.getRegex()+"\\s*\\)\\s*;", Pattern.CASE_INSENSITIVE);
      Matcher m = p.matcher(query);
      if(m.find()){
         String table = m.group(1);
         String values = m.group(2);
         database.insertValues(table, values.split("\\s*,\\s*"), false, true);
         return "OK";
      }else throw new MyParseException(ErrorFinder.getError(query));
   }
}
