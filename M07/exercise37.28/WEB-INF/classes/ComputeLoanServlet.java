import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ComputeLoanServlet extends HttpServlet {
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");

    try (PrintWriter out = response.getWriter()) {
      double loanAmount = Double.parseDouble(request.getParameter("loanAmount"));
      double annualInterestRate = Double.parseDouble(request.getParameter("annualInterestRate"));
      int numberOfYears = Integer.parseInt(request.getParameter("numberOfYears"));

      Loan loan = new Loan(annualInterestRate, numberOfYears, loanAmount);
      DecimalFormat money = new DecimalFormat("#,##0.00");

      out.println("<!DOCTYPE html>");
      out.println("<html><head><title>Loan Payment Result</title></head><body>");
      out.println("<h2>Loan Payment Result</h2>");
      out.println("<p>Loan Amount: $" + money.format(loanAmount) + "</p>");
      out.println("<p>Annual Interest Rate: " + money.format(annualInterestRate) + "%</p>");
      out.println("<p>Number of Years: " + numberOfYears + "</p>");
      out.println("<p><strong>Monthly Payment: $" + money.format(loan.getMonthlyPayment()) + "</strong></p>");
      out.println("<p><strong>Total Payment: $" + money.format(loan.getTotalPayment()) + "</strong></p>");
      out.println("<p><a href=\"index.html\">Back</a></p>");
      out.println("</body></html>");
    } catch (NumberFormatException ex) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
          "Invalid input. Enter numeric values for amount/rate/years.");
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doPost(request, response);
  }
}
