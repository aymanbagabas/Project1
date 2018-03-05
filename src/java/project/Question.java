/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import database.InitiateDb;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

/**
 *
 * @author ayman
 */
public class Question implements Serializable{

    private int numberOfChapters;
    private int numberOfQuestions[];
    private int[][] chapterQuestion;
    private String chapterNo;
    private String questionNo;
    private String question;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private String choiceE;
    private String answerKey;
    private String hint;
    private String submit;
    private String remoteAddr;
    private String type;
    private String[] submittedAnswers;
    private String selectedChapter;
    private String selectedQuestion;
    private Connection connection;
    private final String TABLE = "intro11equiz";
    private final String USERNAME = "Bagabas";

    public Question() {
        initDb();
        setNumberOfChapters();
        numberOfQuestions = new int[numberOfChapters];
        chapterQuestion = new int[numberOfChapters][];
        for(int i = 0; i < numberOfChapters; i++) {
            setNumberOfQuestions(i+1);
            int[] questions = new int[getNumberOfQuestions(i+1)];
            chapterQuestion[i] = questions;
        }
        question = choiceA = choiceB = choiceC = choiceD = choiceE = answerKey = hint = "";
//        System.out.println(getChapterQuestionJson());
    }

    private void loadQuestion() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT question, choiceA, choiceB, choiceC, choiceD, choiceE, answerKey, hint FROM " + TABLE
                    + " WHERE chapterNo = \"" + chapterNo + "\" and questionNo = \"" + questionNo + "\"";
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                question = resultSet.getString("question");
                choiceA = resultSet.getString("choiceA");
                choiceB = resultSet.getString("choiceB");
                choiceC = resultSet.getString("choiceC");
                choiceD = resultSet.getString("choiceD");
                choiceE = resultSet.getString("choiceE");
                answerKey = resultSet.getString("answerKey");
                hint = resultSet.getString("hint");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.printf("%s %s %s %s %s %s %s", question, choiceA, choiceB, choiceC, choiceD, choiceE, answerKey);
    }
    
    private void submitQuestion() {
//        IF EXISTS(select * from test where id=30122)
//   update test set name='john' where id=3012
//ELSE
//   insert into test(name) values('john');
        String answers="";
        for(String s: submittedAnswers) answers += s.toLowerCase();
//        System.out.println(answers);
        boolean isCorrect = answers.toLowerCase().equals(answerKey);
        //Timestamp time = new Timestamp(System.currentTimeMillis());
        String hostname = remoteAddr;
        boolean answerA = (answers.contains("a"));
        boolean answerB = (answers.contains("b"));
        boolean answerC = (answers.contains("c"));
        boolean answerD = (answers.contains("d"));
        boolean answerE = (answers.contains("e"));
        String userName = USERNAME;
        try {
            String query = String.format(
//                            "IF EXISTS(SELECT * FROM intro11e WHERE chapterNo = ? AND questionNo = ?)"
//                             + "UPDATE intro11e SET isCorrect = '?', hostname = '?',"
//                                    + "answerA = '?', answerB = '?', answerC = '?', answerD = '?', answerE = '?' "
//                                    + "WHERE chapterNo = ? AND questionNo = ?"
//                           + "ELSE" +
                             "INSERT INTO intro11e (chapterNo, questionNo, isCorrect, hostname,"
                                    + "answerA, answerB, answerC, answerD, answerE, username) "
                                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    //chapterNo, questionNo, isCorrect, time, hostname, answerA, answerB, answerC, answerD, answerE, chapterNo, questionNo,
                    //isCorrect, time, hostname, answerA, answerB, answerC, answerD, answerE, chapterNo, questionNo);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, chapterNo);
            statement.setString(2, questionNo);
            statement.setBoolean(3, isCorrect);
            statement.setString(4, hostname);
            statement.setBoolean(5, answerA);
            statement.setBoolean(6, answerB);
            statement.setBoolean(7, answerC);
            statement.setBoolean(8, answerD);
            statement.setBoolean(9, answerE);
            statement.setString(10, USERNAME);
            
            statement.execute();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
//        create table if not exists intro11e (
//chapterNo int(11),
//questionNo int(11),
//isCorrect bit(1) default 0,
//time timestamp default current_timestamp,
//hostname varchar(100),
//answerA bit(1) default 0,
//answerB bit(1) default 0,
//answerC bit(1) default 0,
//answerD bit(1) default 0,
//answerE bit(1) default 0,
//username varchar(100)
//);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (getAnswerKey().length() > 1)
            type = "checkbox";
        else
            type = "radio";
    }

    public String getSubmit() {
        return submit;
    }

    public void setSubmit(String submit) {
        if (submittedAnswers != null && submittedAnswers.length > 0) {
            submitQuestion();
        }
        this.submit = submit;
    }

    public String getSelectedChapter() {
        return selectedChapter;
    }

    public void setSelectedChapter(String selectedChapter) {
        this.selectedChapter = selectedChapter;
    }

    public String getSelectedQuestion() {
        return selectedQuestion;
    }

    public void setSelectedQuestion(String selectedQuestion) {
        this.selectedQuestion = selectedQuestion;
    }

    public int[][] getChapterQuestion() {
        return chapterQuestion;
    }

    public String getChapterQuestionJson() {
        //JsonArrayBuilder pJsonArray = Json.createArrayBuilder();
        JsonObjectBuilder jsonObject = Json.createObjectBuilder();
        for(int i = 0; i < chapterQuestion.length;i++) {
            JsonArrayBuilder cJsonArray = Json.createArrayBuilder();
            for (int j = 0; j < chapterQuestion[i].length;j++) {
                cJsonArray.add((j+1));
            }
            jsonObject.add((i+1)+"", cJsonArray.build());
        }
//        System.out.println();
        return jsonObject.build().toString();
    }
    
    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String[] getSubmittedAnswers() {
        return submittedAnswers;
    }

    public void setSubmittedAnswers(String[] submittedAnswers) {
        this.submittedAnswers = submittedAnswers;
    }
    
    

    public String getChapterNo() {
        return chapterNo;
    }

    public void setChapterNo(String chapterNo) {
        this.chapterNo = chapterNo;
    }

    public String getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(String questionNo) {
        loadQuestion();
//        System.out.println(answerKey);
        this.questionNo = questionNo;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
    
    public String[] getChoices() {
        return new String[]{getChoiceA(), getChoiceB(), getChoiceC(), getChoiceD(), getChoiceE()};
    }

    public String getChoiceA() {
        return choiceA;
    }

    public void setChoiceA(String choiceA) {
        this.choiceA = choiceA;
    }

    public String getChoiceB() {
        return choiceB;
    }

    public void setChoiceB(String choiceB) {
        this.choiceB = choiceB;
    }

    public String getChoiceC() {
        return choiceC;
    }

    public void setChoiceC(String choiceC) {
        this.choiceC = choiceC;
    }

    public String getChoiceD() {
        return choiceD;
    }

    public void setChoiceD(String choiceD) {
        this.choiceD = choiceD;
    }

    public String getChoiceE() {
        return choiceE;
    }

    public void setChoiceE(String choiceE) {
        this.choiceE = choiceE;
    }

    public String getAnswerKey() {
        return answerKey;
    }

    public void setAnswerKey(String answerKey) {
        this.answerKey = answerKey;
    }

    public String getHint() {
        if (hint.equals("NULL")) return "";
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
    
    public static String escape(String s) {
    StringBuilder builder = new StringBuilder();
    boolean previousWasASpace = false;
    for( char c : s.toCharArray() ) {
        if( c == ' ' ) {
            if( previousWasASpace ) {
                builder.append("&nbsp;");
                previousWasASpace = false;
                continue;
            }
            previousWasASpace = true;
        } else {
            previousWasASpace = false;
        }
        switch(c) {
            case '<': builder.append("&lt;"); break;
            case '>': builder.append("&gt;"); break;
            case '&': builder.append("&amp;"); break;
            case '"': builder.append("&quot;"); break;
            case '\n': builder.append("<br>"); break;
            // We need Tab support here, because we print StackTraces as HTML
            case '\t': builder.append("&nbsp; &nbsp; &nbsp;"); break;  
            default:
                if( c < 128 ) {
                    builder.append(c);
                } else {
                    builder.append("&#").append((int)c).append(";");
                }    
        }
    }
    return builder.toString();
}
    
    private void setNumberOfChapters() {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT MAX(chapterNo) FROM " + TABLE);
            if (rs.next())
                numberOfChapters = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setNumberOfQuestions(int chapter) {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM " + TABLE + " WHERE chapterNo = " + chapter);
            if (rs.next())
                numberOfQuestions[chapter-1] = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNumberOfChapters() {
        return numberOfChapters;
    }

    public int getNumberOfQuestions(int chapter) {
        return numberOfQuestions[chapter-1];
    }
    
    

    public void initDb() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
//            connection = DriverManager.getConnection("jdbc:mysql://localhost/javabook", "scott", "tiger");
            connection = DriverManager.getConnection("jdbc:mysql://35.185.94.191/bagabas", "bagabas", "Ayman1996");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
