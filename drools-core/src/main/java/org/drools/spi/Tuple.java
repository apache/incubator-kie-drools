/**
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

package org.drools.spi;

import java.io.Serializable;

import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;

/**
 * Partial matches are propagated through the Rete network as <code>Tuple</code>s. Each <code>Tuple</code>
 * Is able to return the <code>FactHandleImpl</code> members of the partial match for the requested pattern.
 * The pattern refers to the index position of the <code>FactHandleImpl</code> in the underlying implementation.
 * 
 * @see FactHandle;
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public interface Tuple
    extends
    Serializable {
    /**
     * Returns the <code>FactHandle</code> for the given pattern index. If the pattern is empty
     * It returns null.
     * 
     * @param pattern
     *      The index of the pattern from which the <code>FactHandleImpl</code> is to be returned
     * @return
     *      The <code>FactHandle</code>
     */
    InternalFactHandle get(int pattern);

    /**
     * Returns the <code>FactHandle</code> for the given <code>Declaration</code>, which in turn
     * specifcy the <code>Pattern</code> that they depend on.
     * 
     * @param declaration
     *      The <code>Declaration</code> which specifies the <code>Pattern</code>
     * @return
     *      The <code>FactHandle</code>
     */
    InternalFactHandle get(Declaration declaration);

    InternalFactHandle[] getFactHandles();

    /**
     * Returns the size of this tuple in number of elements (patterns)
     * @return
     */
    int size();

}
