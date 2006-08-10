package org.drools.spi;

/*
 * Copyright 2005 JBoss Inc
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

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;

public class MockConstraint
    implements
    FieldConstraint {

    /**
     * 
     */
    private static final long serialVersionUID = 2137215908326401659L;

    public Declaration[]      declarations;

    public boolean            isAllowed        = true;

    public boolean isAllowed(final Object object,
                             final Tuple tuple,
                             final WorkingMemory workingMemory) {
        return this.isAllowed;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.declarations;
    }

}