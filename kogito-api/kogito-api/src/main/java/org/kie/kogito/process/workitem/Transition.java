/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.process.workitem;

import java.util.List;

/**
 * Defines work item life cycle phase transition.
 * Including data and policies to be enforced during transition.
 *
 * @param <T> type of data the transition is carrying
 */
public interface Transition<T> {

    /**
     * Returns target phase where work item should be transitioned
     * @return target life cycle phase
     */
    String phase();
    
    /**
     * Optional data to be associated with the transition. 
     * This usually means appending given data into the work item.
     * @return data if given otherwise null
     */
    T data();
    
    /**
     * Optional list of policies to be enforced during transition
     * @return list of policies or an empty list, should never be null
     */
    List<Policy<?>> policies();
}
