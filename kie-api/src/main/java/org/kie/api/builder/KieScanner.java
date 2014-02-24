/*
 * Copyright 2013 JBoss Inc
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

package org.kie.api.builder;

import org.kie.api.Service;

/**
 * A KieScanner is a scanner of the maven repositories (both local and remote)
 * used to automatically discover if there are new releases for a given KieModule and its dependencies
 * and eventually deploy them in the KieRepository
 */
public interface KieScanner {

    /**
     * Starts this KieScanner polling the maven repositories with the given interval expressed in milliseconds
     * @throws An IllegalStateException if this KieScanner has been already started
     */
    void start(long pollingInterval);

    /**
     * Stops this KieScanner
     */
    void stop();

    /**
     * Triggers a synchronous scan
     */
    void scanNow();
}
