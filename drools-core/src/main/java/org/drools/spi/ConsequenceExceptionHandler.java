/**
 * Copyright 2010 JBoss Inc
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

package org.drools.spi;

import org.drools.spi.Activation;
import org.drools.WorkingMemory;


/**
 * Care should be taken when implementing this class. Swallowing of consequence can be dangerous
 * if the exception occured during a WorkingMemory action, thus leaving the integrity of the
 * WorkingMemory invalid.
 *
 */
public interface ConsequenceExceptionHandler {
    void handleException(Activation activation, WorkingMemory workingMemory, Exception exception);
}
