/*
 * Copyright 2011 JBoss Inc
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

package org.kie.fluent;

import org.kie.io.Resource;
import org.kie.io.ResourceConfiguration;
import org.kie.io.ResourceType;

public interface FluentKnowledgeBuilder<T> {
    
    T add(Resource resource,
          ResourceType type);

    T add(Resource resource,
          ResourceType type,
          ResourceConfiguration configuration);

}
