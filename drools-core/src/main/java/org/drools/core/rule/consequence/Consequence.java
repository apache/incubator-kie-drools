/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.rule.consequence;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.rule.accessor.CompiledInvoker;
import org.drools.core.rule.accessor.Invoker;
import org.kie.internal.security.KiePolicyHelper;

/**
 * Consequence to be fired upon successful match of a <code>Rule</code>.
 */
public interface Consequence
    extends
        Invoker {
    
    String getName();
    
    /**
     * Execute the consequence for the supplied matching <code>Tuple</code>.
     * 
     * @param knowledgeHelper
     * @param reteEvaluator
     *            The working memory session.
     * @throws ConsequenceException
     *             If an error occurs while attempting to invoke the
     *             consequence.
     */
    void evaluate(KnowledgeHelper knowledgeHelper,
                  ReteEvaluator reteEvaluator) throws Exception;

    class SafeConsequence implements Consequence, Serializable {
        private static final long serialVersionUID = -8109957972163261899L;
        private final Consequence delegate;
        public SafeConsequence( Consequence delegate ) {
            this.delegate = delegate;
        }

        @Override
        public String getName() {
            return this.delegate.getName();
        }

        @Override
        public void evaluate(final KnowledgeHelper knowledgeHelper, final ReteEvaluator reteEvaluator) throws Exception {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
                delegate.evaluate(knowledgeHelper, reteEvaluator);
                return null;
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public boolean wrapsCompiledInvoker() {
            return this.delegate instanceof CompiledInvoker;
        }
    }
}
