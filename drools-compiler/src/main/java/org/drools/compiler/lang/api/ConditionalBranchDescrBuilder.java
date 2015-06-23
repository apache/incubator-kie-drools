/*
 * Copyright 2015 JBoss Inc
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

package org.drools.compiler.lang.api;

import org.drools.compiler.lang.descr.ConditionalBranchDescr;

public interface ConditionalBranchDescrBuilder<P extends DescrBuilder< ? , ? >> extends DescrBuilder<P, ConditionalBranchDescr> {

    /**
     * Defines the condition for this conditional branch
     *
     * @return a descriptor builder for the EVAL CE
     */
    EvalDescrBuilder<ConditionalBranchDescrBuilder<P>> condition();

    /**
     * Defines a Consequence activated when the condition is evaluated to true
     *
     * @return a descriptor builder for the Named Consequence CE
     */
    NamedConsequenceDescrBuilder<ConditionalBranchDescrBuilder<P>> consequence();

    /**
     * Defines a else branch used when the condition is evaluated to false
     *
     * @return a descriptor builder for the else Conditional Branch CE
     */
    ConditionalBranchDescrBuilder<P> otherwise();
}
