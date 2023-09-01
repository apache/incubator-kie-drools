package org.kie.api.persistence;

public interface ObjectStoringStrategy {

    /**
     * Similar to ObjectMarshallingStrategy, it is used to 
     * decide whether this implementation is going to work for 
     * the given Object.
     * @param obj a given object
     * @return true if it can persist the given object.
     */
    boolean accept(Object obj);
    
    /**
     * Returns the key for the persisted object.
     * @param persistable the object to persist.
     * @return the key of the persisted object.
     */
    Object persist(Object persistable);
    
    /**
     * Returns the key for the persisted object.
     * @param persistable the object to persist.
     * @return the key of the persisted object.
     */
    Object update(Object persistable);
    
    /**
     * Returns the persisted object.
     * @param key the key of the persisted object.
     * @return a persisted object or null.
     */
    Object read(Object key);
}
