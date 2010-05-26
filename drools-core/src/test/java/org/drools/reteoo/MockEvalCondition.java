package org.drools.reteoo;

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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.spi.EvalExpression;
import org.drools.spi.Tuple;

public class MockEvalCondition extends EvalCondition {

    /**
     *
     */
    private static final long    serialVersionUID = 400L;

    private Boolean              isAllowed;

    private final EvalExpression expression       = new EvalExpression() {
                                                      /**
                                                       *
                                                       */
                                                      private static final long serialVersionUID = 400L;

                                                      public Object createContext() { return null; }

                                                      public boolean evaluate(Tuple tuple,
                                                                              Declaration[] requiredDeclarations,
                                                                              WorkingMemory workingMemory,
                                                                              Object context ) {
                                                          return MockEvalCondition.this.isAllowed.booleanValue();
                                                      }
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        }

        public void writeExternal(ObjectOutput out) throws IOException {

        }

        public Declaration[] getRequiredDeclarations() {
            return null;
        }

        public void replaceDeclaration(Declaration declaration,
                                       Declaration resolved) {
        }
                                                  };

    public MockEvalCondition(final boolean isAllowed) {
        this( isAllowed,
              null );
    }

    public MockEvalCondition(final boolean isAllowed,
                             final Declaration[] requiredDeclarations) {
        super( requiredDeclarations );
        setEvalExpression( this.expression );
        setIsAllowed( isAllowed );
    }

    public MockEvalCondition(final EvalExpression eval,
                             final Declaration[] requiredDeclarations) {
        super( eval,
               requiredDeclarations );
    }

    public void setIsAllowed(final boolean isAllowed) {
        this.isAllowed = new Boolean( isAllowed );
    }
}