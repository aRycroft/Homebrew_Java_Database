package commands;

import java.util.ArrayList;

public abstract class QueryReturnFormatter {
   static String displaySelectResults(String results){
      if(results.equals("id\n")) return "Empty Table";
      StringBuilder formattedResponse = new StringBuilder();
      ArrayList<String> responseList = new ArrayList<>();
      String[] rows = results.split("\n");
      String[] columns = rows[0].split(",");
      int[] maxSize = new int[columns.length];
      for (String row : rows) {
         String[] vals = row.split(",", -1);
         for (int j = 0; j < vals.length; j++) {
            String val = vals[j].replace("'", "");
            maxSize[j] = Math.max(val.length(), maxSize[j]);
            responseList.add(val);
         }
      }
      /*Format all strings based on maxColumn size*/
      int i = 0;
      for(String str: responseList){
         String format = "%-"+(maxSize[i++] + 2) +"s";
         formattedResponse.append(String.format(format, str));
         if(i % columns.length == 0){
            formattedResponse.append("\n");
            i = 0;
         }
      }
      return formattedResponse.toString();
   }

}
