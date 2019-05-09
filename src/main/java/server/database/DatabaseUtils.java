package server.database;

import java.io.File;
import java.io.InputStream;
import java.sql.*;
import java.util.Scanner;

public class DatabaseUtils {


    private static final String DATABASE_NAME = "database.sqlite";

    private static final String DATABASE_PATH = "database/";

    private static final String SQL_PATH = "jdbc:sqlite:";

    private static final String URL = SQL_PATH + DATABASE_PATH + DATABASE_NAME;

    private DatabaseUtils() {

    }

    /**
     * Gets a database connection
     * @return a Connection to the database
     * @throws SQLException when there is an error connecting
     */
    public static Connection getDatabaseConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Resets the database
     */
    public static void resetDatabase() {
        InputStream isDB = DatabaseUtils.class.getClassLoader().getResourceAsStream("/sql/reset_tables.sql");
        if (isDB == null) {
            //this is needed to load resource whilst in the ide
            isDB = DatabaseUtils.class.getClassLoader().getResourceAsStream("./sql/reset_tables.sql");
        }
        if (isDB != null) {
            Scanner scanner = new Scanner(isDB);
            String sqlScript = "";
            while (scanner.hasNext()) {
                sqlScript += scanner.next() + " ";
            }
            String[] scripts = sqlScript.split(";");
            try (
                    Connection connection = DriverManager.getConnection(URL);
                    Statement statement = connection.createStatement()
            ) {
                // loops through the scripts from the sql file and executes them.
                for (String script : scripts) {
                    if(!script.trim().isEmpty()) statement.addBatch(script);
                }
                statement.executeBatch();
                System.out.println("Successfully reset the database");
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        } else {
            System.out.println("Unable to find reset sql script");
        }
    }

    /**
     * Checks if the database exists otherwise create the directory and database
     * @throws Exception if can't create database directory.
     */
    public static void checkIfDataBasesExistsElseCreate() throws Exception{
        File directory = new File(DATABASE_PATH);
        if (!directory.exists()) if(!directory.mkdirs()) throw new Exception("Can't create database folder.");
        File dbFile = new File(DATABASE_PATH + DATABASE_NAME);
        if (!dbFile.exists())  {
            try (Connection conn = getDatabaseConnection()) {
                if (conn != null) {
                    conn.getMetaData();
                    System.out.println("A new database has been created");
                    resetDatabase();
                }
            }
        }
    }



}
