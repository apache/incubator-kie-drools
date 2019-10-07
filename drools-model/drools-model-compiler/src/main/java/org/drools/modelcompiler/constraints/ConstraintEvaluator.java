package org.drools.modelcompiler.constraints;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.Tuple;
import org.drools.core.time.Interval;
import org.drools.model.BitMask;
import org.drools.model.Index;
import org.drools.model.SingleConstraint;

public class ConstraintEvaluator {

    protected final SingleConstraint constraint;

    private final Declaration[] declarations;
    private Declaration[] requiredDeclarations;

    private final Declaration patternDeclaration;
    private final Pattern pattern;

    public ConstraintEvaluator(Declaration[] declarations, SingleConstraint constraint) {
        this.constraint = constraint;
        this.pattern = null;
        this.declarations = declarations;
        this.requiredDeclarations = declarations;
        this.patternDeclaration = null;
    }

    public ConstraintEvaluator(Pattern pattern, SingleConstraint constraint) {
        this.constraint = constraint;
        this.pattern = pattern;
        this.declarations = new Declaration[] { pattern.getDeclaration() };
        this.patternDeclaration = findPatternDeclaration();
        this.requiredDeclarations = new Declaration[0];
    }

    public ConstraintEvaluator(Declaration[] declarations, Pattern pattern, SingleConstraint constraint) {
        this.constraint = constraint;
        this.declarations = declarations;
        this.pattern = pattern;
        this.patternDeclaration = findPatternAndRequiredDeclaration();
    }

    private Declaration findPatternDeclaration() {
        for ( int i = 0; i < declarations.length; i++ ) {
            if ( pattern.getDeclaration().getIdentifier().equals( declarations[i].getIdentifier() ) ) {
                return declarations[i];
            }
        }
        return null;
    }

    private Declaration findPatternAndRequiredDeclaration() {
        Declaration patternDeclaration = null;
        List<Declaration> requiredDeclarationsList = new ArrayList<>();
        for ( int i = 0; i < declarations.length; i++ ) {
            if ( pattern.getDeclaration() != null && pattern.getDeclaration().getIdentifier().equals( declarations[i].getIdentifier() ) ) {
                patternDeclaration = declarations[i];
            } else {
                requiredDeclarationsList.add(declarations[i]);
            }
        }
        this.requiredDeclarations = requiredDeclarationsList.toArray( new Declaration[requiredDeclarationsList.size()] );
        return patternDeclaration;
    }

    public boolean evaluate( InternalFactHandle handle, InternalWorkingMemory workingMemory ) {
        try {
            return constraint.getPredicate().test( declarations.length == 1 ?
                                                   getSingleArg( handle, workingMemory ) :
                                                   getAlphaInvocationArgs( handle, workingMemory ) );
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    private Object[] getSingleArg( InternalFactHandle handle, InternalWorkingMemory workingMemory ) {
        return declarations[0].isInternalFact() ? new Object[] { declarations[0].getValue( workingMemory, handle.getObject() ) } : new Object[] { handle.getObject() };
    }

    public Object[] getAlphaInvocationArgs( InternalFactHandle handle, InternalWorkingMemory workingMemory ) {
        Object[] params = new Object[declarations.length];
        for (int i = 0; i < params.length; i++) {
            params[i] = getArgument( handle, workingMemory, declarations[i], null );
        }
        return params;
    }

    private Object getArgument( InternalFactHandle handle, InternalWorkingMemory workingMemory, Declaration declaration, Tuple tuple ) {
        if (declaration == patternDeclaration) {
            return handle.getObject();
        } else {
            Object object = tuple != null && declaration.getPattern().getOffset() < tuple.size() ? tuple.getObject(declaration.getPattern().getOffset()) : handle.getObject();
            return declaration.getValue(workingMemory, object);
        }
    }

    public boolean evaluate(InternalFactHandle handle, Tuple tuple, InternalWorkingMemory workingMemory) {
        try {
            return constraint.getPredicate().test( getBetaInvocationArgs( handle, tuple, workingMemory ) );
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    private Object[] getBetaInvocationArgs( InternalFactHandle handle, Tuple tuple, InternalWorkingMemory workingMemory ) {
        Object[] params = new Object[declarations.length];
        for (int i = 0; i < declarations.length; i++) {
            params[i] = getArgument( handle, workingMemory, declarations[i], tuple );
        }
        return params;
    }

    protected InternalFactHandle[] getBetaInvocationFactHandles( InternalFactHandle handle, Tuple tuple ) {
        InternalFactHandle[] fhs = new InternalFactHandle[declarations.length];
        for (int i = 0; i < fhs.length; i++) {
            fhs[i] = declarations[i] == patternDeclaration ?
                     handle :
                     tuple.get(declarations[i].getPattern().getOffset());
        }
        return fhs;
    }

    public Index getIndex() {
        return constraint.getIndex();
    }

    public String[] getReactiveProps() {
        return constraint.getReactiveProps();
    }

    public BitMask getReactivityBitMask() {
        return constraint.getReactivityBitMask();
    }

    @Override
    public String toString() {
        return constraint.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ConstraintEvaluator otherEval = (ConstraintEvaluator) other;
        if (!getId().equals(otherEval.getId())) return false;
        if (declarations.length != otherEval.declarations.length) return false;
        for (int i = 0; i < declarations.length; i++) {
            if (!declarations[i].getExtractor().equals( otherEval.declarations[i].getExtractor() )) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public String getId() {
        return constraint.getExprId();
    }

    public Declaration[] getRequiredDeclarations() {
        return requiredDeclarations;
    }

    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {
        for ( int i = 0; i < declarations.length; i++) {
            if ( declarations[i].equals( oldDecl )) {
                declarations[i] = newDecl;
                break;
            }
        }
    }

    public ConstraintEvaluator clone() {
        return pattern == null ?
                new ConstraintEvaluator( getClonedDeclarations(), constraint ) :
                new ConstraintEvaluator( getClonedDeclarations(), pattern, constraint );
    }

    protected Declaration[] getClonedDeclarations() {
        Declaration[] clonedDeclarations = new Declaration[declarations.length];
        for (int i = 0; i < declarations.length; i++) {
            clonedDeclarations[i] = declarations[i].clone();
        }
        return clonedDeclarations;
    }

    protected Declaration[] getDeclarations() {
        return declarations;
    }

    protected Pattern getPattern() {
        return pattern;
    }

    public boolean isTemporal() {
        return false;
    }

    public Interval getInterval() {
        throw new UnsupportedOperationException();
    }
}
