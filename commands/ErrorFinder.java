package commands;

public abstract class ErrorFinder {
   public static String getError(String query){
      /*Attempt to find something wrong with incoming query*/
      /*Becuase I use regex it is more difficult to find exactly what's wrong with the query*/
      if(!query.contains(";")) return "Semi colon missing at end of line";
      if(unMatchedBrackets(query)) return "Unmatched brackets";
      if(unMatchedQuotes(query)) return "Unmatched quotation marks";
      return "Invalid query";
   }
   private static boolean unMatchedBrackets(String query) {
      int count = 0;
      for (char c : query.toCharArray()) {
         if (c == '(') count++;
         if (c == ')') count--;
      }
      count = Math.abs(count);
      return count % 2 == 1;
   }

   private static boolean unMatchedQuotes(String query) {
      int count = 0;
      for (char c : query.toCharArray()) {
         if (c == '\'') count++;
      }
      return count % 2 == 1;
   }


}
