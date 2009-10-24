package org.drools.verifier.components;

import org.drools.base.evaluators.Operator;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.CauseType;

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
    private String     constraintGuid;

    // Id of the field that this restriction is related to.
    private String     fieldGuid;

    protected Operator operator;

    public CauseType getCauseType() {
        return CauseType.RESTRICTION;
    }

    public abstract RestrictionType getRestrictionType();

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

    public String getConstraintGuid() {
        return constraintGuid;
    }

    public void setConstraintGuid(String constraintGuid) {
        this.constraintGuid = constraintGuid;
    }

    public String getFieldGuid() {
        return fieldGuid;
    }

    public void setFieldGuid(String guid) {
        this.fieldGuid = guid;
    }

    public boolean isPatternIsNot() {
        return patternIsNot;
    }

    public void setPatternIsNot(boolean patternIsNot) {
        this.patternIsNot = patternIsNot;
    }
}
