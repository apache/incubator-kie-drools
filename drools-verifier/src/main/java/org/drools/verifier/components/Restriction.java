package org.drools.verifier.components;

import org.drools.base.evaluators.Operator;
import org.drools.verifier.report.components.Cause;

/**
 * 
 * @author Toni Rikkola
 */
public abstract class Restriction extends PatternComponent
    implements
    Cause {

    public static class RestrictionType {
        public static final RestrictionType LITERAL                  = new RestrictionType( "LITERAL" );
        public static final RestrictionType VARIABLE                 = new RestrictionType( "VARIABLE" );
        public static final RestrictionType QUALIFIED_IDENTIFIER     = new RestrictionType( "QUALIFIED_IDENTIFIER" );
        public static final RestrictionType RETURN_VALUE_RESTRICTION = new RestrictionType( "RETURN_VALUE_RESTRICTION" );
        public static final RestrictionType ENUM                     = new RestrictionType( "ENUM" );

        protected final String              type;

        private RestrictionType(String t) {
            type = t;
        }
    }

    private boolean    patternIsNot;
    private String     constraintPath;

    // Id of the field that this restriction is related to.
    private String     fieldPath;

    protected Operator operator;

    public abstract RestrictionType getRestrictionType();

    public Restriction(Pattern pattern) {
        super( pattern );
    }

    @Override
    public String getPath() {
        return String.format( "%s/restriction[%s]",
                              getParentPath(),
                              getOrderNumber() );
    }

    @Override
    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.RESTRICTION;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getConstraintPath() {
        return constraintPath;
    }

    public void setConstraintPath(String constraintPath) {
        this.constraintPath = constraintPath;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String path) {
        this.fieldPath = path;
    }

    public boolean isPatternIsNot() {
        return patternIsNot;
    }

    public void setPatternIsNot(boolean patternIsNot) {
        this.patternIsNot = patternIsNot;
    }
}
