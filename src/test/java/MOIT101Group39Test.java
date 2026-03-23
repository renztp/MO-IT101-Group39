import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MOIT101Group39Test {

    @Test
    @DisplayName("Should calculate hours correctly based on timeIn and timeOut")
    void calculateTotalHoursWorked() {
        assertEquals(8.0, MOIT101Group39.calculateTotalHoursWorked(8.0, 17.0));
    }

    @Test
    @DisplayName("Should parse string to correct decimal")
    void shouldParseStringTimeToDecimal() {
        assertEquals(8.0, MOIT101Group39.parseTimeToDecimal("8:0"));
    }
}