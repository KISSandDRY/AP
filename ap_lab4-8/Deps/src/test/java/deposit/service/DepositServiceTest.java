package deposit.service;

import deposit.domain.Deposit;
import deposit.domain.DepositAccount;
import deposit.domain.DepositBuilder;
import deposit.domain.value.Money;
import deposit.domain.value.Currency;
import deposit.domain.value.TermPeriod;
import deposit.interest.InterestRateType;
import deposit.repository.api.IDepositRepository;
import deposit.exceptions.DepositNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

/**
 * Unit tests for the DepositService class.
 * This class uses Mockito to isolate the service from the repository layer.
 */
@ExtendWith(MockitoExtension.class)
class DepositServiceTest {

    @Mock
    private IDepositRepository<Deposit> mockDepositRepo;
    @Mock
    private IDepositRepository<DepositAccount> mockAccountRepo;

    @InjectMocks
    private DepositService depositService;

    private Deposit sampleDeposit;
    private DepositAccount sampleAccount;

    @BeforeEach
    void setUp() {
        sampleDeposit = new DepositBuilder()
                .setBankName("Test Bank")
                .setDepositName("Test Deposit")
                .setCurrency(Currency.USD)
                .setMinAmount(100)
                .setMaxAmount(10000)
                .setMinMonths(6)
                .setMaxMonths(24)
                .setInterestRate(0.05, InterestRateType.FIXED)
                .setCanReplenish(true)
                .build();

        sampleAccount = new DepositAccount(
                sampleDeposit,
                new Money("500.00", Currency.USD),
                new TermPeriod(12)
        );

        depositService = new DepositService(mockDepositRepo, mockAccountRepo);
    }

    @Test
    @DisplayName("should add a deposit successfully when no duplicate exists")
    void addDeposit_shouldSucceed_whenNotDuplicate() {
        assertDoesNotThrow(() -> depositService.addDeposit(sampleDeposit));

        verify(mockDepositRepo, times(1)).add(sampleDeposit);
    }

    @Test
    @DisplayName("should open an account successfully when the deposit exists")
    void openAccount_shouldSucceed_whenDepositExists() {
        // Arrange
        UUID depositId = sampleDeposit.getId();
        Money amount = new Money("1000", Currency.USD);
        TermPeriod term = new TermPeriod(12);
        when(mockDepositRepo.findById(depositId)).thenReturn(Optional.of(sampleDeposit));

        DepositAccount expectedAccount = new DepositAccount(sampleDeposit, amount, term);

        DepositAccount newAccount = depositService.openAccount(depositId, amount, term);

        assertNotNull(newAccount);
        assertEquals(amount, newAccount.getAmount());
        verify(mockAccountRepo, times(1)).add(eq(expectedAccount));
    }

    @Test
    @DisplayName("should throw DepositNotFoundException when opening an account for a non-existent deposit")
    void openAccount_shouldThrowException_whenDepositNotFound() {
        UUID fakeDepositId = UUID.randomUUID();
        when(mockDepositRepo.findById(fakeDepositId)).thenReturn(Optional.empty());

        assertThrows(DepositNotFoundException.class, () -> {
            depositService.openAccount(fakeDepositId, new Money("1000", Currency.USD), new TermPeriod(12));
        });

        verify(mockAccountRepo, never()).add(any());
    }

    @Test
    @DisplayName("should replenish an account successfully")
    void replenishAccount_shouldSucceed() {
        UUID accountId = sampleAccount.getId();
        Money topUpAmount = new Money("200.00", Currency.USD);
        when(mockAccountRepo.findById(accountId)).thenReturn(Optional.of(sampleAccount));

        assertDoesNotThrow(() -> depositService.replenishAccount(accountId, topUpAmount));

        Money expectedAmount = new Money("700.00", Currency.USD);
        assertEquals(expectedAmount, sampleAccount.getAmount());
    }

    @Test
    @DisplayName("should return correctly filtered and sorted suggestions")
    void getSuggestions_shouldReturnFilteredAndSortedList() {
        // Arrange
        Deposit depositA_lowRate = new DepositBuilder().setBankName("A").setDepositName("Low").setCurrency(Currency.UAH).setInterestRate(0.03, InterestRateType.FIXED).setMinAmount(0).setMinMonths(1).build();
        Deposit depositB_highRate = new DepositBuilder().setBankName("B").setDepositName("High").setCurrency(Currency.UAH).setInterestRate(0.05, InterestRateType.FIXED).setMinAmount(0).setMinMonths(1).build();
        Deposit depositC_unsuitable = new DepositBuilder().setBankName("C").setDepositName("Unsuitable").setCurrency(Currency.UAH).setMinAmount(5000).setMinMonths(1).setInterestRate(0.04, InterestRateType.FIXED).build();

        when(mockDepositRepo.findAll(null, null)).thenReturn(List.of(depositA_lowRate, depositB_highRate, depositC_unsuitable));

        Money userAmount = new Money("1000", Currency.UAH);
        TermPeriod userTerm = new TermPeriod(12);

        List<Deposit> suggestions = depositService.getSuggestions(userAmount, userTerm);

        assertEquals(2, suggestions.size()); 
        assertEquals(depositB_highRate, suggestions.get(0)); 
        assertEquals(depositA_lowRate, suggestions.get(1)); 
    }
}

