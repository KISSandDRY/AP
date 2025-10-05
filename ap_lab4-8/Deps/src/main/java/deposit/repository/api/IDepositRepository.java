package deposit.repository.api;

import deposit.domain.api.IStorable;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * Generic repository interface for storing and managing entities that implement {@link IStorable}.
 * Supports basic CRUD operations, filtering, sorting, and persistence.
 *
 * @param <T> the type of entity managed by this repository
 */
public interface IDepositRepository<T extends IStorable> {

    /**
     * Adds an entity to the repository.
     *
     * @param entity the entity to add
     * @return true if added successfully, false otherwise
     */
    boolean add(final T entity);

    /**
     * Removes an entity by its unique identifier.
     *
     * @param id the UUID of the entity to remove
     * @return true if removed successfully, false otherwise
     */
    boolean remove(final UUID id);

    /**
     * Finds an entity by its unique identifier.
     *
     * @param id the UUID of the entity to find
     * @return an Optional containing the found entity, or empty if not found
     */
    Optional<T> findById(final UUID id);

    /**
     * Finds all entities matching a filter predicate and sorts them using a comparator.
     *
     * @param filter the predicate to filter entities
     * @param sorter the comparator to sort entities
     * @return a list of filtered and sorted entities
     */
    List<T> findAll(final Predicate<T> filter, final Comparator<T> sorter);

    /**
     * Saves the current state of the repository to persistent storage.
     *
     * @return true if the save was successful, false otherwise
     */
    boolean save();

    /**
     * Loads the repository state from persistent storage.
     *
     * @return true if the load was successful, false otherwise
     */
    boolean load();
}
