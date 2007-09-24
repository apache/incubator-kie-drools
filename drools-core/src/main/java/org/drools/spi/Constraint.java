package org.drools.spi;

import org.drools.rule.Declaration;

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

public interface Constraint
    extends
    RuleComponent {

    /**
     * Returns all the declarations required by the given 
     * constraint implementation.
     * 
     * @return
     */
    Declaration[] getRequiredDeclarations();

    /**
     * A constraint may be required to replace an old
     * declaration object by a new updated one
     * 
     * @param oldDecl
     * @param newDecl
     */
    void replaceDeclaration(Declaration oldDecl,
                            Declaration newDecl);
}