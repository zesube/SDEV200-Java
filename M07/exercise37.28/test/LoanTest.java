import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LoanTest {

    @Test
    void calculationIsCorrect() {
        Loan loan = new Loan(6.0, 2, 2_000);  // known values
        double monthly = loan.getMonthlyPayment();
        double total   = loan.getTotalPayment();

        assertEquals(88.71, monthly, 0.01,
                     "monthly payment should match the expected value");
        assertEquals(2129.04, total, 0.01,
                     "total payment should be monthly*years*12");
    }
}