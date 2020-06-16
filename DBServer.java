import commands.CommandFactory;
import database.DBHandler;
import database.DBHandlerInterface;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class DBServer {
   final static char EOT = 4;
   final public DBHandlerInterface handler;

   public DBServer(){
      handler = new DBHandler();
   }

   public static void main(String[] args) {
      DBServer server = new DBServer();
      while (true) {
         try {
            server.waitForConnection(8888);
         }
         catch (BindException ignored){}
         catch (Exception e) {
            System.err.println(e);
         }
      }
   }

   private void waitForConnection(int portNumber) throws IOException{
      ServerSocket ss = new ServerSocket(portNumber);
      System.out.println("Server Listening");
      Socket socket = ss.accept();
      while (socket.isConnected()) {
         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
         String line = in.readLine();
         out.write(queryDatabase(line) + "\n" + EOT + "\n");
         out.flush();
      }
   }

   private String queryDatabase(String line){
      try {
         return CommandFactory.createCommand(line).executeCommand(handler);
      } catch (Exception e){
         return "ERROR: " + e.getMessage();
      }
   }
}
