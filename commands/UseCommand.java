package commands;

import database.DBHandlerInterface;
import database.ImpossibleCommandException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class UseCommand extends Command {

   protected UseCommand(String query) {
      super(query);
   }

   @Override
   public String executeCommand(DBHandlerInterface database) throws ImpossibleCommandException, MyParseException {
      Pattern p = Pattern.compile("^USE\\s*+("+RegEnums.NAME.getRegex()+")\\s*;$", Pattern.CASE_INSENSITIVE);
      Matcher m = p.matcher(query);
      if(m.find()){
         String databaseName = m.group(1);
         database.setCurrentDatabase(databaseName);
         return "OK";
      }else throw new MyParseException(ErrorFinder.getError(query));
   }
}
