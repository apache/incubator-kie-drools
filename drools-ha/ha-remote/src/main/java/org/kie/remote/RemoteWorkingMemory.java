/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.remote;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface RemoteWorkingMemory {

    /**
     * @return the String Id of this entry point
     */
    String getEntryPointId();

    /**
     * <p>This class is <i>not</i> a general-purpose <tt>Collection</tt>
     * implementation!  While this class implements the <tt>Collection</tt> interface, it
     * intentionally violates <tt>Collection</tt> general contract, which mandates the
     * use of the <tt>equals</tt> method when comparing objects.</p>
     *
     * <p>Instead the approach used when comparing objects with the <tt>contains(Object)</tt>
     * method is dependent on the WorkingMemory configuration, where it can be configured for <tt>Identity</tt>
     * or for <tt>Equality</tt>.</p>
     * @@return CompletableFuture of Collection<? extends Object> to read all facts from the current session as a Collection.
     */
    CompletableFuture<Collection> getObjects();

    /**
     * @param clazztype the filter to be applied to the returned collection of facts.
     * @@return CompletableFuture of Collection<? extends Object> to read all facts from the current session that are accepted by the given <code>ObjectFilter</code>.
     */
    <T> CompletableFuture<Collection<T>> getObjects(Class<T> clazztype);

    /**
     * @param namedQuery the filter to be applied to the returned collection of facts.
     * @@param namedQuery name of the query to call
     * @@param objectName name of the object to read from the QueryResultsRow
     * @@param params for the rule
     * @@return CompletableFuture of Collection<? extends Object> to read all facts from the current session that are accepted by the given <code>ObjectFilter</code>.
     */
    CompletableFuture<Collection> getObjects(String namedQuery, String objectName, Object... params);

    /**
     * @return a completable future with Long to read the total number of facts currently in this entry point
     */
    CompletableFuture<Long> getFactCount();

    /**
     * @param <code>RemoteFactHandle</code> to retrieve.
     * @@return CompletableFuture of <? extends Object> to read related RemoteFactHandle.
     */
    <T> CompletableFuture<T> getObject(RemoteFactHandle<T> remoteFactHandle);
}
