package org.drools.modelcompiler.constraints;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.Tuple;
import org.drools.core.time.Interval;
import org.drools.model.Index;
import org.drools.model.SingleConstraint;

import java.util.stream.Stream;

public class ConstraintEvaluator {

    protected final SingleConstraint constraint;

    private final Declaration[] declarations;
    private final Declaration[] requiredDeclarations;
    private final Pattern pattern;
    private final Declaration patternDeclaration;

    public ConstraintEvaluator(Pattern pattern, SingleConstraint constraint) {
        this.constraint = constraint;
        this.pattern = pattern;
        this.declarations = new Declaration[] { pattern.getDeclaration() };
        this.patternDeclaration = findPatternDeclaration();
        this.requiredDeclarations = new Declaration[0];
    }

    public ConstraintEvaluator(Declaration[] declarations, Pattern pattern, SingleConstraint constraint) {
        this.constraint = constraint;
        this.pattern = pattern;
        this.declarations = declarations;
        this.patternDeclaration = findPatternDeclaration();
        this.requiredDeclarations = Stream.of( declarations )
                                          .filter( d -> !d.getIdentifier().equals( pattern.getDeclaration().getIdentifier() ) )
                                          .toArray( Declaration[]::new );
    }

    private Declaration findPatternDeclaration() {
        for ( Declaration declaration : declarations ) {
            if ( pattern.getDeclaration().getIdentifier().equals( declaration.getIdentifier() ) ) {
                return declaration;
            }
        }
        return null;
    }

    public boolean evaluate( InternalFactHandle handle, InternalWorkingMemory workingMemory ) {
        return constraint.getPredicate().test( declarations.length == 1 ?
                                               new Object[] { handle.getObject() } :
                                               getAlphaInvocationArgs( handle, workingMemory ) );
    }

    public Object[] getAlphaInvocationArgs( InternalFactHandle handle, InternalWorkingMemory workingMemory ) {
        Object[] params = new Object[declarations.length];
        for (int i = 0; i < params.length; i++) {
            params[i] = getArgument( handle, workingMemory, declarations[i], null );
        }
        return params;
    }

    private Object getArgument( InternalFactHandle handle, InternalWorkingMemory workingMemory, Declaration declaration, Tuple tuple ) {
        return declaration == patternDeclaration ?
                    handle.getObject() :
                    declaration.getValue( workingMemory, tuple != null ? tuple.getObject(declaration.getPattern().getOffset()) : null );
    }

    public boolean evaluate(InternalFactHandle handle, Tuple tuple, InternalWorkingMemory workingMemory) {
        return constraint.getPredicate().test( getBetaInvocationArgs( handle, tuple, workingMemory ) );
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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return getId().equals(((ConstraintEvaluator) other).getId());
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
        return new ConstraintEvaluator( Stream.of(declarations)
                                              .map( Declaration::clone )
                                              .toArray(Declaration[]::new),
                                        pattern,
                                        constraint );
    }

    public boolean isTemporal() {
        return false;
    }

    public Interval getInterval() {
        throw new UnsupportedOperationException();
    }
}
