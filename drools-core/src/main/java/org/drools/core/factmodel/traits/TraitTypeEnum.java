/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.factmodel.traits;


public enum TraitTypeEnum {

    TRAIT,                      // trait proxy
    TRAITABLE,                  // native traitable bean
    LEGACY_TRAITABLE,           // legacy class marked as traitable, bean not yet traited (needs wrapping/injection to provide data structures
    WRAPPED_TRAITABLE,          // legacy class wrapped by a proxy to provide the core data structures.
    NON_TRAIT                   // not marked as trait/traitable

}
