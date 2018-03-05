/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 *
 * @author ayman
 */
public class InitiateDb extends Thread {

    Connection connection;

    public InitiateDb(Connection con) {
        connection = con;
    }

    public void createTable() {
        String dropTable = "drop table if exists intro11equiz;";
        String createTable = "create table if not exists intro11equiz (\n"
                + "  chapterNo int(11), \n"
                + "  questionNo int(11), \n"
                + "  question text, \n"
                + "  choiceA varchar(1000),\n"
                + "  choiceB varchar(1000),\n"
                + "  choiceC varchar(1000),\n"
                + "  choiceD varchar(1000),\n"
                + "  choiceE varchar(1000),\n"
                + "  answerKey varchar(5),\n"
                + "  hint text\n"
                + ");";
        try {
            Statement statement = connection.createStatement();
            statement.execute(dropTable);
            statement.execute(createTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insert(JsonObject o) {
        String chapterNo = "";
        String questionNo = "";
        String question = "";
        String choiceA = "";
        String choiceB = "";
        String choiceC = "";
        String choiceD = "";
        String choiceE = "";
        String answerKey = "";
        String hint = "";

        if (o.containsKey("chapterNo")) {
            chapterNo = o.getInt("chapterNo") + "";
        }
        if (o.containsKey("questionNo")) {
            questionNo = o.getInt("questionNo") + "";
        }
        if (o.containsKey("question")) {
            question = o.getString("question");
        }
        if (o.containsKey("choiceA")) {
            choiceA = o.getString("choiceA");
        }
        if (o.containsKey("choiceB")) {
            choiceB = o.getString("choiceB");
        }
        if (o.containsKey("choiceC")) {
            choiceC = o.getString("choiceC");
        }
        if (o.containsKey("choiceD")) {
            choiceD = o.getString("choiceD");
        }
        if (o.containsKey("choiceE")) {
            choiceE = o.getString("choiceE");
        }
        if (o.containsKey("answerKey")) {
            answerKey = o.getString("answerKey");
        }
        if (o.containsKey("hint")) {
            hint = o.getString("hint");
        }
        try {
            String insert = "INSERT INTO intro11equiz (chapterNo, questionNo, question, choiceA, choiceB, choiceC, choiceD, choiceE, answerKey, hint) "
                    + "VALUES(?,?,?,?,?,?,?,?,?,?) ";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setString(1, chapterNo);
            preparedStatement.setString(2, questionNo);
            preparedStatement.setString(3, question);
            preparedStatement.setString(4, choiceA);
            preparedStatement.setString(5, choiceB);
            preparedStatement.setString(6, choiceC);
            preparedStatement.setString(7, choiceD);
            preparedStatement.setString(8, choiceE);
            preparedStatement.setString(9, answerKey);
            preparedStatement.setString(10, hint);
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(answerKey);
    }

    @Override
    public void run() {
//        InitiateDb init = new InitiateDb();
        synchronized (this) {
            initDb();
            String jsonFile = "intro11equiz.json";
            try {
                System.out.println(jsonFile);
                File file = new File(jsonFile);
                FileInputStream in = new FileInputStream(file);
                JsonReader jsonReader = Json.createReader(in);
                JsonArray jsonArray = jsonReader.readArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.getJsonObject(i);
                    insert(jsonObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initDb() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
//            connection = DriverManager.getConnection("jdbc:mysql://localhost/javabook", "scott", "tiger");
//            connection = DriverManager.getConnection("jdbc:mysql://35.185.94.191/bagabas", "bagabas", "Ayman1996");
//              connection = con;
            createTable();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
//            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/javabook", "scott", "tiger");
            Connection con = DriverManager.getConnection("jdbc:mysql://35.185.94.191/bagabas", "bagabas", "Ayman1996");
            InitiateDb initiateDb = new InitiateDb(con);
            initiateDb.start();
            synchronized(initiateDb) {
                System.out.println("Initializing the database...");
                initiateDb.wait();
                System.out.println("Done!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
