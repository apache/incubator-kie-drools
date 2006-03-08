package org.drools.reteoo;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.spi.EvalExpression;
import org.drools.spi.Tuple;

public class MockEvalCondition extends EvalCondition {

    private static EvalExpression trueExpression  = new EvalExpression() {
                                                      public boolean evaluate(Tuple tuple,
                                                                              Declaration[] requiredDeclarations,
                                                                              WorkingMemory workingMemory) {
                                                          return true;
                                                      }
                                                  };

    private static EvalExpression falseExpression = new EvalExpression() {
                                                      public boolean evaluate(Tuple tuple,
                                                                              Declaration[] requiredDeclarations,
                                                                              WorkingMemory workingMemory) {
                                                          return false;
                                                      }
                                                  };

    public MockEvalCondition(boolean isAllowed) {
        this( isAllowed,
              null );
    }

    public MockEvalCondition(boolean isAllowed,
                             Declaration[] requiredDeclarations) {
        this( (isAllowed) ? trueExpression : falseExpression,
              requiredDeclarations );
    }

    public MockEvalCondition(EvalExpression eval,
                             Declaration[] requiredDeclarations) {
        super( eval,
               requiredDeclarations );
    }
}
