package deposit.domain.api;

import java.util.UUID;

/**
 * Interface representing an entity that can be uniquely identified and stored.
 * Provides a method to retrieve the universally unique identifier (UUID) of the entity.
 */
public interface IStorable { 

    /**
     * Returns the unique identifier (UUID) of this storable entity.
     *
     * @return the UUID of the entity
     */
    UUID getId();
}
