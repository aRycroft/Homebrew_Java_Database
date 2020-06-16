package commands;

import database.DBHandlerInterface;
import database.ImpossibleCommandException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinCommand extends Command{

   protected JoinCommand(String query) {
      super(query);
   }

   @Override
   public String executeCommand(DBHandlerInterface database) throws ImpossibleCommandException, MyParseException {
      Pattern p = Pattern.compile("JOIN\\s+("+RegEnums.NAME.getRegex()+")\\s+AND\\s+("+RegEnums.NAME.getRegex()+")\\s+" +
              "ON\\s+("+RegEnums.NAME.getRegex()+")\\s+AND\\s+("+RegEnums.NAME.getRegex()+")\\s*;", Pattern.CASE_INSENSITIVE);
      Matcher m = p.matcher(query);
      if(m.find()){
         String table1 = m.group(1);
         String table2 = m.group(2);
         String attrib1 = m.group(3);
         String attrib2 = m.group(4);
         String joinedResults = database.joinTables(table1, table2, attrib1, attrib2);
         return QueryReturnFormatter.displaySelectResults(joinedResults);
      }else throw new MyParseException(ErrorFinder.getError(query));
   }
}
