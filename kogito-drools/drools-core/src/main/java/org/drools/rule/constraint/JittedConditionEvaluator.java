package org.drools.rule.constraint;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.Declaration;
import org.drools.rule.builder.dialect.asm.GeneratorHelper;

import java.util.List;

import static org.drools.rule.builder.dialect.asm.GeneratorHelper.matchDeclarationsToTuple;
import static org.drools.rule.constraint.ASMConditionEvaluatorJitter.jitEvaluator;

public class JittedConditionEvaluator implements ConditionEvaluator {

    private final ArrayConditionEvaluator conditionEvaluator;

    private final Declaration[] declarations;

    private List<GeneratorHelper.DeclarationMatcher> declarationMatchers;

    private Object[] array;

    public JittedConditionEvaluator(ConditionAnalyzer.Condition condition, Declaration[] declarations, ClassLoader classLoader) {
        this.declarations = declarations;
        conditionEvaluator = jitEvaluator(condition, declarations, classLoader);
        if (declarations.length > 0) {
            array = new Object[declarations.length];
            if (declarations.length > 1) {
                declarationMatchers = matchDeclarationsToTuple(declarations);
            }
        }
    }

    public boolean evaluate(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        updateValues(object, workingMemory, leftTuple);
        return conditionEvaluator.evaluate(object, array);
    }

    private void updateValues(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        if (declarations.length == 0) {
            return;
        }

        if (leftTuple == null) {
            for (int i = 0; i < declarations.length; i++) {
                array[i] = declarations[i].getExtractor().getValue(workingMemory, object);
            }
            return;
        }

        if (declarations.length == 1) {
            array[0] = declarations[0].getPattern().getOffset() > leftTuple.getIndex() ?
                    declarations[0].getExtractor().getValue(workingMemory, object) :
                    declarations[0].getExtractor().getValue(workingMemory, leftTuple.get(declarations[0]).getObject());
            return;
        }

        for (GeneratorHelper.DeclarationMatcher declarationMatcher : declarationMatchers) {
            if (declarationMatcher.getRootDistance() > leftTuple.getIndex()) {
                array[declarationMatcher.getOriginalIndex()] = declarationMatcher.getDeclaration().getExtractor().getValue(workingMemory, object);
                continue;
            }
            while (declarationMatcher.getRootDistance() < leftTuple.getIndex()) {
                leftTuple = leftTuple.getParent();
            }
            array[declarationMatcher.getOriginalIndex()] = declarationMatcher.getDeclaration().getExtractor().getValue(workingMemory, leftTuple.getHandle().getObject());
        }
    }
}
