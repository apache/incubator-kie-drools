/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.submarine;

import java.util.Map;

/**
 * Represents data model type of objects that are usually descriptor of data holders.
 *
 */
public interface Model {

    /**
     * Returns model representation as map of members of this model type
     * @return non null map of data extracted from the model
     */
    Map<String, Object> toMap();
    
    
    void fromMap(Map<String, Object> params);
}
