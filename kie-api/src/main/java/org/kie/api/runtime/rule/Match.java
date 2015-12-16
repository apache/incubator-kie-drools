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

package org.kie.api.runtime.rule;

import org.kie.api.definition.rule.Rule;

import java.util.List;

public interface Match {

    /**
     * 
     * @return
     *     The Rule that was activated.
     */
    public Rule getRule();

    /**
     * 
     * @return
     *     The matched FactHandles for this Match
     */
    public List< ? extends FactHandle> getFactHandles();

    /**
     * Returns the list of objects that make the tuple that created
     * this Match. The objects are in the proper tuple order.
     * 
     * @return
     */
    public List<Object> getObjects();

    /**
     * Returns the list of declaration identifiers that are bound to the
     * tuple that created this Match.
     * 
     * @return
     */
    public List<String> getDeclarationIds();

    /**
     * Returns the bound declaration value for the given declaration identifier.
     * 
     * @param declarationId
     * @return
     */
    public Object getDeclarationValue(String declarationId);

}
