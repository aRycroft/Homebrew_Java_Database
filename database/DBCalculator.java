package database;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DBCalculator {


   public static boolean checkAgainstConditions(Entry entry, String conditions) throws ImpossibleCommandException{
      if(conditions.isBlank()) return true;
      Stack<Boolean> resultsStack = new Stack<Boolean>();
      Stack<String> opsStack = new Stack<String>();
      StringBuilder clause = new StringBuilder();
      for (char currentChar : conditions.toCharArray()) {
         switch (currentChar){
            case '(':
               /*We know if clause isn't blank it must be AND/OR here*/
               if(!clause.toString().isBlank()) opsStack.push(clause.toString());
               /*Also push parenthesis to operators stack*/
               opsStack.push("(");
               /*reset clause*/
               clause = new StringBuilder();
               break;
            case ')':
               /*We know if clause isn't blank it must be WHERE clause i.e (name == 'dave')*/
               if(!clause.toString().isBlank()) resultsStack.push(calculateEntry(entry, clause.toString()));
               /*Until we meet opening parenthesis pop two from results stack, one from opstack and evaluate*/
               while(!opsStack.peek().equals("(")) {
                  resultsStack.push(evaluate(resultsStack.pop(), resultsStack.pop(), opsStack.pop()));
               }
               /*Pop opening parenthesis*/
               opsStack.pop();
               clause = new StringBuilder();
               break;
            case ';':
               /*If there are still results to evaluate in stack*/
               if(resultsStack.size() > 1) resultsStack.push(evaluate(resultsStack.pop(), resultsStack.pop(), opsStack.pop()));
               else return calculateEntry(entry, clause.toString());
               break;
            default:
               clause.append(currentChar);
               break;
         }
      }
      if(resultsStack.size() > 1) throw new ImpossibleCommandException("Mismatched brackets in where clause.");
      return resultsStack.pop();
   }

   private static boolean evaluate(Boolean term1, Boolean term2, String operator) throws ImpossibleCommandException{
      operator = operator.replace(" ", "");
      if(operator.equals("AND")) return term1 && term2;
      if(operator.equals("OR")) return term1 || term2;
      throw new ImpossibleCommandException("Unrecognised operator in where clause" + operator);
   }

   private static boolean calculateEntry(Entry entry, String clause) throws ImpossibleCommandException{
      Pattern p = Pattern.compile("([a-zA-Z0-9]+)\\s*(==|>|<|>=|<=|!=|LIKE)\\s*([a-zA-Z0-9'. ]+)");
      Matcher m = p.matcher(clause);
      if(m.find()) {
         String value = entry.getColumnData(m.group(1));
         String operator = m.group(2);
         String conValue = m.group(3);
         return calculateCondition(value, operator, conValue);
      } else throw new ImpossibleCommandException("Can't parse where clause");
   }

   public static boolean calculateCondition(String value, String operator, String conValue) throws ImpossibleCommandException {
      if(Pattern.matches(getRegString(), conValue)) {
         if(!Pattern.matches(getRegString(), value)) throw new ImpossibleCommandException("Mismatched types in where clause");
         return calculateStrings(value, operator, conValue);
      }
      if(Pattern.matches(getRegBool(), conValue)) {
         if(!Pattern.matches(getRegBool(), value)) throw new ImpossibleCommandException("Mismatched types in where clause");
         return calculateBools(value, operator, conValue);
      }
      if(Pattern.matches(getRegInt(), conValue)) {
         if(!Pattern.matches(getRegInt(), value)) throw new ImpossibleCommandException("Mismatched types in where clause");
         return calculateInts(value, operator, conValue, false);
      }
      if(Pattern.matches(getRegFloat(), conValue)) {
         if(!Pattern.matches(getRegFloat(), value)) throw new ImpossibleCommandException("Mismatched types in where clause");
         return calculateInts(value, operator, conValue, true);
      }
      return false;
   }

   public static boolean calculateStrings(String value, String operator, String conValue) throws ImpossibleCommandException{
      switch (operator){
         case "==":
            return (conValue.compareTo(value) == 0);
         case "!=":
            return (conValue.compareTo(value) != 0);
         case ">":
            return (conValue.compareTo(value) > 0);
         case "<":
            return (conValue.compareTo(value) < 0);
         case ">=":
            return (conValue.compareTo(value) >= 0);
         case "<=":
            return (conValue.compareTo(value) <= 0);
         case "LIKE":
            return value.contains(conValue.replace("'",""));
         default:
            throw new ImpossibleCommandException(operator + " operator not recognised");
      }
   }
   public static boolean calculateBools(String value, String operator, String conValue) throws ImpossibleCommandException{
      switch (operator){
         case "==":
            return conValue.equals(value);
         case "!=":
            return !conValue.equals(value);
         default:
            throw new ImpossibleCommandException("Boolean values cannot use that operator.");
      }

   }
   public static boolean calculateInts(String value, String operator, String conValue, Boolean isFloat) throws ImpossibleCommandException{
      float numValue, numCondition;
      if(isFloat) {
         numValue = Float.parseFloat(value);
         numCondition = Float.parseFloat(conValue);
      }
      else{
         numValue = Integer.parseInt(value);
         numCondition = Integer.parseInt(conValue);
      }
      switch (operator){
         case "==":
            return numValue == numCondition;
         case "!=":
            return numValue != numCondition;
         case ">":
            return numValue > numCondition;
         case "<":
            return numValue < numCondition;
         case ">=":
            return numValue >= numCondition;
         case "<=":
            return numValue <= numCondition;
         default:
            throw new ImpossibleCommandException(operator + " operator not recognised");
      }
   }

   public static String getRegString(){ return "'[a-zA-Z0-9 ]*'";}
   public static String getRegBool(){ return "true|false";}
   public static String getRegInt(){ return "[0-9]+";}
   public static String getRegFloat(){ return "[0-9]+.[0-9]+";}
}
