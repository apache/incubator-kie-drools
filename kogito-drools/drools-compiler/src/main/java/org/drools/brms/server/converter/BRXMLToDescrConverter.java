package org.drools.brms.server.converter;

import org.drools.RuntimeDroolsException;
import org.drools.brms.client.modeldriven.brxml.CompositeFactPattern;
import org.drools.brms.client.modeldriven.brxml.ConnectiveConstraint;
import org.drools.brms.client.modeldriven.brxml.Constraint;
import org.drools.brms.client.modeldriven.brxml.DSLSentence;
import org.drools.brms.client.modeldriven.brxml.FactPattern;
import org.drools.brms.client.modeldriven.brxml.IConstraint;
import org.drools.brms.client.modeldriven.brxml.IPattern;
import org.drools.brms.client.modeldriven.brxml.RuleAttribute;
import org.drools.brms.client.modeldriven.brxml.RuleModel;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.RestrictionConnectiveDescr;
import org.drools.lang.descr.RestrictionDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.VariableRestrictionDescr;

/** 
 * This class will convert BRXML to Descriptors, allowing to roundtrip between 
 * sipported formats (DRL, XML, CLP, etc).
 * 
 * It will work off the RuleModel object graph, primarily.
 * 
 * @author Edson Tirelli
 * @deprecated Use BRDRLPersistence instead
 */
public class BRXMLToDescrConverter {

    public RuleDescr toDescr(final RuleModel model,
                             final String ruleName) {
        final RuleDescr rule = new RuleDescr( ruleName );
        addAttributes( rule,
                       model.attributes );
        addLHS( rule,
                model.lhs );
        return rule;
    }

    private void addLHS(final RuleDescr rule,
                        final IPattern[] lhs) {
        final AndDescr lhsDescr = new AndDescr();
        rule.setLhs( lhsDescr );
        for ( int i = 0; i < lhs.length; i++ ) {
            final IPattern cond = lhs[i];
            if ( cond instanceof DSLSentence ) {
                // need to decide what to do with DSL sentences
                //render((DSLSentence) cond, buf);
            } else if ( cond instanceof FactPattern ) {
                addFact( lhsDescr,
                         (FactPattern) cond );
            } else if ( cond instanceof CompositeFactPattern ) {
                addComposite( lhsDescr,
                              (CompositeFactPattern) cond );
            }
        }

    }

    private void addComposite(final ConditionalElementDescr ce,
                              final CompositeFactPattern pattern) {
        ConditionalElementDescr inner = null;
        if ( CompositeFactPattern.COMPOSITE_TYPE_OR.equals( pattern.type ) ) {
            inner = new OrDescr();
        } else if ( CompositeFactPattern.COMPOSITE_TYPE_NOT.equals( pattern.type ) ) {
            inner = new NotDescr();
        } else if ( CompositeFactPattern.COMPOSITE_TYPE_EXISTS.equals( pattern.type ) ) {
            inner = new ExistsDescr();
        }
        for ( int i = 0; i < pattern.patterns.length; i++ ) {
            this.addFact( inner,
                          pattern.patterns[i] );
        }
        ce.addDescr( (BaseDescr) inner );
    }

    private void addFact(final ConditionalElementDescr ce,
                         final FactPattern factPattern) {
        final PatternDescr pattern = new PatternDescr( factPattern.factType );
        pattern.setIdentifier( factPattern.boundName );
        ce.addDescr( pattern );

        for ( int i = 0; i < factPattern.constraints.length; i++ ) {
            final Constraint constr = factPattern.constraints[i];
            if ( constr.fieldBinding != null ) {
                final FieldBindingDescr fieldDescr = new FieldBindingDescr( constr.fieldName,
                                                                      constr.fieldBinding );
                pattern.addConstraint( fieldDescr );
            }
            if ( constr.constraintValueType == IConstraint.TYPE_PREDICATE ) {
                final PredicateDescr predicateDescr = new PredicateDescr( constr.value );
                pattern.addConstraint( predicateDescr );
            } else {
                final FieldConstraintDescr constrDescr = new FieldConstraintDescr( constr.fieldName );
                constrDescr.addRestriction( this.getFieldRestriction( constr.constraintValueType,
                                                                      constr.operator,
                                                                      constr.value ) );

                if ( constr.connectives != null ) {
                    for ( int j = 0; j < constr.connectives.length; j++ ) {
                        final ConnectiveConstraint conn = constr.connectives[j];
                        if ( conn.isANDConnective() ) {
                            final RestrictionConnectiveDescr andDescr = new RestrictionConnectiveDescr( RestrictionConnectiveDescr.AND );
                            constrDescr.addRestriction( andDescr );
                        } else if ( conn.isORConnective() ) {
                            final RestrictionConnectiveDescr orDescr = new RestrictionConnectiveDescr( RestrictionConnectiveDescr.OR );
                            constrDescr.addRestriction( orDescr );
                        } else {
                            throw new IllegalStateException( "Unknown connective type/operator: [" + conn.operator + "]" );
                        }

                        constrDescr.addRestriction( this.getFieldRestriction( conn.constraintValueType,
                                                                              conn.operator,
                                                                              conn.value ) );
                    }
                }
                pattern.addConstraint( constrDescr );
            }
        }
    }

    /**
     * @param constr
     * @param constrDescr
     */
    private RestrictionDescr getFieldRestriction(final int type,
                                                 final String operator,
                                                 final String value) {
        switch ( type ) {
            case IConstraint.TYPE_LITERAL :
                final LiteralRestrictionDescr lit = new LiteralRestrictionDescr( operator,
                                                                           value );
                return lit;
            case IConstraint.TYPE_VARIABLE :
                final VariableRestrictionDescr var = new VariableRestrictionDescr( operator,
                                                                             value );
                return var;
            case IConstraint.TYPE_ENUM :
                final LiteralRestrictionDescr enu = new LiteralRestrictionDescr( operator,
                                                                           value,
                                                                           true );
                return enu;
            case IConstraint.TYPE_RET_VALUE :
                final ReturnValueRestrictionDescr rvc = new ReturnValueRestrictionDescr( operator,
                                                                                   value );
                return rvc;
            default :
                throw new RuntimeDroolsException( "Undefined constraint type in ROM: " + type );
        }
    }

    private void addAttributes(final RuleDescr rule,
                               final RuleAttribute[] attributes) {
        for ( int i = 0; i < attributes.length; i++ ) {
            final RuleAttribute at = attributes[i];
            final AttributeDescr attr = new AttributeDescr( at.attributeName,
                                                      at.value == null ? "true" : at.value );
            rule.addAttribute( attr );
        }
    }

}
