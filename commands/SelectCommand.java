package commands;

import database.DBHandlerInterface;
import database.ImpossibleCommandException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectCommand extends Command {

   protected SelectCommand(String query) {
      super(query);
   }

   @Override
   public String executeCommand(DBHandlerInterface database) throws ImpossibleCommandException, MyParseException {
      Pattern p = Pattern.compile("SELECT\\s+(\\*|"+RegEnums.ATTRIBLIST.getRegex()+")\\s+FROM\\s+" +
              "("+RegEnums.NAME.getRegex()+")(?:\\s*;|"+RegEnums.WHERESTATEMENT.getRegex()+")", Pattern.CASE_INSENSITIVE);
      Matcher m = p.matcher(query);
      if(m.find()){
         String values = m.group(1);
         String tableName = m.group(5);
         String selectedVals = database.selectValues(tableName, values.split("\\s*,\\s*"), getConditions());
         return QueryReturnFormatter.displaySelectResults(selectedVals);
      } throw new MyParseException(ErrorFinder.getError(query));
   }
}
