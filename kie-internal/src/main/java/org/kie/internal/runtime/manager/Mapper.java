/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.internal.runtime.manager;

import org.kie.api.runtime.manager.Context;

/**
 * <code>Mapper</code> responsibility is to provide correlation between context 
 * identifier and ksession identifier to effectively keep track of what context
 * has been mapped to given ksession.<br>
 * Mapper covers entire life cycle of the mapping which consists of:
 * <ul>
 * 	<li>storing the mapping</li>
 * 	<li>retrieving the mapping</li>
 * 	<li>removing the mapping</li>
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
