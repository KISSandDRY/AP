package deposit.repository;

import deposit.domain.api.IStorable;
import deposit.repository.api.IDepositRepository;

import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.function.Predicate;

/**
 * In-memory implementation of {@link IDepositRepository} for storing and managing entities in a HashMap.
 * Supports adding, removing, finding by ID, filtered and sorted retrieval,
 * and stub methods for saving and loading data (which always succeed).
 *
 * @param <T> the type of entity managed by this repository, must implement {@link IStorable}
 */
public final class InMemoryDepositRepository<T extends IStorable> implements IDepositRepository<T> {

    private final Map<UUID, T> accounts;

    /**
     * Constructs a new empty InMemoryDepositRepository.
     */
    public InMemoryDepositRepository() {
        this.accounts = new HashMap<>();
    }

    /**
     * Adds an entity to the repository, replacing any existing entity with the same ID.
     *
     * @param account the entity to add
     * @return true always as the operation succeeds
     */
    @Override
    public boolean add(final T account) {
        accounts.put(account.getId(), account);
        return true;
    }

    /**
     * Removes an entity by its unique ID.
     *
     * @param id the UUID of the entity to remove
     * @return true if the entity was removed, false if not found
     */
    @Override
    public boolean remove(final UUID id) {
        return accounts.remove(id) != null;
    }

    /**
     * Finds an entity by its unique ID.
     *
     * @param id the UUID of the entity to find
     * @return an Optional containing the entity if found, or empty otherwise
     */
    @Override
    public Optional<T> findById(final UUID id) {
        return Optional.ofNullable(accounts.get(id));
    }

    /**
     * Returns a list of all entities filtered by the given predicate and sorted by the given comparator.
     * Both filter and sorter parameters can be null to return all entities unsorted.
     *
     * @param filter the predicate to apply for filtering (can be null)
     * @param sorter the comparator to apply for sorting (can be null)
     * @return a list of filtered and sorted entities
     */
    @Override
    public List<T> findAll(final Predicate<T> filter, final Comparator<T> sorter) {
        Stream<T> stream = accounts.values().stream();

        if (filter != null) 
            stream = stream.filter(filter);

        if (sorter != null) 
            stream = stream.sorted(sorter);

        return stream.toList();
    }

    /**
     * Returns all entities optionally filtered by a predicate.
     *
     * @param filter the predicate to filter entities (can be null)
     * @return a list of entities matching the filter or all if filter is null
     */
    public List<T> findAll(final Predicate<T> filter) {
        return findAll(filter, null);
    }

    /**
     * Returns all entities optionally sorted by a comparator.
     *
     * @param sorter the comparator to sort entities (can be null)
     * @return a list of sorted entities or all if sorter is null
     */
    public List<T> findAll(final Comparator<T> sorter) {
        return findAll(null, sorter);
    }

    /**
     * Returns all entities without filtering or sorting.
     *
     * @return a list containing all entities in the repository
     */
    public List<T> findAll() {
        return findAll(null, null);
    }

    /**
     * Stub method to simulate saving repository data.
     *
     * @return true always
     */
    @Override 
    public boolean save() {
        return true;
    }

    /**
     * Stub method to simulate loading repository data.
     *
     * @return true always
     */
    @Override 
    public boolean load() {
        return true;
    }
}
