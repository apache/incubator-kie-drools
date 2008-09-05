package org.drools.guvnor.server.util;

import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.ActionFieldFunction;
import org.drools.guvnor.client.modeldriven.brl.ActionFieldValue;
import org.drools.guvnor.client.modeldriven.brl.ActionInsertFact;
import org.drools.guvnor.client.modeldriven.brl.ActionInsertLogicalFact;
import org.drools.guvnor.client.modeldriven.brl.ActionRetractFact;
import org.drools.guvnor.client.modeldriven.brl.ActionSetField;
import org.drools.guvnor.client.modeldriven.brl.ActionUpdateField;
import org.drools.guvnor.client.modeldriven.brl.CompositeFactPattern;
import org.drools.guvnor.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.ConnectiveConstraint;
import org.drools.guvnor.client.modeldriven.brl.DSLSentence;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.FieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.FreeFormLine;
import org.drools.guvnor.client.modeldriven.brl.IAction;
import org.drools.guvnor.client.modeldriven.brl.IPattern;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.RuleAttribute;
import org.drools.guvnor.client.modeldriven.brl.RuleModel;
import org.drools.guvnor.client.modeldriven.brl.SingleFieldConstraint;
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
     * @see org.drools.guvnor.server.util.BRLPersistence#marshal(org.drools.guvnor.client.modeldriven.brl.RuleModel)
     */
    public String marshal(RuleModel model) {
        boolean isDSLEnhanced = model.hasDSLSentences();


        StringBuffer buf = new StringBuffer();
        buf.append( "rule \"" + model.name + "\"\n" );
        this.marshalAttributes( buf,
                                model );
        buf.append( "\twhen\n" );
        this.marshalLHS( buf,
                         model,
                         isDSLEnhanced );
        buf.append( "\tthen\n" );
        this.marshalRHS( buf,
                         model,
                         isDSLEnhanced );
        buf.append( "end\n" );
        return buf.toString();
    }

    /* (non-Javadoc)
     * @see org.drools.guvnor.server.util.BRLPersistence#unmarshal(java.lang.String)
     */
    public RuleModel unmarshal(String str) {
        throw new UnsupportedOperationException("Still not possible to convert pure DRL to RuleModel");
    }

    /**
     * Marshal model attributes
     *
     * @param buf
     * @param model
     */
    private void marshalAttributes(StringBuffer buf,
                                   RuleModel model) {
        boolean hasDialect = false;
        for ( int i = 0; i < model.attributes.length; i++ ) {
            RuleAttribute attr = model.attributes[i];

            buf.append( "\t" );
            buf.append( attr );

            buf.append( "\n" );
            if (attr.attributeName.equals( "dialect" )) {
                hasDialect = true;
            }
        }
//Un comment below for mvel
        if (!hasDialect) {
            RuleAttribute attr = new RuleAttribute("dialect", "mvel");
            buf.append( "\t" );
            buf.append( attr );
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
                            RuleModel model,
                            boolean isDSLEnhanced) {
        IPattern[] lhs = model.lhs;
        LHSPatternVisitor visitor = new LHSPatternVisitor( isDSLEnhanced,
                                                           buf );
        for ( int i = 0; i < lhs.length; i++ ) {
            final IPattern cond = lhs[i];
            visitor.visit( cond );
        }
    }

    private void marshalRHS(StringBuffer buf,
                            RuleModel model,
                            boolean isDSLEnhanced) {
        IAction[] rhs = model.rhs;
        RHSActionVisitor visitor = new RHSActionVisitor( isDSLEnhanced,
                                                         buf );
        for ( int i = 0; i < rhs.length; i++ ) {
            final IAction action = rhs[i];
            visitor.visit( action );
        }
    }

    public static class LHSPatternVisitor extends ReflectiveVisitor {
        private StringBuffer buf;
        private boolean      isDSLEnhanced;

        public LHSPatternVisitor(boolean isDSLEnhanced,
                                 StringBuffer b) {
            this.isDSLEnhanced = isDSLEnhanced;
            buf = b;
        }

        public void visitFactPattern(FactPattern pattern) {
            buf.append( "\t\t" );
            if ( isDSLEnhanced ) {
                // adding passthrough markup
                buf.append( ">" );
            }
            generateFactPattern( pattern );
            buf.append( "\n" );
        }

        public void visitFreeFormLine(FreeFormLine ffl) {

        	this.buf.append("\t\t");
        	if (isDSLEnhanced) {
        		buf.append(">");
        	}
        	this.buf.append(ffl.text);
        	this.buf.append("\n");
        }

        public void visitCompositeFactPattern(CompositeFactPattern pattern) {
            buf.append( "\t\t" );
            if ( isDSLEnhanced ) {
                // adding passthrough markup
                buf.append( ">" );
            }
            if ( CompositeFactPattern.COMPOSITE_TYPE_EXISTS.equals( pattern.type ) ) {
                renderCompositeFOL(pattern);
            } else if ( CompositeFactPattern.COMPOSITE_TYPE_NOT.equals( pattern.type ) ) {
                renderCompositeFOL(pattern);
            } else if ( CompositeFactPattern.COMPOSITE_TYPE_OR.equals( pattern.type ) ) {
                buf.append( "( " );
                if (pattern.patterns != null ) {
                    for ( int i = 0; i < pattern.patterns.length; i++ ) {
                        if ( i > 0 ) {
                            buf.append( " " );
                            buf.append( pattern.type );
                            buf.append( " " );
                        }
                        renderSubPattern( pattern, i );
                    }
                }
                buf.append( " )\n" );
            }
        }

		private void renderCompositeFOL(CompositeFactPattern pattern) {
			buf.append( pattern.type );
			if (pattern.patterns != null && pattern.patterns.length > 1) {
				buf.append(" (");
				for (int i = 0; i < pattern.patterns.length; i++) {
					renderSubPattern(pattern, i);
					if (i != pattern.patterns.length -1) {
						buf.append(" and ");
					}
				}
				buf.append(") \n");
			} else {
				buf.append( " " );
				renderSubPattern( pattern, 0 );
				buf.append( "\n" );
			}
		}

        private void renderSubPattern(CompositeFactPattern pattern, int subIndex) {
            if (pattern.patterns == null || pattern.patterns.length == 0) return;
            this.generateFactPattern( pattern.patterns[subIndex] );
        }

        public void visitDSLSentence(final DSLSentence sentence) {
            buf.append( "\t\t" );
            buf.append( sentence.toString() );
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

            //top level constraints
            if (pattern.constraintList != null) {
                generateConstraints( pattern );
            }
            buf.append( ")" );
        }

        private void generateConstraints(FactPattern pattern) {
            int printedCount = 0;
            for ( int i = 0; i < pattern.getFieldConstraints().length; i++ ) {
                StringBuffer buffer = new StringBuffer();
                generateConstraint( pattern.constraintList.constraints[i], false, buffer );
                if (buffer.length() > 0) {
                    if ( printedCount > 0 ) {
                    buf.append( ", " );
                }
                    buf.append(buffer);
                    printedCount++;
                }
            }
        }

        /**
         * Recursively process the nested constraints.
         * It will only put brackets in for the ones that aren't at top level.
         * This makes for more readable DRL in the most common cases.
         */
        private void generateConstraint(FieldConstraint con, boolean nested, StringBuffer buf) {
            if (con instanceof CompositeFieldConstraint) {
                CompositeFieldConstraint cfc = (CompositeFieldConstraint) con;
                if (nested) buf.append( "( " );
                FieldConstraint[] nestedConstraints = cfc.constraints;
                if (nestedConstraints != null) {
                    for ( int i = 0; i < nestedConstraints.length; i++ ) {
                        generateConstraint( nestedConstraints[i] , true, buf);
                        if (i < (nestedConstraints.length - 1)) {
                            //buf.append(" ) ");
                            buf.append( cfc.compositeJunctionType + " ");
                            //buf.append(" ( ");
                        }
                    }
                }
                if (nested) buf.append( ")" );
            } else {
                generateSingleFieldConstraint( (SingleFieldConstraint) con, buf );
            }
        }

        private void generateSingleFieldConstraint(final SingleFieldConstraint constr, StringBuffer buf) {
            if ( constr.constraintValueType == ISingleFieldConstraint.TYPE_PREDICATE ) {
                buf.append( "eval( " );
                buf.append( constr.value );
                buf.append( " )" );
            } else {
                if ( constr.fieldBinding != null ) {
                    buf.append( constr.fieldBinding );
                    buf.append( " : " );
                }
                if ((constr.operator != null && constr.value != null)
                        || constr.fieldBinding != null) {
                    SingleFieldConstraint parent = (SingleFieldConstraint) constr.parent;
                    StringBuffer parentBuf = new StringBuffer();
                    while (parent != null) {
                        parentBuf.insert(0, parent.fieldName + ".");
                        parent = (SingleFieldConstraint) parent.parent;
                    }
                    buf.append(parentBuf);
                    buf.append( constr.fieldName );
                }

                addFieldRestriction( buf,
                                     constr.constraintValueType,
                                     constr.operator,
                                     constr.value );

                //and now do the connectives.
                if ( constr.connectives != null ) {
                    for ( int j = 0; j < constr.connectives.length; j++ ) {
                        final ConnectiveConstraint conn = constr.connectives[j];
                        addFieldRestriction( buf,
                                             conn.constraintValueType,
                                             conn.operator,
                                             conn.value );
                    }
                }
            }
        }

        /**
         * @param constr
         * @param constrDescr
         */
        private void addFieldRestriction(final StringBuffer buf,
                                         final int type,
                                         final String operator,
                                         final String value) {
            if (operator == null) {
                return;
            }

            buf.append( " " );
            buf.append( operator );
            buf.append( " " );
            switch ( type ) {
                case ISingleFieldConstraint.TYPE_RET_VALUE :
                    buf.append( "( " );
                    buf.append( value );
                    buf.append( " )" );
                    break;
                case ISingleFieldConstraint.TYPE_LITERAL :
                    buf.append( '"' );
                    buf.append( value );
                    buf.append( '"' );
                    break;
                default :
                    buf.append( value );
            }
            buf.append( " " );
        }

    }

    public static class RHSActionVisitor extends ReflectiveVisitor {
        private StringBuffer buf;
        private boolean isDSLEnhanced;
        private int          idx = 0;

        public RHSActionVisitor(boolean isDSLEnhanced, StringBuffer b) {
            this.isDSLEnhanced = isDSLEnhanced;
            buf = b;
        }

        public void visitActionInsertFact(final ActionInsertFact action) {
            this.generateInsertCall( action,
                                     false );
        }

        public void visitActionInsertLogicalFact(final ActionInsertLogicalFact action) {
            this.generateInsertCall( action,
                                     true );
        }

        public void visitFreeFormLine(FreeFormLine ffl) {

        	this.buf.append("\t\t");
        	if (isDSLEnhanced) {
        		buf.append(">");
        	}
        	this.buf.append(ffl.text);
        	this.buf.append("\n");
        }

        private void generateInsertCall(final ActionInsertFact action,
                                        final boolean isLogic) {
            buf.append( "\t\t" );
            if( isDSLEnhanced ) {
                buf.append( ">" );
            }
            if ( action.fieldValues.length == 0 ) {
            	buf.append((isLogic) ?  "insertLogical( new " : "insert( new ");

                buf.append( action.factType );
                buf.append( "() );\n" );
            } else {
                buf.append( action.factType );
                buf.append( " fact" );
                buf.append( idx );
                buf.append( " = new " );
                buf.append( action.factType );
                buf.append( "();\n" );
                generateSetMethodCalls( "fact" + idx,
                                        action.fieldValues );
                buf.append( "\t\t" );
                if (isDSLEnhanced) {
                    buf.append( ">" );
                }
                if ( isLogic ) {
                    buf.append( "insertLogical( fact" );
                } else {
                    buf.append( "insert( fact" );
                }
                buf.append( idx++ );
                buf.append( " );\n" );
            }
        }

        public void visitActionUpdateField(final ActionUpdateField action) {
            this.visitActionSetField( action );
            buf.append( "\t\t" );
            if( isDSLEnhanced ) {
                buf.append( ">" );
            }
            buf.append( "update( " );
            buf.append( action.variable );
            buf.append( " );\n" );
        }

        public void visitActionRetractFact(final ActionRetractFact action) {
            buf.append( "\t\t" );
            if( isDSLEnhanced ) {
                buf.append( ">" );
            }
            buf.append( "retract( " );
            buf.append( action.variableName );
            buf.append( " );\n" );
        }

        public void visitDSLSentence(final DSLSentence sentence) {
            buf.append( "\t\t" );
            buf.append( sentence.toString() );
            buf.append( "\n" );
        }

        public void visitActionSetField(final ActionSetField action) {
            this.generateSetMethodCalls( action.variable,
                                         action.fieldValues );
        }

        private void generateSetMethodCalls(final String variableName,
                                            final ActionFieldValue[] fieldValues) {
            for ( int i = 0; i < fieldValues.length; i++ ) {
                buf.append( "\t\t" );
                if( isDSLEnhanced ) {
                    buf.append( ">" );
                }
                buf.append( variableName );

                ActionFieldValue value = fieldValues[i];
                if (value instanceof ActionFieldFunction) {
                    buf.append(".");
                    buf.append(value.field);
                } else {
                buf.append( ".set" );
                buf.append( Character.toUpperCase( fieldValues[i].field.charAt( 0 ) ) );
                buf.append( fieldValues[i].field.substring( 1 ) );
                }
                buf.append( "( " );
                if ( fieldValues[i].isFormula() ) {
                    buf.append( fieldValues[i].value.substring( 1 ) );
                } else if( SuggestionCompletionEngine.TYPE_STRING.equals( fieldValues[i].type ) ) {
                    buf.append( "\"" );
                    buf.append( fieldValues[i].value );
                    buf.append( "\"" );
                } else {
                    buf.append( fieldValues[i].value );
                }
                buf.append( " );\n" );
            }
        }

    }

}
