package deposit.repository;

import deposit.domain.api.IStorable;
import deposit.exceptions.DataAccessException;
import deposit.repository.api.IDepositRepository;
import deposit.repository.api.IFileBasedRepository;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Repository implementation that stores entities in memory and persists them to a file.
 * Uses a thread-safe map keyed by UUID for in-memory storage.
 * Supports adding, removing, querying, and sorting entities.
 * Implements saving and loading the repository state to and from a file using Java Serialization.
 *
 * @param <T> the type of entities managed, must implement {@link IStorable}
 */
public final class FileDepositRepository<T extends IStorable> implements IDepositRepository<T>, IFileBasedRepository {

    private static final Logger logger = LogManager.getLogger(FileDepositRepository.class);

    private Map<UUID, T> entities = new ConcurrentHashMap<>();
    private String defaultFileName; 

    /**
     * Constructs a FileDepositRepository with the specified default filename for persistence.
     *
     * @param defaultFileName the filename to use for saving/loading entity data; must not be null
     * @throws NullPointerException if defaultFileName is null
     */
    public FileDepositRepository(final String defaultFileName) {
        this.defaultFileName = Objects.requireNonNull(defaultFileName, "Default filename cannot be null.");
    }

    /**
     * Adds or replaces an entity in the repository.
     *
     * @param entity the entity to add
     * @return true always to indicate success
     */
    @Override
    public boolean add(final T entity) {
        entities.put(entity.getId(), entity);
        return true;
    }

    /**
     * Removes the entity with the given UUID from the repository.
     *
     * @param id the UUID of the entity to remove
     * @return true if the entity was found and removed, false otherwise
     */
    @Override
    public boolean remove(final UUID id) {
        return entities.remove(id) != null;
    }
    
    /**
     * Finds an entity by its UUID.
     *
     * @param id the UUID to search for
     * @return an Optional containing the entity if found, or empty if not found
     */
    @Override
    public Optional<T> findById(final UUID id) {
        return Optional.ofNullable(entities.get(id));
    }

    /**
     * Returns a list of all entities filtered by the provided predicate and sorted by the provided comparator.
     *
     * @param filter a Predicate to filter entities, or null to include all
     * @param comparator a Comparator to sort entities, or null to leave unsorted
     * @return a list of matching entities
     */
    @Override
    public List<T> findAll(final Predicate<T> filter, final Comparator<T> comparator) {
        var stream = entities.values().stream();

        if (filter != null) 
            stream = stream.filter(filter);

        if (comparator != null) 
            stream = stream.sorted(comparator);

        return stream.collect(Collectors.toList());
    }

    /**
     * Saves the current repository state to the default file.
     *
     * @return true if the save succeeded
     * @throws DataAccessException if an I/O error occurs during saving
     */
    @Override
    public boolean save() throws DataAccessException {
        return save(defaultFileName);
    }

    /**
     * Loads the repository state from the default file.
     *
     * @return true if the load succeeded
     * @throws DataAccessException if an I/O or class loading error occurs during loading
     */
    @Override
    public boolean load() throws DataAccessException {
        return load(defaultFileName);
    }

    /**
     * Saves the current repository state to the specified file using object serialization.
     *
     * @param filename the file to save to
     * @return true if save succeeded
     * @throws DataAccessException if an I/O error occurs
     */
    @Override
    public boolean save(final String filename) throws DataAccessException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(entities);

            logger.info("Successfully saved " + entities.size() + " entities to " + filename);
            return true;

        } catch (IOException e) {
            logger.error("Failed to save data to file " + filename + ": " + e.getMessage());
            throw new DataAccessException("Failed to save data to file: " + filename, e);
        }
    }

    /**
     * Loads the repository state from the specified file using object deserialization.
     *
     * @param filename the file to load from
     * @return true if load succeeded
     * @throws DataAccessException if an I/O or class loading error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean load(final String filename) throws DataAccessException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            entities = (Map<UUID, T>) ois.readObject();

            logger.info("Successfully loaded " + entities.size() + " entities from " + filename);
            return true;

        } catch (IOException | ClassNotFoundException e) {
            logger.error("Failed to load data from file " + filename + ": " + e.getMessage());
            throw new DataAccessException("Failed to load data from file: " + filename, e);
        }
    }
}
