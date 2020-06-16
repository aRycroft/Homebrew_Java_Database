package commands;

import database.DBHandlerInterface;
import database.DataStorageException;
import database.ImpossibleCommandException;

public abstract class Command {
   final String query;

   protected Command(String query) {
      this.query = query;
   }

   public abstract String executeCommand(DBHandlerInterface database) throws ImpossibleCommandException, MyParseException, DataStorageException;

   String getConditions(){
      int conPosition = query.lastIndexOf("WHERE");
      if(conPosition == -1) conPosition = query.lastIndexOf("where");
      String result = "";
      if(conPosition != -1) result = query.substring(conPosition + 5);
      return result;
   }
}
