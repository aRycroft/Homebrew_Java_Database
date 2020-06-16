package commands;

import database.DBHandlerInterface;
import database.DataStorageException;
import database.ImpossibleCommandException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateCommand extends Command {

   protected UpdateCommand(String query) {
      super(query);
   }

   @Override
   public String executeCommand(DBHandlerInterface database) throws ImpossibleCommandException, MyParseException, DataStorageException {
      Pattern p = Pattern.compile("UPDATE\\s+("+RegEnums.NAME.getRegex()+")\\s+SET\\s+("+RegEnums.NAMEVALUELIST.getRegex()
              +")("+RegEnums.WHERESTATEMENT.getRegex()+"|\\s*;)", Pattern.CASE_INSENSITIVE);
      Matcher m = p.matcher(query);
      if(m.find()){
         String table = m.group(1);
         String values = m.group(2);
         /*Split on comma and = sign*/
         String[] valueSplit = values.split("\\s*(?:=|,)\\s*");
         database.updateValues(table, valueSplit, getConditions(), true);
         return "OK";
      }else throw new MyParseException(ErrorFinder.getError(query));
   }
}
