import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class MOIT101Group39Test {

    @Nested
    @DisplayName("Time Conversion & Work Hours")
    class AttendanceTests {

        @Test
        @DisplayName("Should calculate hours correctly based on timeIn and timeOut")
        void calculateTotalHoursWorked() {
            assertEquals(8.0, MOIT101Group39.calculateTotalHoursWorked(8.0, 17.0));
        }

        @ParameterizedTest
        @CsvSource({
                "08:30, 8.5",
                "08:45, 8.75",
                "17:00, 17.0",
                "invalid, 0.0"
        })
        @DisplayName("parseTimeToDecimal: Should convert HH:mm strings correctly")
        void testParseTimeToDecimal(String input, double expected) {
            assertEquals(expected, MOIT101Group39.parseTimeToDecimal(input),
                    "The time string " + input + " did not convert to the expected decimal.");
        }

        @Test
        @DisplayName("calculateTotalHoursWorked: Should deduct 1 hour for lunch break")
        void testCalculateHoursWithBreak() {
            // 8:00 AM (8.0) to 5:00 PM (17.0)
            double result = MOIT101Group39.calculateTotalHoursWorked(8.0, 17.0);
            assertEquals(8.0, result, "Total hours should be 8.0 after 1-hour break deduction.");
        }

        @Test
        @DisplayName("calculateTotalHoursWorked: Should respect the 8:05 AM grace period")
        void testGracePeriod() {
            // 8.0833 is roughly 8:05 AM. Anything <= this should be treated as 8.0.
            double result = MOIT101Group39.calculateTotalHoursWorked(8.10, 17.0);
            assertEquals(8.0, result, "8:10 AM should still be counted as an 8:00 AM start.");
        }

        @Test
        @DisplayName("calculateTotalHoursWorked: Should count total hours worked correctly if undertime")
        void testUnderTime() {
            double result = MOIT101Group39.calculateTotalHoursWorked(8.5, 16.0);
            assertEquals(6.5, result, "8:10AM to 4:00PM should count correctly as undertime");
        }

        @Test
        @DisplayName("calculateTotalHoursWorked: should not return negative hours for very short shifts because of lunch break")
        void testNegativeHours() {
            double result = MOIT101Group39.calculateTotalHoursWorked(8.0, 8.25);
            assertEquals(0.0, result, "Hours worked should never be negative. Found: " + result);
        }
    }

    @Nested
    @DisplayName("System Logic & Calculations")
    class LogicTests {

        @Test
        @DisplayName("calculateGrossSalary: Simple multiplication of hours and rate")
        void testGrossCalculation() {
            assertEquals(500.0, MOIT101Group39.calculateGrossSalary(10.0, 50.0));
            assertEquals(0.0, MOIT101Group39.calculateGrossSalary(0.0, 0.0));
        }
    }

    @Nested
    @DisplayName("Salary & Tax Deductions")
    class DeductionTests {

        @Test
        @DisplayName("calculateSSS: Should return minimum 225.0 for low salaries")
        void testSSSMinimum() {
            assertEquals(225.0, MOIT101Group39.calculateSSS(5000.0));
        }

        @Test
        @DisplayName("calculateSSS: Should return maximum 1125.0 for high salaries")
        void testSSSMaximum() {
            assertEquals(1125.0, MOIT101Group39.calculateSSS(30000.0));
        }

        @Test
        @DisplayName("calculatePhilHealth: Should return employee share (50%)")
        void testPhilHealthEmployeeShare() {
            // For 10,000 gross, total premium is 300. Employee share is 150.
            assertEquals(150.0, MOIT101Group39.calculatePhilHealth(10000.0));
        }

        @Test
        @DisplayName("calculateWithholdingTax: Should return 0 for income below threshold")
        void testTaxFreeThreshold() {
            assertEquals(0.0, MOIT101Group39.calculateWithholdingTax(20000.0));
        }

        @Test
        @DisplayName("calculateWithholdingTax: Should calculate 15% for the first bracket")
        void testFirstTaxBracket() {
            double taxableIncome = 25000.0;
            double expected = (25000 - 20833) * 0.15;
            assertEquals(expected, MOIT101Group39.calculateWithholdingTax(taxableIncome), 0.01);
        }
    }
}