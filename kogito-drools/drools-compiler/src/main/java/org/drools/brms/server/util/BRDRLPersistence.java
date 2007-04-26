package org.drools.brms.server.util;

import org.drools.brms.client.modeldriven.brxml.ActionAssertFact;
import org.drools.brms.client.modeldriven.brxml.ActionAssertLogicalFact;
import org.drools.brms.client.modeldriven.brxml.ActionFieldValue;
import org.drools.brms.client.modeldriven.brxml.ActionModifyField;
import org.drools.brms.client.modeldriven.brxml.ActionRetractFact;
import org.drools.brms.client.modeldriven.brxml.ActionSetField;
import org.drools.brms.client.modeldriven.brxml.CompositeFactPattern;
import org.drools.brms.client.modeldriven.brxml.ConnectiveConstraint;
import org.drools.brms.client.modeldriven.brxml.Constraint;
import org.drools.brms.client.modeldriven.brxml.DSLSentence;
import org.drools.brms.client.modeldriven.brxml.FactPattern;
import org.drools.brms.client.modeldriven.brxml.IAction;
import org.drools.brms.client.modeldriven.brxml.IConstraint;
import org.drools.brms.client.modeldriven.brxml.IPattern;
import org.drools.brms.client.modeldriven.brxml.RuleModel;
import org.drools.util.ReflectiveVisitor;

/**
 * This class persists the rule model to DRL and back
 * 
 * @author etirelli
 */
public class BRDRLPersistence
    implements
    BRLPersistence {

    private static final BRLPersistence INSTANCE = new BRDRLPersistence();

    private BRDRLPersistence() {
    }

    public static BRLPersistence getInstance() {
        return INSTANCE;
    }

    /* (non-Javadoc)
     * @see org.drools.brms.server.util.BRLPersistence#marshal(org.drools.brms.client.modeldriven.brxml.RuleModel)
     */
    public String marshal(RuleModel model) {
        StringBuffer buf = new StringBuffer();
        buf.append( "rule \"" + model.name + "\"\n" );
        this.marshalAttributes( buf,
                                model );
        buf.append( "\twhen\n" );
        this.marshalLHS( buf,
                         model );
        buf.append( "\tthen\n" );
        this.marshalRHS( buf,
                         model );
        buf.append( "end\n" );
        return buf.toString();
    }

    /* (non-Javadoc)
     * @see org.drools.brms.server.util.BRLPersistence#unmarshal(java.lang.String)
     */
    public RuleModel unmarshal(String str) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Marshal model attributes
     * 
     * @param buf
     * @param model
     */
    private void marshalAttributes(StringBuffer buf,
                                   RuleModel model) {
        for ( int i = 0; i < model.attributes.length; i++ ) {
            buf.append( "\t" );
            buf.append( model.attributes[i] );
            buf.append( "\n" );
        }
    }

    /**
     * Marshal LHS patterns
     * 
     * @param buf
     * @param model
     */
    private void marshalLHS(StringBuffer buf,
                            RuleModel model) {
        IPattern[] lhs = model.lhs;
        LHSPatternVisitor visitor = new LHSPatternVisitor( buf );
        for ( int i = 0; i < lhs.length; i++ ) {
            final IPattern cond = lhs[i];
            visitor.visit( cond );
        }
    }

    private void marshalRHS(StringBuffer buf,
                            RuleModel model) {
        IAction[] rhs = model.rhs;
        RHSActionVisitor visitor = new RHSActionVisitor( buf );
        for ( int i = 0; i < rhs.length; i++ ) {
            final IAction action = rhs[i];
            visitor.visit( action );
        }
    }


    public static class LHSPatternVisitor extends ReflectiveVisitor {
        private StringBuffer buf;

        public LHSPatternVisitor( StringBuffer b ) {
            buf = b;
        }
        
        public void visitFactPattern( FactPattern pattern ) {
            buf.append( "\t\t" );
            generateFactPattern( pattern );
            buf.append( "\n" );
        }
        
        public void visitCompositeFactPattern( CompositeFactPattern pattern ) {
            buf.append( "\t\t" );
            if( CompositeFactPattern.COMPOSITE_TYPE_EXISTS.equals( pattern.type ) ) {
                buf.append( pattern.type );
                buf.append( " " );
                this.generateFactPattern( pattern.patterns[0] );
                buf.append( "\n" );
            } else if( CompositeFactPattern.COMPOSITE_TYPE_NOT.equals( pattern.type ) ) {
                buf.append( pattern.type );
                buf.append( " " );
                this.generateFactPattern( pattern.patterns[0] );
                buf.append( "\n" );
            } else if( CompositeFactPattern.COMPOSITE_TYPE_OR.equals( pattern.type ) ) {
                buf.append( "( " );
                for( int i = 0; i < pattern.patterns.length; i++ ) {
                    if( i > 0 ) {
                        buf.append( " " );
                        buf.append( pattern.type );
                        buf.append( " " );
                    }
                    this.generateFactPattern( pattern.patterns[0] );
                }
                buf.append( " )\n" );
            }
        }

        public void visitDSLSentence( final DSLSentence sentence ) {
            buf.append( "\t\t" );
            buf.append( sentence.sentence );
            buf.append( "\n" );
        }
        
        private void generateFactPattern(FactPattern pattern) {
            if ( pattern.boundName != null ) {
                buf.append( pattern.boundName );
                buf.append( " : " );
            }
            if ( pattern.factType != null ) {
                buf.append( pattern.factType );
            }
            buf.append( "( " );

            for ( int i = 0; i < pattern.constraints.length; i++ ) {
                if( i > 0 ) {
                    buf.append( ", " );
                }
                final Constraint constr = pattern.constraints[i];
                if ( constr.constraintValueType == IConstraint.TYPE_PREDICATE ) {
                    buf.append( "( " );
                    buf.append( constr.value );
                    buf.append( " )" );
                } else {
                    if ( constr.fieldBinding != null ) {
                        buf.append( constr.fieldBinding );
                        buf.append( " : " );
                    }
                    buf.append( constr.fieldName );

                    addFieldRestriction( buf,
                                         constr.constraintValueType,
                                         constr.operator,
                                         constr.value );

                    if ( constr.connectives != null ) {
                        for ( int j = 0; j < constr.connectives.length; j++ ) {
                            final ConnectiveConstraint conn = constr.connectives[j];
                            if ( conn.isANDConnective() ) {
                                buf.append( "&" );
                            } else if ( conn.isORConnective() ) {
                                buf.append( "|" );
                            } else {
                                throw new IllegalStateException( "Unknown connective type/operator: [" + conn.operator + "]" );
                            }

                            addFieldRestriction( buf,
                                                 conn.constraintValueType,
                                                 conn.operator,
                                                 conn.value );
                        }
                    }
                }
            }
            buf.append( ")" );
        }

        /**
         * @param constr
         * @param constrDescr
         */
        private void addFieldRestriction(final StringBuffer buf,
                                         final int type,
                                         final String operator,
                                         final String value) {
            buf.append( " " );
            buf.append( operator );
            buf.append( " " );
            switch ( type ) {
                case IConstraint.TYPE_RET_VALUE :
                    buf.append( "( " );
                    buf.append( operator );
                    buf.append( " )" );
                    break;
                default :
                    buf.append( value );
            }
            buf.append( " " );
        }
        
    }
    
    public static class RHSActionVisitor extends ReflectiveVisitor {
        private StringBuffer buf;
        private int idx = 0;

        public RHSActionVisitor( StringBuffer b ) {
            buf = b;
        }
        
        public void visitActionAssertFact( final ActionAssertFact action ) {
            this.generateAssertCall( action, false );
        }

        public void visitActionAssertLogicalFact( final ActionAssertLogicalFact action ) {
            this.generateAssertCall( action, false );
        }
        
        private void generateAssertCall( final ActionAssertFact action, final boolean isLogic ) {
            if( action.fieldValues.length == 0 ) {
                buf.append( "\t\tassert( new " );
                buf.append( action.factType );
                buf.append( "() );\n" );
            } else {
                buf.append( "\t\t" );
                buf.append( action.factType );
                buf.append( " fact" );
                buf.append( idx );
                buf.append( " = new " );
                buf.append( action.factType );
                buf.append( "();\n" );
                generateSetMethodCalls( "fact"+idx, action.fieldValues );
                if( isLogic ) {
                    buf.append( "\t\tassertLogical( fact" );
               } else {
                    buf.append( "\t\tassert( fact" );
                }
                buf.append( idx++ );
                buf.append( " );\n" );
            }
        }

        public void visitActionModifyField( final ActionModifyField action ) {
            this.visitActionSetField( action );
            buf.append( "\t\tmodify( " );
            buf.append( action.variable );
            buf.append( " );\n" );
        }
        
        public void visitActionRetractFact( final ActionRetractFact action ) {
            buf.append( "\t\tretract( " );
            buf.append( action.variableName );
            buf.append( " );\n" );
        }
        
        public void visitDSLSentence( final DSLSentence sentence ) {
            buf.append( "\t\t" );
            buf.append( sentence.sentence );
            buf.append( "\n" );
        }
        
        public void visitActionSetField( final ActionSetField action ) {
            this.generateSetMethodCalls( action.variable, action.fieldValues );
        }
        
        private void generateSetMethodCalls(final String variableName, final ActionFieldValue[] fieldValues) {
            for( int i = 0; i < fieldValues.length; i++ ) {
                buf.append( "\t\t" );
                buf.append( variableName );
                buf.append( ".set" );
                buf.append( Character.toUpperCase( fieldValues[i].field.charAt( 0 ) ) );
                buf.append( fieldValues[i].field.substring( 1 ) );
                buf.append( "( " );
                if( fieldValues[i].isFormula() ) {
                    buf.append( fieldValues[i].value );
                } else {
                    buf.append( "\"" );
                    buf.append( fieldValues[i].value );
                    buf.append( "\"" );
                }
                buf.append( " );\n" );
            }
        }
        
    }

}
