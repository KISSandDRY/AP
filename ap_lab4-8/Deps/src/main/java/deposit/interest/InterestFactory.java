package deposit.interest;

import deposit.domain.value.PercentageRate;
import deposit.interest.api.IInterestFactory;
import deposit.interest.api.IInterestRate;
import deposit.interest.api.IInterestStrategy;
import deposit.interest.rates.FixedInterestRate;
import deposit.interest.rates.FloatingInterestRate;
import deposit.interest.strategies.CompoundInterest;
import deposit.interest.strategies.SimpleInterest;

import java.util.Map;
import java.util.Objects;
import java.util.EnumMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Factory class responsible for creating instances of interest rates and interest strategies.
 * Uses maps keyed by enum types to store creators for different rate and strategy implementations.
 * Provides convenient methods to instantiate interest rates and strategies based on type parameters.
 */
public final class InterestFactory implements IInterestFactory {

    private final Map<InterestRateType, Function<PercentageRate, IInterestRate>> rateCreators;
    private final Map<InterestStrategyType, Supplier<IInterestStrategy>> strategyCreators;

    /**
     * Initializes the factory with mappings of rate and strategy types to their respective constructors.
     */
    public InterestFactory() {
        rateCreators = new EnumMap<>(InterestRateType.class);
        strategyCreators = new EnumMap<>(InterestStrategyType.class);

        rateCreators.put(InterestRateType.FIXED, FixedInterestRate::new);
        rateCreators.put(InterestRateType.FLOATING, FloatingInterestRate::new);

        strategyCreators.put(InterestStrategyType.SIMPLE, SimpleInterest::new);
        strategyCreators.put(InterestStrategyType.COMPOUND, CompoundInterest::new);
    }

    /**
     * Creates an interest rate instance of the specified type with the given value.
     *
     * @param type the interest rate type enum
     * @param value the percentage rate value
     * @return a new interest rate instance of the requested type
     * @throws NullPointerException if the rate type is unknown
     */
    @Override
    public IInterestRate createRate(
        final InterestRateType type, 
        final PercentageRate value
    ) throws NullPointerException {

        Function<PercentageRate, IInterestRate> creator = rateCreators.get(type);
        Objects.requireNonNull(creator, "Unknown interest rate type: " + type);

        return creator.apply(value);
    }

    /**
     * Creates an interest strategy instance of the specified type.
     *
     * @param type the interest strategy type enum
     * @return a new interest strategy instance of the requested type
     * @throws NullPointerException if the strategy type is unknown
     */
    @Override
    public IInterestStrategy createStrategy(
        final InterestStrategyType type
    ) throws NullPointerException {

        Supplier<IInterestStrategy> creator = strategyCreators.get(type);
        Objects.requireNonNull(creator, "Unknown interest strategy type: " + type);

        return creator.get();
    }
}
