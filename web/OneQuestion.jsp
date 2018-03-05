<%-- 
    Document   : index
    Created on : Feb 14, 2018, 11:32:38 AM
    Author     : ayman
--%>

<%@page import="project.Question"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="intro6e.css" />
        <link rel="stylesheet" type="text/css" href="intro6eselftest.css" />
        <link rel="stylesheet" href="highlight.css">
        <script src="//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.12.0/highlight.min.js"></script>
        <script>hljs.initHighlightingOnLoad();</script>
        <script src="//code.jquery.com/jquery-1.10.2.js"></script>
        <script src="//code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
        <%
            String title = "Multiple-Choice Question";
            if (request.getParameter("title") != null && !request.getParameter("title").isEmpty()) {
                title = request.getParameter("title");
            }

        %>

        <title><%= title%></title>
        <jsp:useBean class="project.Question" id="q" scope="page">
            <jsp:setProperty name="q" property="chapterNo" value="<%= request.getParameter("chapterNo")%>" />
            <jsp:setProperty name="q" property="questionNo" value="<%= request.getParameter("questionNo")%>" />
            <jsp:setProperty name="q" property="submittedAnswers" value="<%= request.getParameterValues("choices")%>" />
            <jsp:setProperty name="q" property="remoteAddr" value="<%= request.getRemoteAddr()%>" />
        </jsp:useBean>
        <jsp:setProperty name="q" property="*" />
    </head>
    <body>
        <script>
            var json = JSON.parse('<%= q.getChapterQuestionJson()%>');
            function populate(s1, s2) {
                var c = $(s1).val();
                console.log(c);
                $(s2).find('option').remove().end();
                for (var i in json[c]) {
                    $(s2).append("<option value='" + json[c][i] + "'>" + json[c][i] + "</option>");
                }
            }
        </script>
        <h3 id="h3style" style="width: 500px auto; max-width: 620px; margin: 0 auto; ">
            <%= title%> <% out.print((q.getChapterNo() != null) ? q.getChapterNo() : "");
                out.print((q.getChapterNo() != null && q.getQuestionNo() != null) ? "." : "");
                out.print((q.getQuestionNo() != null) ? q.getQuestionNo() : "");
            %>
        </h3>
        <div style="width: 500px auto; max-width: 620px; margin: 0 auto; border: 1px solid #f6912f; font-weight: normal ">
            <form method="post">
                <div id="question">
                    <div id="questionstatement"><span>&nbsp;&nbsp;</span>&nbsp;
                        <%
                            String text = q.getQuestion();
                            String code = "";
                            if (q.getQuestion() != null && q.getQuestion().contains("\n")) {
                                String split[] = q.getQuestion().split("\n", 2);
                                text = split[0];
                                code = split[1];
                                if (!code.equals("")) {
                                    code = "<div class = \"preBlock\"><pre><code style=\"background-color: white;\">" + code + "</pre></code></dev>";
                                }
                                out.write(q.escape(text) + code);
                            } else {
                                out.write(q.escape(text));
                            }
                        %>
                    </div>
                    <div id="choices">
                        <%  if (text.isEmpty() && (q.getChapterNo() != null || q.getQuestionNo() != null)) {
                                out.println("Question not found!");
                            } else if (q.getChapterNo() != null && q.getQuestionNo() != null) {
                                String answers = q.getAnswerKey();
                                int answersLen = answers.length();
                                String type;
                                if (answersLen > 1) {
                                    type = "checkbox";
                                } else {
                                    type = "radio";
                                }
                                String choices[] = q.getChoices();
                                char letter = 'A';
                                for (String c : choices) {
                                    if (!c.equals("NULL")) {
                                        out.println("                            <div id = \"choicemargin\">");
                                        out.println("                                <input name=\"choices\" type=" + type + " value=" + letter + ">");
                                        out.println("                                <span id=\"choicelabel\">" + letter + ".</span> <span id=\"choicestatement\">" + Question.escape(c) + "</span>");
                                        out.println("                            </div>");
                                    }
                                    letter = (char) (((int) letter) + 1);
                                }

                                String userChoices = "";
                                String hint = q.getHint();
                                String hintWithStyle;
                                if (hint.equals("")) {
                                    hintWithStyle = "";
                                } else {
                                    hintWithStyle = "<div style = 'color: purple; font-family: Times New Roman;'> Explanation: " + Question.escape(hint) + "</div>";
                                }
                                if (request.getParameterValues("choices") != null && (q.getSubmit() != null && !q.getSubmit().isEmpty())) {
                                    for (String c : request.getParameterValues("choices")) {
                                        userChoices += c;
                                    }
                                    if (answers.equals(userChoices.toLowerCase())) {
                                        out.println("<br><span style = \"color: green\">Your answer " + userChoices +" is correct <img src=\"correct.jpg\" border=\"0\" width=\"42\" height=\"30\"</img></span><br>");
                                    } else if (request.getParameterValues("choices").length != 0) {
                                        out.println("<br><span style = \"color: red\">Your answer " + userChoices + " is incorrect <img src=\"wrong.jpg\" border=\"0\" width=\"42\" height=\"30\"</img></span>");
                                        out.println("<div id = \"a1\" style = \"color: green\"> Click here to show the correct answer</div>");
                                        out.println("<script type=\"text/javascript\">$(document).ready(function() {$(\"#a1\").click(function() {$(this).text(\"The correct answer is " + answers.toUpperCase() + "\");$(this).append(\"" + hintWithStyle + "\");});});</script>");
                                    }
                                } else if (request.getParameterValues("choices") == null && (q.getSubmit() != null && !q.getSubmit().isEmpty())) {
                                    out.println("<br><span >You did not answer this <img src=\"noanswer.jpg\" border=\"0\" width=\"42\" height=\"30\"</img></span>");
                                    out.println("<div id = \"a1\" style = \"color: green\"> Click here to show the correct answer</div>");
                                    out.println("<script type=\"text/javascript\">$(document).ready(function() {$(\"#a1\").click(function() {$(this).text(\"The correct answer is " + answers.toUpperCase() + "\");$(this).append(\"" + hintWithStyle + "\");});});</script>");
                                }
                                out.println("<input class='buttons' type=\"submit\" name = \"submit\" value= \"Check My Answer\">");
                            } else {
                                out.println("<label>Select chapter: </label><select id='chapterSelect' name='chapter' onchange=\"populate(this, '#questionSelect')\">");
                                for (int i = 0; i < q.getNumberOfChapters(); i++) {
                                    out.println("   <option value=\"" + (i + 1) + "\">" + (i + 1) + "</option>");
                                }
                                out.println("</select><br>");

                                out.println("<label>Select question: </label><select id='questionSelect' name='question'>");
                                for (int i = 0; i < q.getNumberOfQuestions(1); i++) {
                                    out.println("   <option value=\"" + (i + 1) + "\">" + (i + 1) + "</option>");
                                }
                                out.println("</select><br>");
                                out.println("<input class='buttons' type='button' id='getquestion' value='Get Question' >");
                            }
                        %>
                    </div>
                    <input type="hidden" name="chapterNo" value="<%= request.getParameter("chapterNo")%>" >
                    <input type="hidden" name="questionNo" value="<%= request.getParameter("questionNo")%>" >
                    <script>
                        $('#getquestion').click(function () {
                            location.replace("<%= request.getRequestURL()%>" + "?chapterNo=" + $('#chapterSelect').val() + "&questionNo=" + $('#questionSelect').val());
                        })
                    </script>
                </div>
            </form>
        </div>
        <!--        <h3 id="h3style" style = " width: 500px auto; max-width: 620px; margin: 0 auto; ">Multiple-Choice Question CheckBox</h3>
                <div style="width: 500px auto; max-width: 620px; margin: 0 auto; border: 1px solid #f6912f; font-weight: normal ">
                    <form method="post" action="GradeOneQuestion">
                        <div id="question"><div id="questionstatement">
                                <span>&nbsp;&nbsp;</span>&nbsp;      Which of the following assignment statements is incorrect?
                                <div class = "preBlock"> <br></div></div><div id="choices">
                                <div id = "choicemargin">
                                    <input type="checkbox" value="A" name="QA12"> 
                                    <span id="choicelabel">A.</span> <span id="choicestatement"> 	i = j = k = 1; </span>
                                    <br></div>
                                <div id = "choicemargin"><input type="checkbox" value="B" name="QB12"> 
                                    <span id="choicelabel">B.</span> <span id="choicestatement">	i = 1; j = 1; k = 1;</span><br>
                                </div>
                                <div id = "choicemargin">
                                    <input type="checkbox" value="C" name="QC12"> <span id="choicelabel">C.</span> 
                                    <span id="choicestatement"> 	i = 1 = j = 1 = k = 1;</span><br></div>
                                <div id = "choicemargin"><input type="checkbox" value="D" name="QD12"> 
                                    <span id="choicelabel">D.</span> <span id="choicestatement">      i == j == k == 1;</span><br></div>
                                <div style="text-align: left; margin-right: 1em; ">
                                    <input type="submit" style = "margin-bottom: 0px; margin-top: 10px; margin-left: 5px;border: 0px; font-family: Helvetica, monospace; font-size: 85%;background-color: rgba(0, 128, 0, 0.7); border-radius: 0px; color:black;"name = "buttonName" value= "Check My Answer"> </div>
                                <input type="hidden" value=CheckBox name="title" /><input type="hidden" value=2 name="chapter" />
                                <input type="hidden" value= "intro10equiz" name="table2" /><input type="hidden" value= name="username" />
                                <input type="hidden" value=12 name="questionNo" /><input type="hidden" value=500 name="width" /><input type="hidden" value=620 name="maxwidth" />
                                </form>
                            </div>-->
    </body>
</html>
