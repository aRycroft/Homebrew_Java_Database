import java.io.*;
import java.net.*;

public class DBClient
{
    final static char EOT = 4;

    public static void main(String[] args)
    {
        try {
            BufferedReader commandLine = new BufferedReader(new InputStreamReader(System.in));
            Socket socket = new Socket("127.0.0.1", 8888);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //testAll(out, in);
            while(true) handleNextCommand(commandLine, out, in);
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private static void handleNextCommand(BufferedReader commandLine, BufferedWriter out, BufferedReader in)
    {
        try {
            System.out.print("SQL:> ");
            String command = commandLine.readLine();
            out.write(command + "\n");
            out.flush();
            String incoming = in.readLine();
            while( ! incoming.contains("" + EOT + "")) {
                System.out.println(incoming);
                incoming = in.readLine();
            }
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private static String queryDatabase(String query, BufferedWriter out, BufferedReader in){
        try {
            StringBuilder response = new StringBuilder();
            out.write(query + "\n");
            out.flush();
            String incoming = in.readLine();
            response.append(incoming).append("\n");
            while( ! incoming.contains("" + EOT + "")) {
                incoming = in.readLine();
                response.append(incoming).append("\n");
            }
            return response.toString();
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
        return "";
    }

    private static void testAll(BufferedWriter out, BufferedReader in){
        extraTest(out, in);
        createTest(out, in);
        insertTest(out, in);
        updateTest(out, in);
        setupActorsData(out, in);
        robustnessTest(out, in);
        extensionTest(out, in);
        setupActorsData(out, in);
    }

    private static void extraTest(BufferedWriter out, BufferedReader in){
        System.out.println("----EXTRA TESTING----");
        assert(queryDatabase("CREATE DATABASE db;", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("USE db;", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("CREATE TABLE testing;", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("ALTER TABLE testing add name;", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("ALTER TABLE testing add number;", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("UPDATE testing SET name='al',number=2;", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("INSERT INTO testing VALUes ('hello', 50);", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("UPDATE testing SET name='al',number=2;", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("INSERT INTO testing VALUes ('test', 50);", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("UPDATE testing SET name='al',number=2 WHERE (name=='test') OR (number==2);", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("INSERT INTO testing VALUes ('testagain', 50);", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("INSERT INTO testing VALUes ('testagainagain', 50);", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("UPDATE testing SET name='shouldnt happen',number=0 WHERE (number > 2) AND ((name=='test') OR (number==2));", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("UPDATE testing SET name='should happen',number=0 WHERE (number > 2) OR ((name=='al') AND (number==2));", out, in).equals("OK\n" + EOT + "\n"));
        assert (queryDatabase("SELECT * FROM testing;", out, in).equals(
                "id  name           number  \n" +
                "1   should happen  0       \n" +
                "2   should happen  0       \n" +
                "3   should happen  0       \n" +
                "4   should happen  0       \n" +
                "\n" + EOT + "\n"));
        assert(queryDatabase("DELETE FROM testing WHERE number == 0;", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("ALTER TABLE testing drop name;", out, in).equals("OK\n" + EOT + "\n"));
        queryDatabase("DROP DATABASE db;", out, in);
        System.out.println("----EXTRA PASSED----");
    }

    private static void setupActorsData(BufferedWriter out, BufferedReader in){
        queryDatabase("CREATE DATABASE imdb;", out, in);
        queryDatabase("USE imdb;", out, in);
        queryDatabase("DROP TABLE actors;", out, in);
        queryDatabase("DROP TABLE movies;", out, in);
        queryDatabase("DROP TABLE roles;", out, in);
        queryDatabase("DROP DATABASE imdb;", out, in);
        queryDatabase("CREATE DATABASE imdb;", out, in);
        queryDatabase("USE imdb;", out, in);
        queryDatabase("CREATE TABLE actors (name, nationality, awards);", out, in);
        queryDatabase("INSERT INTO actors VALUES ('Hugh Grant', 'British', 3);", out, in);
        queryDatabase("INSERT INTO actors VALUES ('Toni Collette', 'Australian', 12);", out, in);
        queryDatabase("INSERT INTO actors VALUES ('James Caan', 'American', 8);", out, in);
        queryDatabase("INSERT INTO actors VALUES ('Emma Thompson', 'British', 10);", out, in);
        queryDatabase("CREATE TABLE movies (name, genre);", out, in);
        queryDatabase("INSERT INTO movies VALUES ('Mickey Blue Eyes', 'Comedy');", out, in);
        queryDatabase("INSERT INTO movies VALUES ('About a Boy', 'Comedy');", out, in);
        queryDatabase("INSERT INTO movies VALUES ('Sense and Sensibility', 'Period Drama');", out, in);
        queryDatabase("SELECT id FROM movies WHERE name == 'Mickey Blue Eyes';", out, in);
        queryDatabase("SELECT id FROM movies WHERE name == 'About a Boy';", out, in);
        queryDatabase("SELECT id FROM movies WHERE name == 'Sense and Sensibility';", out, in);
        queryDatabase("SELECT id FROM actors WHERE name == 'Hugh Grant';", out, in);
        queryDatabase("SELECT id FROM actors WHERE name == 'Toni Collette';", out, in);
        queryDatabase("SELECT id FROM actors WHERE name == 'James Caan';", out, in);
        queryDatabase("SELECT id FROM actors WHERE name == 'Emma Thompson';", out, in);
        queryDatabase("CREATE TABLE roles (name, movie_id, actor_id);", out, in);
        queryDatabase("INSERT INTO roles VALUES ('Edward', 3, 1);", out, in);
        queryDatabase("INSERT INTO roles VALUES ('Frank', 1, 3);", out, in);
        queryDatabase("INSERT INTO roles VALUES ('Fiona', 2, 2);", out, in);
        queryDatabase("INSERT INTO roles VALUES ('Elinor', 3, 4);", out, in);
    }

    private static void robustnessTest(BufferedWriter out, BufferedReader in){
        System.out.println("----TESTING ROBUSTNESS----");
        assert(queryDatabase("SELECT * FROM actors", out, in).equals("ERROR: Semi colon missing at end of line\n" + EOT + "\n"));
        assert(queryDatabase("SELECT * FROM actors);", out, in).equals("ERROR: Unmatched brackets\n" + EOT + "\n"));
        assert(queryDatabase("SELECT * FROM crew;", out, in).equals("ERROR: table crew does not exist\n" + EOT + "\n"));
        assert(queryDatabase("SELECT spouse FROM actors;", out, in).equals("ERROR: spouse attribute does not exist\n" + EOT + "\n"));
        assert(queryDatabase("        CREATE TABLE test;", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("SELECT * FROM actors WHERE name == 'Hugh Grant;", out, in).equals("ERROR: Unmatched quotation marks\n" + EOT + "\n"));
        assert (queryDatabase("SELECT * FROM actors WHERE name > 10;", out, in).equals("ERROR: Mismatched types in where clause\n" + EOT + "\n"));
        assert(queryDatabase("SELECT name age FROM actors;", out, in).equals("ERROR: Invalid query\n" + EOT + "\n"));
        assert(queryDatabase("SELECT * FROM actors awards > 10;", out, in).equals("ERROR: Invalid query\n" + EOT + "\n"));
        assert(queryDatabase("SELECT * FROM actors WHERE awards LIKE 10;", out, in).equals("ERROR: LIKE operator not recognised\n" + EOT + "\n"));
        assert(queryDatabase("      SELECT * FROM actors WHERE awards > 10;", out, in).equals(
                "id  name           nationality  awards  \n" +
                "2   Toni Collette  Australian   12      \n" +
                "\n" + EOT + "\n"));
        assert(queryDatabase("USE ebay;", out, in).equals("ERROR: Unknown database\n" + EOT + "\n"));
        System.out.println("----PASSED ROBUSTNESS----");
    }

    private static void createTest(BufferedWriter out, BufferedReader in){
        System.out.println("----TESTING CREATING/REMOVING----");
        assert(queryDatabase("CREATE markbook;", out, in).equals("ERROR: Invalid query\n" + EOT + "\n"));
        assert(queryDatabase("CREATE DATABASE markbook", out, in).equals("ERROR: Semi colon missing at end of line\n" + EOT + "\n"));
        assert(queryDatabase("CREATE DATABASE markbook;", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("CREATE DATABASE markbook;", out, in).equals("ERROR: markbook already exists\n" + EOT + "\n"));
        assert(queryDatabase("USE markbook;", out, in).equals("OK\n" + EOT + "\n"));

        assert(queryDatabase("CREATE TABLE marks (name, mark, pass);", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("CREATE TABLE marks (name, mark, pass);", out, in).equals("ERROR: table marks already exists\n" + EOT + "\n"));
        System.out.println("----PASSED CREATING/REMOVING----");
    }
    private static void insertTest(BufferedWriter out, BufferedReader in) {
        System.out.println("----TESTING INSERT----");
        assert(queryDatabase("INSERT INTO marks VALUES ('Steve Carrel', 65, true);", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("INSERT INTO marks VALUES ('Dave', 55, true);", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("INSERT INTO marks VALUES ('Bob', 35, false);", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("INSERT INTO marks VALUES ('Clive', 20, false);", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("SELECT * FROM marks;", out, in).equals(
                "id  name          mark  pass   \n" +
                "1   Steve Carrel  65    true   \n" +
                "2   Dave          55    true   \n" +
                "3   Bob           35    false  \n" +
                "4   Clive         20    false  \n" +
                "\n" + EOT + "\n"));
        assert(queryDatabase("SELECT * FROM marks WHERE name != 'Dave';", out, in).equals(
                "id  name          mark  pass   \n" +
                "1   Steve Carrel  65    true   \n" +
                "3   Bob           35    false  \n" +
                "4   Clive         20    false  \n" +
                "\n" + EOT + "\n"));
        assert(queryDatabase("SELECT * FROM marks WHERE pass == true;", out, in).equals(
                "id  name          mark  pass  \n" +
                "1   Steve Carrel  65    true  \n" +
                "2   Dave          55    true  \n" +
                "\n" + EOT + "\n"));
        System.out.println("----PASSED INSERT----");
    }

    private static void updateTest(BufferedWriter out, BufferedReader in){
        System.out.println("----TESTING UPDATE/DELETE----");
        assert(queryDatabase("UPDATE marks SET mark = 38 WHERE name == 'Clive';", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("SELECT * FROM marks WHERE name == 'Clive';", out, in).equals(
                "id  name   mark  pass   \n" +
                "4   Clive  38    false  \n" +
                "\n" + EOT + "\n"));
        assert(queryDatabase("DELETE FROM marks WHERE name == 'Dave';", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("SELECT * FROM marks;", out, in).equals(
                "id  name          mark  pass   \n" +
                "1   Steve Carrel  65    true   \n" +
                "3   Bob           35    false  \n" +
                "4   Clive         38    false  \n" +
                "\n" + EOT + "\n"));
        assert(queryDatabase("DELETE FROM marks WHERE mark < 40;", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("SELECT * FROM marks;", out, in).equals(
                "id  name          mark  pass  \n" +
                "1   Steve Carrel  65    true  \n" +
                "\n" + EOT + "\n"));
        queryDatabase("DROP DATABASE markbook;", out, in);
        System.out.println("----PASSED UPDATE/DELETE----");

    }

    private static void extensionTest(BufferedWriter out, BufferedReader in) {
        System.out.println("----TESTING EXTENSION----");
        assert(queryDatabase("SELECT * FROM actors WHERE awards < 5;", out, in).equals(
                "id  name        nationality  awards  \n" +
                "1   Hugh Grant  British      3       \n" +
                "\n" + EOT + "\n"));
        assert(queryDatabase("ALTER TABLE actors ADD age;", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("SELECT * FROM actors;", out, in).equals(
                "id  name           nationality  awards  age  \n" +
                "1   Hugh Grant     British      3            \n" +
                "2   Toni Collette  Australian   12           \n" +
                "3   James Caan     American     8            \n" +
                "4   Emma Thompson  British      10           \n" +
                "\n" + EOT + "\n"));
        assert(queryDatabase("UPDATE actors SET age = 45 WHERE name == 'Hugh Grant';", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("SELECT * FROM actors WHERE name == 'Hugh Grant';", out, in).equals(
                "id  name        nationality  awards  age  \n" +
                "1   Hugh Grant  British      3       45   \n" +
                "\n" + EOT + "\n"));
        assert(queryDatabase("ALTER TABLE actors DROP age;", out, in).equals("OK\n" + EOT + "\n"));
        assert(queryDatabase("SELECT * FROM actors WHERE name == 'Hugh Grant';", out, in).equals(
                "id  name        nationality  awards  \n" +
                "1   Hugh Grant  British      3       \n" +
                "\n" + EOT + "\n"));
        assert(queryDatabase("SELECT * FROM actors WHERE (awards > 5) AND (nationality == 'British');", out, in).equals(
                "id  name           nationality  awards  \n" +
                "4   Emma Thompson  British      10      \n" +
                "\n" + EOT + "\n"));
        assert(queryDatabase("SELECT * FROM actors WHERE (awards > 5) AND ((nationality == 'British') OR (nationality == 'Australian'));", out, in).equals(
                "id  name           nationality  awards  \n" +
                "2   Toni Collette  Australian   12      \n" +
                "4   Emma Thompson  British      10      \n" +
                "\n" + EOT + "\n"));
        assert (queryDatabase("SELECT * FROM actors WHERE name LIKE 'an';", out, in).equals(
                "id  name        nationality  awards  \n" +
                "1   Hugh Grant  British      3       \n" +
                "3   James Caan  American     8       \n" +
                "\n" + EOT + "\n"));
        assert (queryDatabase("JOIN actors AND roles ON id AND actor_id;", out, in).equals(
                "id  actors.name    actors.nationality  actors.awards  roles.name  roles.movie_id  roles.actor_id  \n" +
                "1   Hugh Grant     British             3              Edward      3               1               \n" +
                "2   Toni Collette  Australian          12             Fiona       2               2               \n" +
                "3   James Caan     American            8              Frank       1               3               \n" +
                "4   Emma Thompson  British             10             Elinor      3               4               \n" +
                "\n" + EOT + "\n"));
        assert(queryDatabase("JOIN movies AND roles ON id AND movie_id;", out, in).equals(
                "id  movies.name            movies.genre  roles.name  roles.movie_id  roles.actor_id  \n" +
                "1   Mickey Blue Eyes       Comedy        Frank       1               3               \n" +
                "2   About a Boy            Comedy        Fiona       2               2               \n" +
                "3   Sense and Sensibility  Period Drama  Edward      3               1               \n" +
                "4   Sense and Sensibility  Period Drama  Elinor      3               4               \n" +
                "\n" + EOT + "\n"));
        assert (queryDatabase("DELETE FROM actors WHERE name == 'Hugh Grant';",out, in).equals("OK\n" + EOT + "\n"));
        assert (queryDatabase("DELETE FROM actors WHERE name == 'James Caan';",out, in).equals("OK\n" + EOT + "\n"));
        assert (queryDatabase("DELETE FROM actors WHERE name == 'Emma Thompson';",out, in).equals("OK\n" + EOT + "\n"));
        assert (queryDatabase("JOIN actors AND roles ON id AND actor_id;", out, in).equals(
                "id  actors.name    actors.nationality  actors.awards  roles.name  roles.movie_id  roles.actor_id  \n" +
                "1   Toni Collette  Australian          12             Fiona       2               2               \n" +
                "\n" + EOT + "\n"));
        assert (queryDatabase("DROP TABLE actors;", out, in).equals("OK\n" + EOT + "\n"));
        assert (queryDatabase("SELECT * FROM actors;", out, in).equals("ERROR: table actors does not exist\n" + EOT + "\n"));
        assert (queryDatabase("DROP DATABASE imdb;", out, in).equals("OK\n" + EOT + "\n"));
        assert (queryDatabase("USE imdb;", out, in).equals("ERROR: Unknown database\n" + EOT + "\n"));
        System.out.println("----PASSED EXTENSION----");
    }
    }
