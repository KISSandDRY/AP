package deposit.domain.value;

import deposit.exceptions.ValidationException;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Money value object.
 * These tests verify its construction, validation, arithmetic, and comparison logic.
 */
class MoneyTest {

    @Test
    @DisplayName("should create Money successfully from a valid string")
    void testCreationFromString_succeeds() {
        Money money = new Money("123.456", Currency.USD);
        // The record's constructor should round the amount to 2 decimal places.
        assertEquals(new BigDecimal("123.46"), money.amount());
        assertEquals(Currency.USD, money.currency());
    }

    @Test
    @DisplayName("should throw ValidationException when created with a negative amount")
    void testCreation_withNegativeAmount_throwsException() {
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> new Money(new BigDecimal("-0.01"), Currency.EUR)
        );
        assertEquals("Money amount cannot be negative: -0.01", exception.getMessage());
    }

    @Test
    @DisplayName("should throw NullPointerException for null amount or currency")
    void testCreation_withNulls_throwsException() {
        // CORRECTED: Cast null to BigDecimal to resolve constructor ambiguity.
        assertThrows(NullPointerException.class, () -> new Money((BigDecimal) null, Currency.UAH));
        assertThrows(NullPointerException.class, () -> new Money(BigDecimal.TEN, null));
    }

    @Test
    @DisplayName("should add two Money objects of the same currency")
    void add_withSameCurrency_succeeds() {
        Money m1 = new Money("100.25", Currency.USD);
        Money m2 = new Money("50.50", Currency.USD);
        Money expected = new Money("150.75", Currency.USD);
        assertEquals(expected, m1.add(m2));
    }

    @Test
    @DisplayName("should throw ValidationException when adding different currencies")
    void add_withDifferentCurrencies_throwsException() {
        Money m1 = new Money("100", Currency.USD);
        Money m2 = new Money("100", Currency.EUR);

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> m1.add(m2)
        );
        assertTrue(exception.getMessage().contains("Cannot add money of different currencies"));
    }

    @Test
    @DisplayName("should multiply Money by a BigDecimal factor")
    void multiply_succeeds() {
        Money m1 = new Money("10.00", Currency.UAH);
        BigDecimal factor = new BigDecimal("1.5");
        Money expected = new Money("15.00", Currency.UAH);
        assertEquals(expected, m1.multiply(factor));
    }

    @Test
    @DisplayName("should correctly compare two Money objects")
    void compareTo_withSameCurrency_returnsCorrectOrder() {
        Money m_small = new Money("99.99", Currency.EUR);
        Money m_large = new Money("100.00", Currency.EUR);
        Money m_equal = new Money("100.00", Currency.EUR);

        assertTrue(m_small.compareTo(m_large) < 0);
        assertTrue(m_large.compareTo(m_small) > 0);
        assertEquals(0, m_large.compareTo(m_equal));
    }

    @Test
    @DisplayName("should throw ValidationException when comparing different currencies")
    void compareTo_withDifferentCurrencies_throwsException() {
        Money m1 = new Money("100", Currency.USD);
        Money m2 = new Money("100", Currency.EUR);
        assertThrows(ValidationException.class, () -> m1.compareTo(m2));
    }

    @ParameterizedTest
    @DisplayName("should format toString correctly for different currencies")
    @CsvSource({
        "1234.56, USD, '1234.56 $'",
        "99.90, UAH, '99.90 ₴'",
        "0.50, EUR, '0.50 €'"
    })
    void toString_formatsCorrectly(String amount, Currency currency, String expected) {
        Money money = new Money(amount, currency);
        assertEquals(expected, money.toString());
    }
}

