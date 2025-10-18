package deposit.domain.value;

import deposit.exceptions.ValidationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the generic Range value object.
 * These tests verify the contains() and toString() logic for various range types.
 */
class RangeTest {

    @Nested
    @DisplayName("Tests with Integer")
    class IntegerRangeTests {

        @Test
        @DisplayName("should correctly check values for a bounded range")
        void contains_withBoundedRange() {
            Range<Integer> range = new Range<>(10, 20);
            assertTrue(range.contains(15), "Value inside should be true");
            assertTrue(range.contains(10), "Min boundary value should be true");
            assertTrue(range.contains(20), "Max boundary value should be true");
            assertFalse(range.contains(9), "Value below min should be false");
            assertFalse(range.contains(21), "Value above max should be false");
        }

        @Test
        @DisplayName("should correctly check values for a lower-bounded range")
        void contains_withLowerBoundedRange() {
            Range<Integer> range = new Range<>(10, null); // [10, infinity)
            assertTrue(range.contains(100));
            assertTrue(range.contains(10));
            assertFalse(range.contains(9));
        }

        @Test
        @DisplayName("should correctly check values for an upper-bounded range")
        void contains_withUpperBoundedRange() {
            Range<Integer> range = new Range<>(null, 20); // (-infinity, 20]
            assertTrue(range.contains(0));
            assertTrue(range.contains(20));
            assertFalse(range.contains(21));
        }

        @Test
        @DisplayName("should return true for any value in an unbounded range")
        void contains_withUnboundedRange() {
            Range<Integer> range = new Range<>(null, null); // (-infinity, infinity)
            assertTrue(range.contains(Integer.MIN_VALUE));
            assertTrue(range.contains(0));
            assertTrue(range.contains(Integer.MAX_VALUE));
        }
    }

    @Nested
    @DisplayName("Tests with Money")
    class MoneyRangeTests {

        @Test
        @DisplayName("should correctly check Money values for a bounded range")
        void contains_withBoundedMoneyRange() {
            Range<Money> range = new Range<>(new Money("100", Currency.USD), new Money("1000", Currency.USD));
            assertTrue(range.contains(new Money("500", Currency.USD)));
            assertTrue(range.contains(new Money("100", Currency.USD)));
            assertFalse(range.contains(new Money("99.99", Currency.USD)));
            assertFalse(range.contains(new Money("1000.01", Currency.USD)));
        }

        @Test
        @DisplayName("should correctly check Money values for a lower-bounded range")
        void contains_withLowerBoundedMoneyRange() {
            Range<Money> range = new Range<>(new Money("0", Currency.EUR), null);
            assertTrue(range.contains(new Money("0", Currency.EUR)));
            assertTrue(range.contains(new Money("999999", Currency.EUR)));
        }

        @Test
        @DisplayName("should throw ValidationException when checking 'contains' with a different currency")
        void contains_withDifferentCurrency_throwsException() {
            Range<Money> range = new Range<>(new Money("100", Currency.EUR), null);

            // Verify that checking contains() with a Money object of a different currency
            // throws a ValidationException because the underlying compareTo() will fail.
            assertThrows(ValidationException.class, () -> range.contains(new Money("500", Currency.USD)));
            assertThrows(ValidationException.class, () -> range.contains(Money.ZERO_UAH));
        }
    }

    @Nested
    @DisplayName("toString() Formatting")
    class ToStringTests {

        @Test
        @DisplayName("should format a bounded range correctly")
        void toString_bounded() {
            Range<Integer> range = new Range<>(10, 20);
            assertEquals("[10, 20]", range.toString());
        }

        @Test
        @DisplayName("should format a lower-bounded range correctly")
        void toString_lowerBounded() {
            Range<Money> range = new Range<>(new Money("100", Currency.USD), null);
            assertEquals("[100.00 $, ∞]", range.toString());
        }

        @Test
        @DisplayName("should format an upper-bounded range correctly")
        void toString_upperBounded() {
            Range<Integer> range = new Range<>(null, 50);
            assertEquals("[-∞, 50]", range.toString());
        }

        @Test
        @DisplayName("should format an unbounded range correctly")
        void toString_unbounded() {
            Range<Integer> range = new Range<>(null, null);
            assertEquals("[-∞, ∞]", range.toString());
        }
    }
}

