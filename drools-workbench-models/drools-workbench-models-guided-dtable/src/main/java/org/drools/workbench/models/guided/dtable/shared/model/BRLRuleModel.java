/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.drools.workbench.models.guided.dtable.shared.model.adaptors.ActionInsertFactCol52ActionInsertFactAdaptor;
import org.drools.workbench.models.guided.dtable.shared.model.adaptors.ActionInsertFactCol52ActionInsertLogicalFactAdaptor;
import org.drools.workbench.models.guided.dtable.shared.model.adaptors.ConditionCol52FieldConstraintAdaptor;
import org.drools.workbench.models.guided.dtable.shared.model.adaptors.Pattern52FactPatternAdaptor;

/**
 * A RuleModel that can provide details of bound Facts and Fields from an
 * associated Decision Table. This allows columns using BRL fragments to
 * integrate with Decision Table columns
 */
public class BRLRuleModel extends RuleModel {

    private static final long serialVersionUID = 540l;

    private GuidedDecisionTable52 dtable;

    public BRLRuleModel() {
    }

    public BRLRuleModel(final GuidedDecisionTable52 dtable) {
        PortablePreconditions.checkNotNull("dtable",
                                           dtable);
        this.dtable = dtable;
    }

    @Override
    public List<String> getLHSBoundFacts() {
        final Set<String> facts = new HashSet<String>();
        for (CompositeColumn<? extends BaseColumn> col : dtable.getConditions()) {
            if (col instanceof Pattern52) {
                final Pattern52 p = (Pattern52) col;
                if (p.isBound()) {
                    facts.add(p.getBoundName());
                }
            } else if (col instanceof BRLConditionColumn) {
                //Delegate to super class's implementation
                final RuleModel rm = new RuleModel();
                final BRLConditionColumn brl = (BRLConditionColumn) col;
                rm.lhs = brl.getDefinition().toArray(new IPattern[brl.getDefinition().size()]);
                facts.addAll(rm.getLHSBoundFacts());
            }
        }
        facts.addAll(super.getLHSBoundFacts());
        return new ArrayList<>(facts);
    }

    @Override
    public FactPattern getLHSBoundFact(final String var) {
        for (CompositeColumn<? extends BaseColumn> col : dtable.getConditions()) {
            if (col instanceof Pattern52) {
                final Pattern52 p = (Pattern52) col;
                if (p.isBound() && p.getBoundName().equals(var)) {
                    return new Pattern52FactPatternAdaptor(p);
                }
            } else if (col instanceof BRLConditionColumn) {
                //Delegate to super class's implementation
                final RuleModel rm = new RuleModel();
                final BRLConditionColumn brl = (BRLConditionColumn) col;
                rm.lhs = brl.getDefinition().toArray(new IPattern[brl.getDefinition().size()]);
                final FactPattern fp = rm.getLHSBoundFact(var);
                if (fp != null) {
                    return fp;
                }
            }
        }
        return super.getLHSBoundFact(var);
    }

    @Override
    public SingleFieldConstraint getLHSBoundField(final String var) {
        for (CompositeColumn<? extends BaseColumn> col : dtable.getConditions()) {
            if (col instanceof Pattern52) {
                final Pattern52 p = (Pattern52) col;
                for (ConditionCol52 cc : p.getChildColumns()) {
                    if (cc.isBound() && cc.getBinding().equals(var)) {
                        final ConditionCol52FieldConstraintAdaptor sfcAdaptor = new ConditionCol52FieldConstraintAdaptor(cc);
                        sfcAdaptor.setFactType(p.getFactType());
                        return sfcAdaptor;
                    }
                }
            } else if (col instanceof BRLConditionColumn) {
                //Delegate to super class's implementation
                final RuleModel rm = new RuleModel();
                final BRLConditionColumn brl = (BRLConditionColumn) col;
                rm.lhs = brl.getDefinition().toArray(new IPattern[brl.getDefinition().size()]);
                final SingleFieldConstraint sfc = rm.getLHSBoundField(var);
                if (sfc != null) {
                    return sfc;
                }
            }
        }
        return super.getLHSBoundField(var);
    }

    @Override
    public String getLHSBindingType(final String var) {
        for (CompositeColumn<? extends BaseColumn> col : dtable.getConditions()) {
            if (col instanceof Pattern52) {
                final Pattern52 p = (Pattern52) col;
                if (p.isBound() && p.getBoundName().equals(var)) {
                    return p.getFactType();
                }
                for (ConditionCol52 cc : p.getChildColumns()) {
                    if (cc.isBound() && cc.getBinding().equals(var)) {
                        return cc.getFieldType();
                    }
                }
            } else if (col instanceof BRLConditionColumn) {
                //Delegate to super class's implementation
                final RuleModel rm = new RuleModel();
                final BRLConditionColumn brl = (BRLConditionColumn) col;
                rm.lhs = brl.getDefinition().toArray(new IPattern[brl.getDefinition().size()]);
                final String type = rm.getLHSBindingType(var);
                if (type != null) {
                    return type;
                }
            }
        }
        return super.getLHSBindingType(var);
    }

    @Override
    public FactPattern getLHSParentFactPatternForBinding(final String var) {
        for (CompositeColumn<? extends BaseColumn> col : dtable.getConditions()) {
            if (col instanceof Pattern52) {
                final Pattern52 p = (Pattern52) col;
                if (p.isBound() && p.getBoundName().equals(var)) {
                    return new Pattern52FactPatternAdaptor(p);
                }
                for (ConditionCol52 cc : p.getChildColumns()) {
                    if (cc.isBound() && cc.getBinding().equals(var)) {
                        return new Pattern52FactPatternAdaptor(p);
                    }
                }
            } else if (col instanceof BRLConditionColumn) {
                //Delegate to super class's implementation
                final RuleModel rm = new RuleModel();
                final BRLConditionColumn brl = (BRLConditionColumn) col;
                rm.lhs = brl.getDefinition().toArray(new IPattern[brl.getDefinition().size()]);
                final FactPattern fp = rm.getLHSParentFactPatternForBinding(var);
                if (fp != null) {
                    return fp;
                }
            }
        }
        return super.getLHSParentFactPatternForBinding(var);
    }

    @Override
    public List<String> getAllVariables() {
        final Set<String> variables = new HashSet<String>();
        variables.addAll(getAllLHSVariables());
        variables.addAll(getAllRHSVariables());
        return new ArrayList<String>(variables);
    }

    @Override
    public List<String> getAllLHSVariables() {
        final Set<String> variables = new HashSet<String>();
        for (CompositeColumn<? extends BaseColumn> col : dtable.getConditions()) {
            if (col instanceof Pattern52) {
                final Pattern52 p = (Pattern52) col;
                if (p.isBound()) {
                    variables.add(p.getBoundName());
                }
                for (ConditionCol52 cc : p.getChildColumns()) {
                    if (cc.isBound()) {
                        variables.add(cc.getBinding());
                    }
                }
            } else if (col instanceof BRLConditionColumn) {
                //Delegate to super class's implementation
                final RuleModel rm = new RuleModel();
                final BRLConditionColumn brl = (BRLConditionColumn) col;
                rm.lhs = brl.getDefinition().toArray(new IPattern[brl.getDefinition().size()]);
                variables.addAll(rm.getAllLHSVariables());
            }
        }
        variables.addAll(super.getAllLHSVariables());
        return new ArrayList<String>(variables);
    }

    @Override
    public List<String> getAllRHSVariables() {
        final Set<String> variables = new HashSet<String>();
        for (ActionCol52 col : dtable.getActionCols()) {
            if (col instanceof ActionInsertFactCol52) {
                final ActionInsertFactCol52 action = (ActionInsertFactCol52) col;
                variables.add(action.getBoundName());
            } else if (col instanceof BRLActionColumn) {
                //Delegate to super class's implementation
                final RuleModel rm = new RuleModel();
                final BRLActionColumn brl = (BRLActionColumn) col;
                rm.rhs = brl.getDefinition().toArray(new IAction[brl.getDefinition().size()]);
                variables.addAll(rm.getAllRHSVariables());
            }
        }
        variables.addAll(super.getAllRHSVariables());
        return new ArrayList<String>(variables);
    }

    @Override
    public boolean isBoundFactUsed(final String binding) {
        for (ActionCol52 col : dtable.getActionCols()) {
            if (col instanceof ActionInsertFactCol52) {
                final ActionInsertFactCol52 action = (ActionInsertFactCol52) col;
                if (action.getBoundName().equals(binding)) {
                    return true;
                }
            } else if (col instanceof ActionRetractFactCol52) {

                if (col instanceof LimitedEntryActionRetractFactCol52) {

                    //Check whether Limited Entry retraction is bound to Pattern
                    final LimitedEntryActionRetractFactCol52 ler = (LimitedEntryActionRetractFactCol52) col;
                    if (ler.getValue().getStringValue().equals(binding)) {
                        return false;
                    }
                } else {

                    //Check whether data for column contains Pattern binding
                    final int colIndex = dtable.getExpandedColumns().indexOf(col);
                    for (List<DTCellValue52> row : dtable.getData()) {
                        DTCellValue52 cell = row.get(colIndex);
                        if (cell != null && cell.getStringValue().equals(binding)) {
                            return true;
                        }
                    }
                }
            } else if (col instanceof BRLActionColumn) {
                //Delegate to super class's implementation
                final RuleModel rm = new RuleModel();
                final BRLActionColumn brl = (BRLActionColumn) col;
                rm.rhs = brl.getDefinition().toArray(new IAction[brl.getDefinition().size()]);
                if (rm.isBoundFactUsed(binding)) {
                    return true;
                }
            }
        }
        return super.isBoundFactUsed(binding);
    }

    @Override
    public List<String> getBoundVariablesInScope(final BaseSingleFieldConstraint con) {
        final Set<String> variables = new HashSet<String>();
        for (CompositeColumn<? extends BaseColumn> col : dtable.getConditions()) {
            if (col instanceof Pattern52) {
                final Pattern52 p = (Pattern52) col;
                if (p.isBound()) {
                    variables.add(p.getBoundName());
                }
                for (ConditionCol52 cc : p.getChildColumns()) {
                    if (cc.isBound()) {
                        variables.add(cc.getBinding());
                    }
                }
            } else if (col instanceof BRLConditionColumn) {
                //Delegate to super class's implementation
                final RuleModel rm = new RuleModel();
                final BRLConditionColumn brl = (BRLConditionColumn) col;
                rm.lhs = brl.getDefinition().toArray(new IPattern[brl.getDefinition().size()]);
                variables.addAll(rm.getBoundVariablesInScope(con));
            }
        }
        variables.addAll(super.getBoundVariablesInScope(con));
        return new ArrayList<String>(variables);
    }

    @Override
    public boolean isVariableNameUsed(String s) {
        return super.isVariableNameUsed(s);
    }

    @Override
    public List<String> getRHSBoundFacts() {
        final Set<String> variables = new HashSet<String>();
        for (ActionCol52 col : dtable.getActionCols()) {
            if (col instanceof ActionInsertFactCol52) {
                final ActionInsertFactCol52 action = (ActionInsertFactCol52) col;
                variables.add(action.getBoundName());
            } else if (col instanceof BRLActionColumn) {
                //Delegate to super class's implementation
                final RuleModel rm = new RuleModel();
                final BRLActionColumn brl = (BRLActionColumn) col;
                rm.rhs = brl.getDefinition().toArray(new IAction[brl.getDefinition().size()]);
                variables.addAll(rm.getRHSBoundFacts());
            }
        }
        variables.addAll(super.getRHSBoundFacts());
        return new ArrayList<String>(variables);
    }

    @Override
    public ActionInsertFact getRHSBoundFact(final String var) {
        for (ActionCol52 col : dtable.getActionCols()) {
            if (col instanceof ActionInsertFactCol52) {
                final ActionInsertFactCol52 action = (ActionInsertFactCol52) col;
                if (action.getBoundName().equals(var)) {
                    if (action.isInsertLogical()) {
                        return new ActionInsertFactCol52ActionInsertLogicalFactAdaptor(action);
                    }
                    return new ActionInsertFactCol52ActionInsertFactAdaptor(action);
                }
            } else if (col instanceof BRLActionColumn) {
                //Delegate to super class's implementation
                final RuleModel rm = new RuleModel();
                final BRLActionColumn brl = (BRLActionColumn) col;
                rm.rhs = brl.getDefinition().toArray(new IAction[brl.getDefinition().size()]);
                final ActionInsertFact aif = rm.getRHSBoundFact(var);
                if (aif != null) {
                    return aif;
                }
            }
        }
        return super.getRHSBoundFact(var);
    }
}
