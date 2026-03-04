public class Loan {
    private double annualInterestRate;
    private int numberOfYears;
    private double loanAmount;

    public Loan(double annualInterestRate, int numberOfYears, double loanAmount) {
        this.annualInterestRate = annualInterestRate;
        this.numberOfYears = numberOfYears;
        this.loanAmount = loanAmount;
    }

    public double getMonthlyPayment() {
        double monthlyRate = annualInterestRate / 1200;
        return loanAmount * monthlyRate /
                (1 - Math.pow(1 + monthlyRate, -numberOfYears * 12));
    }

    public double getTotalPayment() {
        return getMonthlyPayment() * numberOfYears * 12;
    }

    /** quick sanity check – can be run from the command line */
    public static void main(String[] args) {
        Loan loan = new Loan(5.0, 10, 10_000);
        System.out.printf("monthly=%.2f total=%.2f%n",
                          loan.getMonthlyPayment(), loan.getTotalPayment());
    }
}
