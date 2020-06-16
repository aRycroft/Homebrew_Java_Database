package commands;

public abstract class CommandFactory {
   public static Command createCommand(String query) throws CommandNotFoundException{
      String[] splitQuery = query.split(" ");
      if(splitQuery.length == 1){
         throw new CommandNotFoundException("Empty command");
      }
      int i = 0;
      while(splitQuery[i].equals("")) i++;
      String commandType = splitQuery[i].toUpperCase();;
      switch (commandType) {
         case "CREATE":
            return new CreateCommand(query);
         case "USE":
            return new UseCommand(query);
         case "DROP":
            return new DropCommand(query);
         case "ALTER":
            return new AlterCommand(query);
         case "INSERT":
            return new InsertCommand(query);
         case "SELECT":
            return new SelectCommand(query);
         case "UPDATE":
            return new UpdateCommand(query);
         case "DELETE":
            return new DeleteCommand(query);
         case "JOIN":
            return new JoinCommand(query);
      }
      throw new CommandNotFoundException("Can't find the command.");
   }
}
