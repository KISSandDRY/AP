package deposit.repository.api;

/**
 * An interface for repositories that can be configured to save/load from specific files.
 */
public interface IFileBasedRepository {
    /**
     * Saves the repository's data to a specific file.
     * @param filename The path of the file to save to.
     * @return true if successful, false otherwise.
     */
    boolean save(final String filename);

    /**
     * Loads the repository's data from a specific file.
     * @param filename The path of the file to load from.
     * @return true if successful, false otherwise.
     */
    boolean load(final String filename);
}
