package commands;

public enum RegEnums {
   NAME ("[a-zA-Z0-9_.]+"),
   STRING ("'[a-zA-Z0-9 ]*'"),
   BOOL ("true|false"),
   INT ("[0-9]+"),
   FLOAT ("[0-9]+.[0-9]+"),
   VALUE ("(?:("+STRING.getRegex()+")|("+BOOL.getRegex()+")|("+INT.getRegex()+")|("+FLOAT.getRegex()+"))"),
   ATTRIBLIST ("((\\s*"+NAME.getRegex()+"\\s*,)*(\\s*"+NAME.getRegex()+"\\s*))"),
   VALUELIST ("((\\s*"+VALUE.getRegex()+"\\s*,)*(\\s*"+VALUE.getRegex()+"\\s*))"),
   NAMEVALUELIST ("(\\s*"+NAME.getRegex()+"\\s*=\\s*"+VALUE.getRegex()+"\\s*,\\s*)*\\s*" +
           "(\\s*"+NAME.getRegex()+"\\s*=\\s*"+VALUE.getRegex()+"\\s*)"),
   OPERATOR ("(==|!=|<|>|<=|>=|LIKE)"),
   WHERECLAUSE (NAME.getRegex()+"\\s*"+OPERATOR.getRegex()+"\\s*"+VALUE.getRegex()),
   WHERESTATEMENT ("\\s+WHERE\\s+(?:[()]*"+WHERECLAUSE.getRegex()+"\\s*[()]*\\s*(AND|OR)*\\s*[()]*)+\\s*;");

   final String data;

   RegEnums(String regex) {
      data = regex;
   }

   public String getRegex(){
      return data;
   }
}
