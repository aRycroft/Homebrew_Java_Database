package commands;

import database.DBHandlerInterface;
import database.DataStorageException;
import database.ImpossibleCommandException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeleteCommand extends Command {

   protected DeleteCommand(String query) {
      super(query);
   }

   @Override
   public String executeCommand(DBHandlerInterface database) throws ImpossibleCommandException, MyParseException, DataStorageException {
      Pattern p = Pattern.compile("DELETE\\s+FROM\\s+("
              +RegEnums.NAME.getRegex()+")"
              +RegEnums.WHERESTATEMENT.getRegex(), Pattern.CASE_INSENSITIVE);
      Matcher m = p.matcher(query);
      if(m.find()) {
         String table = m.group(1);
         database.deleteValues(table, getConditions(), true);
         return "OK";
      }else throw new MyParseException(ErrorFinder.getError(query));
   }
}
