package org.drools.brms.server.converter;

import org.drools.RuntimeDroolsException;
import org.drools.brms.client.modeldriven.brxml.CompositeFactPattern;
import org.drools.brms.client.modeldriven.brxml.ConnectiveConstraint;
import org.drools.brms.client.modeldriven.brxml.Constraint;
import org.drools.brms.client.modeldriven.brxml.DSLSentence;
import org.drools.brms.client.modeldriven.brxml.FactPattern;
import org.drools.brms.client.modeldriven.brxml.IPattern;
import org.drools.brms.client.modeldriven.brxml.RuleAttribute;
import org.drools.brms.client.modeldriven.brxml.RuleModel;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.ColumnDescr;
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
 */
public class BRXMLToDescrConverter {

    public RuleDescr toDescr(RuleModel model,
                             String ruleName) {
        RuleDescr rule = new RuleDescr( ruleName );
        addAttributes( rule,
                       model.attributes );
        addLHS( rule,
                model.lhs );
        return rule;
    }

    private void addLHS(RuleDescr rule,
                        IPattern[] lhs) {
        AndDescr lhsDescr = new AndDescr(); 
        rule.setLhs( lhsDescr );
        for ( int i = 0; i < lhs.length; i++ ) {
            IPattern cond = lhs[i];
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

    private void addComposite(ConditionalElementDescr ce,
                              CompositeFactPattern pattern) {
        ConditionalElementDescr inner = null;
        if( CompositeFactPattern.OR.equals( pattern.type )) {
            inner = new OrDescr();
        } else if( CompositeFactPattern.NOT.equals( pattern.type )) {
            inner = new NotDescr();
        } else if( CompositeFactPattern.EXISTS.equals( pattern.type )) {
            inner = new ExistsDescr();
        }
        for( int i = 0; i < pattern.patterns.length; i++ ) {
            this.addFact( inner, pattern.patterns[i] );
        }
        ce.addDescr( (BaseDescr) inner );
    }

    private void addFact(ConditionalElementDescr ce,
                         FactPattern pattern) {
        ColumnDescr column = new ColumnDescr( pattern.factType );
        column.setIdentifier( pattern.boundName );
        ce.addDescr( column );

        for ( int i = 0; i < pattern.constraints.length; i++ ) {
            Constraint constr = pattern.constraints[i];
            if ( constr.fieldBinding != null ) {
                FieldBindingDescr fieldDescr = new FieldBindingDescr( constr.fieldName,
                                                                      constr.fieldBinding );
                column.addDescr( fieldDescr );
            }
            if ( constr.type == Constraint.TYPE_PREDICATE ) {
                PredicateDescr predicateDescr = new PredicateDescr( constr.value );
                column.addDescr( predicateDescr );
            } else {
                FieldConstraintDescr constrDescr = new FieldConstraintDescr( constr.fieldName );
                constrDescr.addRestriction( this.getFieldRestriction( constr.type,
                                                                      constr.operator,
                                                                      constr.value ) );

                if ( constr.connectives != null ) {
                    for ( int j = 0; j < constr.connectives.length; j++ ) {
                        ConnectiveConstraint conn = constr.connectives[j];
                        switch ( conn.connectiveType ) {
                            case ConnectiveConstraint.AND_CONNECTIVE :
                                RestrictionConnectiveDescr andDescr = new RestrictionConnectiveDescr( RestrictionConnectiveDescr.AND );
                                constrDescr.addRestriction( andDescr );
                                break;
                            case ConnectiveConstraint.OR_CONNECTIVE :
                                RestrictionConnectiveDescr orDescr = new RestrictionConnectiveDescr( RestrictionConnectiveDescr.OR );
                                constrDescr.addRestriction( orDescr );
                                break;
                            default :
                                // TODO: handle error
                                // unknown connective... error
                        }
                        constrDescr.addRestriction( this.getFieldRestriction( conn.constraintType,
                                                                              conn.operator,
                                                                              conn.value ) );
                    }
                }
                column.addDescr( constrDescr );
            }
        }
    }

    /**
     * @param constr
     * @param constrDescr
     */
    private RestrictionDescr getFieldRestriction(int type,
                                                 String operator,
                                                 String value) {
        switch ( type ) {
            case Constraint.TYPE_LITERAL :
                LiteralRestrictionDescr lit = new LiteralRestrictionDescr( operator,
                                                                           value );
                return lit;
            case Constraint.TYPE_VARIABLE :
                VariableRestrictionDescr var = new VariableRestrictionDescr( operator,
                                                                             value );
                return var;
            case Constraint.TYPE_ENUM :
                LiteralRestrictionDescr enu = new LiteralRestrictionDescr( operator,
                                                                           value,
                                                                           true );
                return enu;
            case Constraint.TYPE_RET_VALUE :
                ReturnValueRestrictionDescr rvc = new ReturnValueRestrictionDescr( operator,
                                                                                   value );
                return rvc;
            default :
                throw new RuntimeDroolsException( "Undefined constraint type in ROM: " + type );
        }
    }

    private void addAttributes(RuleDescr rule,
                               RuleAttribute[] attributes) {
        for ( int i = 0; i < attributes.length; i++ ) {
            RuleAttribute at = attributes[i];
            AttributeDescr attr = new AttributeDescr( at.attributeName,
                                                      at.value == null ? "true" : at.value );
            rule.addAttribute( attr );
        }
    }

}
