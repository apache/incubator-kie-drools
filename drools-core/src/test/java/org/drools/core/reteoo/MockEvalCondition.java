package org.drools.core.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.EvalCondition;
import org.drools.base.rule.accessor.EvalExpression;

public class MockEvalCondition extends EvalCondition {

    private static final long    serialVersionUID = 510l;

    private Boolean              isAllowed;

    private final EvalExpression expression       = new EvalExpression() {
                                                      private static final long serialVersionUID = 510l;

                                                      public Object createContext() { return null; }

                                                      public boolean evaluate(BaseTuple tuple,
                                                                              Declaration[] requiredDeclarations,
                                                                              ValueResolver valueResolver,
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
        public EvalExpression clone() {
            return this;
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
        this.isAllowed = Boolean.valueOf( isAllowed );
    }
}
