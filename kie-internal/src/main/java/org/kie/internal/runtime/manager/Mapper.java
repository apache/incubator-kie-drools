package org.kie.internal.runtime.manager;

import org.kie.api.runtime.manager.Context;

/**
 * <code>Mapper</code> responsibility is to provide correlation between context
 * identifier and ksession identifier to effectively keep track of what context
 * has been mapped to given ksession.<br>
 * Mapper covers entire life cycle of the mapping which consists of:
 * <ul>
 *  <li>storing the mapping</li>
 *  <li>retrieving the mapping</li>
 *  <li>removing the mapping</li>
 * </ul>
 *
 */
public interface Mapper {

    /**
     * Stores context to ksession id mapping
     * @param context instance of the context to be stored
     * @param ksessionId actual identifier of ksession
     */
    void saveMapping(Context<?> context, Long ksessionId, String ownerId);

    /**
     * Finds ksession for given context
     * @param context instance of the context
     * @return ksession identifier when found otherwise null
     */
    Long findMapping(Context<?> context, String ownerId);

    /**
     * Finds context by ksession identifier
     * @param ksessionId identifier of ksession
     * @return context instance when wound otherwise null
     */
    Object findContextId(Long ksessionId, String ownerId);

    /**
     * Remove permanently context to ksession id mapping
     * @param context context instance that mapping shall be removed for
     */
    void removeMapping(Context<?> context, String ownerId);
}
