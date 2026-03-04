<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Random" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Exercise 38.14 - Addition Quiz</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 2rem;
      line-height: 1.4;
    }

    h2 {
      margin-top: 0;
    }

    .quiz-row {
      margin: 8px 0;
    }

    input[type="number"] {
      width: 90px;
      padding: 4px;
    }

    table {
      border-collapse: collapse;
      margin-top: 12px;
      width: 100%;
      max-width: 680px;
    }

    th, td {
      border: 1px solid #ccc;
      padding: 8px;
      text-align: left;
    }

    .correct {
      color: #0a7a0a;
      font-weight: 700;
    }

    .wrong {
      color: #b00020;
      font-weight: 700;
    }

    button, .link-btn {
      margin-top: 12px;
      padding: 8px 14px;
      text-decoration: none;
      display: inline-block;
    }
  </style>
</head>
<body>
<%
  final int QUESTION_COUNT = 10;
  boolean submitted = "true".equals(request.getParameter("submitted"));

  if (!submitted) {
    Random random = new Random();
%>
  <h2>Addition Quiz</h2>
  <form method="post" action="index.jsp">
    <input type="hidden" name="submitted" value="true">

<%
    for (int i = 0; i < QUESTION_COUNT; i++) {
      int n1 = random.nextInt(100);
      int n2 = random.nextInt(100);
%>
    <div class="quiz-row">
      <label>
        <%= (i + 1) %>. What is <%= n1 %> + <%= n2 %>?&nbsp;
        <input type="number" name="answer<%= i %>" required>
      </label>
      <input type="hidden" name="n1_<%= i %>" value="<%= n1 %>">
      <input type="hidden" name="n2_<%= i %>" value="<%= n2 %>">
    </div>
<%
    }
%>
    <button type="submit">Submit Answers</button>
  </form>
<%
  } else {
    int correctCount = 0;
%>
  <h2>Addition Quiz Result</h2>
  <table>
    <tr>
      <th>#</th>
      <th>Question</th>
      <th>Your Answer</th>
      <th>Correct Answer</th>
      <th>Result</th>
    </tr>
<%
    for (int i = 0; i < QUESTION_COUNT; i++) {
      int n1 = Integer.parseInt(request.getParameter("n1_" + i));
      int n2 = Integer.parseInt(request.getParameter("n2_" + i));
      int correctAnswer = n1 + n2;

      String answerText = request.getParameter("answer" + i);
      int userAnswer;
      try {
        userAnswer = Integer.parseInt(answerText);
      } catch (Exception ex) {
        userAnswer = Integer.MIN_VALUE;
      }

      boolean correct = userAnswer == correctAnswer;
      if (correct) {
        correctCount++;
      }
%>
    <tr>
      <td><%= (i + 1) %></td>
      <td><%= n1 %> + <%= n2 %></td>
      <td><%= (userAnswer == Integer.MIN_VALUE ? "(invalid)" : userAnswer) %></td>
      <td><%= correctAnswer %></td>
      <td class="<%= (correct ? "correct" : "wrong") %>"><%= (correct ? "Correct" : "Wrong") %></td>
    </tr>
<%
    }
%>
  </table>

  <p><strong>Score:</strong> <%= correctCount %> / <%= QUESTION_COUNT %></p>
  <a class="link-btn" href="index.jsp">Try Another Quiz</a>
<%
  }
%>
</body>
</html>
