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

package org.drools.jsr94.rules;

/*
 * $Id: Jsr94FactHandle.java,v 1.14 2005/02/04 02:13:38 mproctor Exp $
 *
 * Copyright 2003-2004 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import javax.rules.Handle;

import org.drools.core.common.DefaultFactHandle;
import org.kie.api.runtime.rule.EntryPoint;

/**
 * The Drools implementation of the <code>Handle</code> interface which provides
 * a marker interface for Drools-specific object identity mechanism. When using
 * the <code>StatefulRuleSession</code> objects that are added to rule session
 * state are identified using a Drools-supplied <code>Handle</code>
 * implementation.
 * <p/>
 * <code>Handle</code>s are used to unambigiously identify objects within the
 * rule session state and should not suffer many of the object identity issues
 * that arise when using muliple class loaders, serializing
 * <code>StatefulRuleSessions</code>, or using <code>Object.equals</code> or
 * <code>object1 == object2</code> reference equality.
 */
public class Jsr94FactHandle extends DefaultFactHandle
    implements
    Handle {

    private static final long serialVersionUID = 510l;

    /**
     * Constructs a new <code>Handle</code>.
     *
     * @param id A unique <code>Handle</code> id.
     * @param recency A value indicating the recency of this <code>Handle</code>
     *        (more recently created <code>Handle</code>s have greater values
     *         than <code>Handle</code>s created further in the past)
     *
     * @see org.kie.conflict.RecencyConflictResolver
     */
    Jsr94FactHandle(final long id,
                    final Object object,
                    final long recency,
                    final EntryPoint entryPoint) {
        super( (int) id,
               object,
               recency,
               entryPoint );
    }
}
