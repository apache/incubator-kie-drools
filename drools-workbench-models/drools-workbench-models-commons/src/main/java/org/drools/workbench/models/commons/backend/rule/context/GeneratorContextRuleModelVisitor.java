/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.commons.backend.rule.context;

import java.util.Set;

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
import org.drools.workbench.models.datamodel.rule.visitors.ToStringExpressionVisitor;

/**
 * A Rule Model Visitor to extract Interpolation Variables (Template Keys). This version
 * also identifies whether components of a RuleModel use non-template constraints.
 */
public class GeneratorContextRuleModelVisitor {

    private IFactPattern factPattern;
    private RuleModel model = new RuleModel();
    private Set<InterpolationVariable> vars;
    private boolean hasNonTemplateOutput;

    public GeneratorContextRuleModelVisitor( final Set<InterpolationVariable> vars ) {
        this.vars = vars;
    }

    public GeneratorContextRuleModelVisitor( final IPattern pattern,
                                             final Set<InterpolationVariable> vars ) {
        this.vars = vars;
        this.model.addLhsItem( pattern );
    }

    public GeneratorContextRuleModelVisitor( final IAction action,
                                             final Set<InterpolationVariable> vars ) {
        this.vars = vars;
        this.model.addRhsItem( action );
    }

    private void parseStringPattern( final String text ) {
        if ( text == null || text.length() == 0 ) {
            return;
        }
        int pos = 0;
        while ( ( pos = text.indexOf( "@{",
                                      pos ) ) != -1 ) {
            int end = text.indexOf( '}',
                                    pos + 2 );
            if ( end != -1 ) {
                final String varName = text.substring( pos + 2,
                                                       end );
                pos = end + 1;
                InterpolationVariable var = new InterpolationVariable( varName,
                                                                       DataType.TYPE_OBJECT );
                if ( !vars.contains( var ) ) {
                    vars.add( var );
                }
            }
        }
    }

    public void visit( final Object o ) {
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
        } else if ( o instanceof ActionFieldValue ) {
            visitActionFieldValue( (ActionFieldValue) o );
        }
    }

    //ActionInsertFact, ActionSetField, ActionpdateField
    private void visitActionFieldList( final ActionInsertFact afl ) {
        String factType = afl.getFactType();
        for ( ActionFieldValue afv : afl.getFieldValues() ) {
            InterpolationVariable var = new InterpolationVariable( afv.getValue(),
                                                                   afv.getType(),
                                                                   factType,
                                                                   afv.getField() );
            if ( afv.getNature() == FieldNatureType.TYPE_TEMPLATE && !vars.contains( var ) ) {
                vars.add( var );
            } else {
                hasNonTemplateOutput = true;
            }
        }
    }

    private void visitActionFieldList( final ActionSetField afl ) {
        String factType = model.getLHSBindingType( afl.getVariable() );
        for ( ActionFieldValue afv : afl.getFieldValues() ) {
            InterpolationVariable var = new InterpolationVariable( afv.getValue(),
                                                                   afv.getType(),
                                                                   factType,
                                                                   afv.getField() );
            if ( afv.getNature() == FieldNatureType.TYPE_TEMPLATE && !vars.contains( var ) ) {
                vars.add( var );
            } else {
                hasNonTemplateOutput = true;
            }
        }
    }

    private void visitActionFieldList( final ActionUpdateField afl ) {
        String factType = model.getLHSBindingType( afl.getVariable() );
        for ( ActionFieldValue afv : afl.getFieldValues() ) {
            InterpolationVariable var = new InterpolationVariable( afv.getValue(),
                                                                   afv.getType(),
                                                                   factType,
                                                                   afv.getField() );
            if ( afv.getNature() == FieldNatureType.TYPE_TEMPLATE && !vars.contains( var ) ) {
                vars.add( var );
            } else {
                hasNonTemplateOutput = true;
            }
        }
    }

    private void visitActionFieldValue( final ActionFieldValue afv ) {
        if ( afv.getNature() != FieldNatureType.TYPE_TEMPLATE ) {
            hasNonTemplateOutput = true;
        }
    }

    private void visitCompositeFactPattern( final CompositeFactPattern pattern ) {
        if ( pattern.getPatterns() != null ) {
            for ( IFactPattern fp : pattern.getPatterns() ) {
                visit( fp );
            }
        }
    }

    private void visitCompositeFieldConstraint( final CompositeFieldConstraint cfc ) {
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

    private void visitFactPattern( final FactPattern pattern ) {
        this.factPattern = pattern;
        for ( FieldConstraint fc : pattern.getFieldConstraints() ) {
            visit( fc );
        }
    }

    private void visitFreeFormLine( final FreeFormLine ffl ) {
        parseStringPattern( ffl.getText() );
    }

    private void visitFromAccumulateCompositeFactPattern( final FromAccumulateCompositeFactPattern pattern ) {
        visit( pattern.getFactPattern() );
        visit( pattern.getSourcePattern() );

        parseStringPattern( pattern.getActionCode() );
        parseStringPattern( pattern.getInitCode() );
        parseStringPattern( pattern.getReverseCode() );
    }

    private void visitFromCollectCompositeFactPattern( final FromCollectCompositeFactPattern pattern ) {
        visit( pattern.getFactPattern() );
        visit( pattern.getRightPattern() );
    }

    private void visitFromCompositeFactPattern( final FromCompositeFactPattern pattern ) {
        visit( pattern.getFactPattern() );
        ToStringExpressionVisitor visitor = new ToStringExpressionVisitor();
        parseStringPattern( pattern.getExpression().getText( visitor ) );
    }

    private void visitRuleModel( final RuleModel model ) {
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

    private void visitSingleFieldConstraint( final SingleFieldConstraint sfc ) {
        final InterpolationVariable var = new InterpolationVariable( sfc.getValue(),
                                                                     sfc.getFieldType(),
                                                                     ( factPattern == null ? "" : factPattern.getFactType() ),
                                                                     sfc.getFieldName() );
        if ( BaseSingleFieldConstraint.TYPE_TEMPLATE == sfc.getConstraintValueType() && !vars.contains( var ) ) {
            vars.add( var );
        } else {
            hasNonTemplateOutput = true;
        }

        //Visit Connection constraints
        if ( sfc.getConnectives() != null ) {
            for ( int i = 0; i < sfc.getConnectives().length; i++ ) {
                final ConnectiveConstraint cc = sfc.getConnectives()[ i ];
                InterpolationVariable ccVar = new InterpolationVariable( cc.getValue(),
                                                                         cc.getFieldType(),
                                                                         ( factPattern == null ? "" : factPattern.getFactType() ),
                                                                         cc.getFieldName() );
                if ( BaseSingleFieldConstraint.TYPE_TEMPLATE == cc.getConstraintValueType() && !vars.contains( ccVar ) ) {
                    vars.add( ccVar );
                } else {
                    hasNonTemplateOutput = true;
                }

            }
        }
    }

    private void visitSingleFieldConstraint( final SingleFieldConstraintEBLeftSide sfexp ) {
        final String genericType = sfexp.getExpressionLeftSide().getGenericType();
        String factType = sfexp.getExpressionLeftSide().getPreviousClassType();
        if ( factType == null ) {
            factType = sfexp.getExpressionLeftSide().getClassType();
        }
        final InterpolationVariable var = new InterpolationVariable( sfexp.getValue(),
                                                                     genericType,
                                                                     factType,
                                                                     sfexp.getFieldName() );
        if ( BaseSingleFieldConstraint.TYPE_TEMPLATE == sfexp.getConstraintValueType() && !vars.contains( var ) ) {
            vars.add( var );
        } else {
            hasNonTemplateOutput = true;
        }

        //Visit Connection constraints
        if ( sfexp.getConnectives() != null ) {
            for ( int i = 0; i < sfexp.getConnectives().length; i++ ) {
                final ConnectiveConstraint cc = sfexp.getConnectives()[ i ];
                InterpolationVariable ccVar = new InterpolationVariable( cc.getValue(),
                                                                         genericType,
                                                                         factType,
                                                                         cc.getFieldName() );
                if ( BaseSingleFieldConstraint.TYPE_TEMPLATE == cc.getConstraintValueType() && !vars.contains( ccVar ) ) {
                    vars.add( ccVar );
                } else {
                    hasNonTemplateOutput = true;
                }
            }
        }
    }

    public boolean hasNonTemplateOutput() {
        return hasNonTemplateOutput;
    }

}
