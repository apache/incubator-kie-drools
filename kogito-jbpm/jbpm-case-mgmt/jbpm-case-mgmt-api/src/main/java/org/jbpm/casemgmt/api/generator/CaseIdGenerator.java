/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.casemgmt.api.generator;

import java.util.Map;

/**
 * Responsible for generating and keeping track of generated case identifiers.
 * Identifiers are always prefixed (and by that registered) by constant - default is CASE.
 * <br/>
 * In addition, it's up to generator to to return fixed size generated string to keep the IDs
 * in similar format. Recommended is to have it set to at least 10 items as part of the generated value:<br/><br/>
 * Generators should return following: <code>0000000001</code>, <code>0000000010</code>, <code>0000000100</code>
 * instead of <code>1</code>, <code>10</code>, <code>100</code> 
 */
public interface CaseIdGenerator {
    
    /**
     * Identifier of the generator so it can be found and registered at runtime
     * @return unique identifier
     */
    String getIdentifier();

    /**
     * Should be called only one time per given prefix. Subsequent calls with same prefix do not affect the generator state.
     * @param prefix unique prefix that should be used for generating case identifiers
     */
    void register(String prefix);
    
    /**
     * Unregisters given prefix from the generator. It's up to generator implementation to either remove the prefix 
     * and its latest value permanently or resume it in case of further registration of the same prefix.
     * @param prefix unique prefix that should be used for generating case identifiers
     */
    void unregister(String prefix);
    
    /**
     * Generates next value for given prefix. Returned value should include the prefix as part of the returned value.
     * @param prefix unique prefix that should be used for generating case identifiers
     * @return complete case id in format (PREFIX-GENERATED_VALUE)
     * @param optionalParameters map of optionalParameters that might be helpful for implementation
     * @throws CasePrefixNotFoundException in case given prefix was not registered
     */
    String generate(String prefix, Map<String, Object> optionalParameters) throws CasePrefixNotFoundException;
}
