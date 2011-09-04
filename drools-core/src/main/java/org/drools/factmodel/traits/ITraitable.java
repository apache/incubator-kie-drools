/*
 * Copyright 2011 JBoss Inc
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

package org.drools.factmodel.traits;

import java.util.Map;
import java.util.Set;

public interface ITraitable<K> {

    public static final String MAP_FIELD_NAME = "__$$dynamic_properties_map$$";
    public String TRAITSET_FIELD_NAME = "__$$dynamic_traits_set$$";

    public Map<String,Object> getDynamicProperties();

    public Map<String, ? extends IThing<K>> getTraits();

}
