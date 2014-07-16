/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.datamodel.rule.visitors;

import java.util.Map;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.ActionUpdateField;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IFactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraintEBLeftSide;

/**
 * A Rule Model Visitor to extract Interpolation Variables (Template Keys)
 */
public class RuleModelVisitor {

    private IFactPattern factPattern;
    private RuleModel model = new RuleModel();
    private Map<InterpolationVariable, Integer> vars;

    public RuleModelVisitor() {
        //Empty constructor for Errai marshalling
    }

    public RuleModelVisitor( Map<InterpolationVariable, Integer> vars ) {
        this.vars = vars;
    }

    public RuleModelVisitor( IPattern pattern,
                             Map<InterpolationVariable, Integer> vars ) {
        this.vars = vars;
        this.model.addLhsItem( pattern );
    }

    public RuleModelVisitor( IAction action,
                             Map<InterpolationVariable, Integer> vars ) {
        this.vars = vars;
        this.model.addRhsItem( action );
    }

    private void parseStringPattern( String text ) {
        if ( text == null || text.length() == 0 ) {
            return;
        }
        int pos = 0;
        while ( ( pos = text.indexOf( "@{",
                                      pos ) ) != -1 ) {
            int end = text.indexOf( '}',
                                    pos + 2 );
            if ( end != -1 ) {
                String varName = text.substring( pos + 2,
                                                 end );
                pos = end + 1;
                InterpolationVariable var = new InterpolationVariable( varName,
                                                                       DataType.TYPE_OBJECT );
                if ( !vars.containsKey( var ) ) {
                    vars.put( var,
                              vars.size() );
                }
            }
        }
    }

    public void visit( Object o ) {
        if ( o == null ) {
            return;
        }
        if ( o instanceof RuleModel ) {
            visitRuleModel( (RuleModel) o );
        } else if ( o instanceof FactPattern ) {
            visitFactPattern( (FactPattern) o );
        } else if ( o instanceof CompositeFieldConstraint ) {
            visitCompositeFieldConstraint( (CompositeFieldConstraint) o );
        } else if ( o instanceof SingleFieldConstraintEBLeftSide ) {
            visitSingleFieldConstraint( (SingleFieldConstraintEBLeftSide) o );
        } else if ( o instanceof SingleFieldConstraint ) {
            visitSingleFieldConstraint( (SingleFieldConstraint) o );
        } else if ( o instanceof CompositeFactPattern ) {
            visitCompositeFactPattern( (CompositeFactPattern) o );
        } else if ( o instanceof FreeFormLine ) {
            visitFreeFormLine( (FreeFormLine) o );
        } else if ( o instanceof FromAccumulateCompositeFactPattern ) {
            visitFromAccumulateCompositeFactPattern( (FromAccumulateCompositeFactPattern) o );
        } else if ( o instanceof FromCollectCompositeFactPattern ) {
            visitFromCollectCompositeFactPattern( (FromCollectCompositeFactPattern) o );
        } else if ( o instanceof FromCompositeFactPattern ) {
            visitFromCompositeFactPattern( (FromCompositeFactPattern) o );
        } else if ( o instanceof DSLSentence ) {
            visitDSLSentence( (DSLSentence) o );
        } else if ( o instanceof ActionInsertFact ) {
            visitActionFieldList( (ActionInsertFact) o );
        } else if ( o instanceof ActionUpdateField ) {
            visitActionFieldList( (ActionUpdateField) o );
        } else if ( o instanceof ActionSetField ) {
            visitActionFieldList( (ActionSetField) o );
        }
    }

    //ActionInsertFact, ActionSetField, ActionpdateField
    private void visitActionFieldList( ActionInsertFact afl ) {
        String factType = afl.getFactType();
        for ( ActionFieldValue afv : afl.getFieldValues() ) {
            InterpolationVariable var = new InterpolationVariable( afv.getValue(),
                                                                   afv.getType(),
                                                                   factType,
                                                                   afv.getField() );
            if ( afv.getNature() == FieldNatureType.TYPE_TEMPLATE && !vars.containsKey( var ) ) {
                vars.put( var,
                          vars.size() );
            }
        }
    }

    private void visitActionFieldList( ActionSetField afl ) {
        String factType = model.getLHSBindingType( afl.getVariable() );
        for ( ActionFieldValue afv : afl.getFieldValues() ) {
            InterpolationVariable var = new InterpolationVariable( afv.getValue(),
                                                                   afv.getType(),
                                                                   factType,
                                                                   afv.getField() );
            if ( afv.getNature() == FieldNatureType.TYPE_TEMPLATE && !vars.containsKey( var ) ) {
                vars.put( var,
                          vars.size() );
            }
        }
    }

    private void visitActionFieldList( ActionUpdateField afl ) {
        String factType = model.getLHSBindingType( afl.getVariable() );
        for ( ActionFieldValue afv : afl.getFieldValues() ) {
            InterpolationVariable var = new InterpolationVariable( afv.getValue(),
                                                                   afv.getType(),
                                                                   factType,
                                                                   afv.getField() );
            if ( afv.getNature() == FieldNatureType.TYPE_TEMPLATE && !vars.containsKey( var ) ) {
                vars.put( var,
                          vars.size() );

            }
        }
    }

    private void visitCompositeFactPattern( CompositeFactPattern pattern ) {
        if ( pattern.getPatterns() != null ) {
            for ( IFactPattern fp : pattern.getPatterns() ) {
                visit( fp );
            }
        }
    }

    private void visitCompositeFieldConstraint( CompositeFieldConstraint cfc ) {
        if ( cfc.getConstraints() != null ) {
            for ( FieldConstraint fc : cfc.getConstraints() ) {
                visit( fc );
            }
        }
    }

    //TODO Handle definition and value
    private void visitDSLSentence( final DSLSentence sentence ) {
        String text = sentence.getDefinition();
        parseStringPattern( text );
    }

    private void visitFactPattern( FactPattern pattern ) {
        this.factPattern = pattern;
        for ( FieldConstraint fc : pattern.getFieldConstraints() ) {
            visit( fc );
        }
    }

    private void visitFreeFormLine( FreeFormLine ffl ) {
        parseStringPattern( ffl.getText() );
    }

    private void visitFromAccumulateCompositeFactPattern( FromAccumulateCompositeFactPattern pattern ) {
        visit( pattern.getFactPattern() );
        visit( pattern.getSourcePattern() );

        parseStringPattern( pattern.getActionCode() );
        parseStringPattern( pattern.getInitCode() );
        parseStringPattern( pattern.getReverseCode() );
    }

    private void visitFromCollectCompositeFactPattern( FromCollectCompositeFactPattern pattern ) {
        visit( pattern.getFactPattern() );
        visit( pattern.getRightPattern() );
    }

    private void visitFromCompositeFactPattern( FromCompositeFactPattern pattern ) {
        visit( pattern.getFactPattern() );
        ToStringExpressionVisitor visitor = new ToStringExpressionVisitor( pattern.getExpression().getBinding() );
        parseStringPattern( pattern.getExpression().getText( visitor ) );
    }

    private void visitRuleModel( RuleModel model ) {
        this.model = model;
        if ( model.lhs != null ) {
            for ( IPattern pat : model.lhs ) {
                visit( pat );
            }
        }
        if ( model.rhs != null ) {
            for ( IAction action : model.rhs ) {
                visit( action );
            }
        }
    }

    private void visitSingleFieldConstraint( SingleFieldConstraint sfc ) {
        InterpolationVariable var = new InterpolationVariable( sfc.getValue(),
                                                               sfc.getFieldType(),
                                                               ( factPattern == null ? "" : factPattern.getFactType() ),
                                                               sfc.getFieldName() );
        if ( BaseSingleFieldConstraint.TYPE_TEMPLATE == sfc.getConstraintValueType() && !vars.containsKey( var ) ) {
            vars.put( var,
                      vars.size() );
        }

        //Visit Connection constraints
        if ( sfc.getConnectives() != null ) {
            for ( int i = 0; i < sfc.getConnectives().length; i++ ) {
                final ConnectiveConstraint cc = sfc.getConnectives()[ i ];
                InterpolationVariable ccVar = new InterpolationVariable( cc.getValue(),
                                                                         cc.getFieldType(),
                                                                         ( factPattern == null ? "" : factPattern.getFactType() ),
                                                                         cc.getFieldName() );
                if ( BaseSingleFieldConstraint.TYPE_TEMPLATE == cc.getConstraintValueType() && !vars.containsKey( ccVar ) ) {
                    vars.put( ccVar,
                              vars.size() );
                }

            }
        }
    }

    private void visitSingleFieldConstraint( SingleFieldConstraintEBLeftSide sfexp ) {
        String genericType = sfexp.getExpressionLeftSide().getGenericType();
        String factType = sfexp.getExpressionLeftSide().getPreviousClassType();
        if ( factType == null ) {
            factType = sfexp.getExpressionLeftSide().getClassType();
        }
        InterpolationVariable var = new InterpolationVariable( sfexp.getValue(),
                                                               genericType,
                                                               factType,
                                                               sfexp.getFieldName() );
        if ( BaseSingleFieldConstraint.TYPE_TEMPLATE == sfexp.getConstraintValueType() && !vars.containsKey( var ) ) {
            vars.put( var,
                      vars.size() );
        }

        //Visit Connection constraints
        if ( sfexp.getConnectives() != null ) {
            for ( int i = 0; i < sfexp.getConnectives().length; i++ ) {
                final ConnectiveConstraint cc = sfexp.getConnectives()[ i ];
                InterpolationVariable ccVar = new InterpolationVariable( cc.getValue(),
                                                                         genericType,
                                                                         factType,
                                                                         cc.getFieldName() );
                if ( BaseSingleFieldConstraint.TYPE_TEMPLATE == cc.getConstraintValueType() && !vars.containsKey( ccVar ) ) {
                    vars.put( ccVar,
                              vars.size() );
                }
            }
        }

    }

}
