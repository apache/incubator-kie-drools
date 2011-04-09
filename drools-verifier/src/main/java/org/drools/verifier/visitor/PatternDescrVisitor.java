/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.visitor;

import org.drools.lang.descr.*;
import org.drools.verifier.components.*;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.solver.Solvers;

public class PatternDescrVisitor extends ConditionalElementDescrVisitor {

    private ObjectType objectType;
    private Pattern pattern;
    private WorkingMemory workingMemory = null;

    public PatternDescrVisitor(VerifierData data,
                               VerifierRule rule,
                               Solvers solvers) {
        super(data,
                solvers);
        this.rule = rule;
    }

    public void visit(PatternDescr descr,
                      VerifierComponent parent,
                      int orderNumber) throws UnknownDescriptionException {
        visitPatternDescr(descr,
                parent,
                orderNumber);

    }

    public void visit(PatternDescr descr,
                      int orderNumber) throws UnknownDescriptionException {
        visitPatternDescr(descr,
                null,
                orderNumber);
    }

    private Pattern visitPatternDescr(PatternDescr descr,
                                      VerifierComponent parent,
                                      int orderNumber) throws UnknownDescriptionException {

        objectType = data.getObjectTypeByFullName(descr.getObjectType());

        if (objectType == null) {
            Import objectImport = data.getImportByName(descr.getObjectType());

            if (objectImport != null) {
                objectType = ObjectTypeFactory.createObjectType(objectImport);
            } else {
                objectType = ObjectTypeFactory.createObjectType(descr.getObjectType());
            }

            data.add(objectType);
        }

        pattern = new Pattern(rule);
        if (parent != null) {
            pattern.setParentPath(parent.getPath());
            pattern.setParentType(parent.getVerifierComponentType());
        }
        pattern.setObjectTypePath(objectType.getPath());
        pattern.setName(objectType.getName());
        pattern.setPatternNot(solvers.getRuleSolver().isChildNot());
        pattern.setPatternExists(solvers.getRuleSolver().isExists());
        pattern.setPatternForall(solvers.getRuleSolver().isForall());
        pattern.setOrderNumber(orderNumber);

        if (descr.getIdentifier() != null) {
            PatternVariable patternVariable = new PatternVariable(rule);
            patternVariable.setName(descr.getIdentifier());

            patternVariable.setParentPath(pattern.getPath());
            patternVariable.setParentType(pattern.getVerifierComponentType());

            data.add(patternVariable);
        }

        // visit source.
        if (descr.getSource() != null) {
            visit(descr.getSource());
        } else {
            if (workingMemory == null) {
                workingMemory = new WorkingMemory();
                data.add(workingMemory);
            }
            pattern.setSourcePath(workingMemory.getPath());
            pattern.setSourceType(workingMemory.getVerifierComponentType());
        }

        visit(descr.getBindings());

        solvers.startPatternSolver(pattern);

        visit(descr.getConstraint());

        solvers.endPatternSolver();

        data.add(pattern);

        return pattern;
    }

    @Override
    protected VerifierComponent getParent() {
        return pattern;
    }

    @Override
    protected void visit(Object descr) throws UnknownDescriptionException {
        if (descr instanceof AccumulateDescr) {
            visitAccumulateDescr((AccumulateDescr) descr);
        } else if (descr instanceof CollectDescr) {
            visitCollectDescr((CollectDescr) descr);
        } else if (descr instanceof EntryPointDescr) {
            visitEntryPointDescr((EntryPointDescr) descr);
        } else if (descr instanceof FromDescr) {
            visitFromDescr((FromDescr) descr);
        } else if (descr instanceof AndDescr) {
            visitAndDescr((AndDescr) descr);
        } else if (descr instanceof OrDescr) {
            visitOrDescr((OrDescr) descr);
        } else if (descr instanceof FieldConstraintDescr) {
            visitFieldConstraintDescr((FieldConstraintDescr) descr);
        } else if (descr instanceof BindingDescr) {
            visitBindingDescr((BindingDescr) descr);
        } else if (descr instanceof PredicateDescr) {
            visitPredicateDescr((PredicateDescr) descr);
        } else if (descr instanceof ExprConstraintDescr) {
            visit((ExprConstraintDescr) descr);
        } else {
            throw new UnknownDescriptionException((BaseDescr) descr);
        }
    }

    private void visit(ExprConstraintDescr descr) {
        ExprConstraintDescrVisitor exprConstraintDescrVisitor = new ExprConstraintDescrVisitor(pattern, data, orderNumber, solvers);
        exprConstraintDescrVisitor.visit(descr);
    }

    /**
     * End leaf
     *
     * @param descr
     * @return
     */
    private PatternEval visitPredicateDescr(PredicateDescr descr) {

        PatternEval predicate = new PatternEval(pattern);
        predicate.setContent(descr.getContent().toString());
        predicate.setClassMethodName(descr.getClassMethodName());
        predicate.setOrderNumber(orderNumber.next());
        predicate.setParentPath(pattern.getPath());
        predicate.setParentType(pattern.getVerifierComponentType());

        data.add(predicate);

        solvers.addPatternComponent(predicate);

        return predicate;
    }

    /**
     * End
     *
     * @param descr
     */
    private void visitBindingDescr(BindingDescr descr) {
        Field field = new Field();
        field.setName(descr.getExpression());
        field.setObjectTypeName(objectType.getName());
        field.setObjectTypePath(objectType.getPath());
        data.add(field);

        FieldVariable fieldVariable = new FieldVariable(pattern);
        fieldVariable.setParentPath(field.getPath());
        fieldVariable.setName(descr.getVariable());
        fieldVariable.setOrderNumber(orderNumber.next());

        data.add(fieldVariable);
    }

    private void visitFieldConstraintDescr(FieldConstraintDescr descr) throws UnknownDescriptionException {
        FieldConstraintDescrVisitor visitor = new FieldConstraintDescrVisitor(data,
                pattern,
                solvers,
                orderNumber.next());
        visitor.visitFieldConstraintDescr(descr);
    }

    private void visitEntryPointDescr(EntryPointDescr descr) throws UnknownDescriptionException {
        // Check if already exists
        EntryPoint entryPoint = data.getEntryPointByEntryId(descr.getEntryId());

        if (entryPoint == null) {
            entryPoint = new EntryPoint();

            entryPoint.setEntryPointName(descr.getEntryId());

            data.add(entryPoint);
        }

        pattern.setSourcePath(entryPoint.getPath());
        pattern.setSourceType(entryPoint.getVerifierComponentType());
    }

    /**
     * End leaf
     *
     * @param descr
     * @return
     * @throws UnknownDescriptionException
     */
    private VerifierFromDescr visitFromDescr(FromDescr descr) throws UnknownDescriptionException {
        VerifierFromDescr from = new VerifierFromDescr(pattern);

        VerifierComponent ds = visit(descr.getDataSource());

        from.setDataSourcePath(ds.getPath());
        from.setDataSourceType(ds.getVerifierComponentType());
        from.setParentPath(rule.getPath());
        from.setParentType(rule.getVerifierComponentType());

        data.add(from);

        return from;
    }

    private VerifierCollectDescr visitCollectDescr(CollectDescr descr) throws UnknownDescriptionException {
        VerifierCollectDescr collect = new VerifierCollectDescr(pattern);
        collect.setClassMethodName(descr.getClassMethodName());

        PatternDescrVisitor visitor = new PatternDescrVisitor(data,
                rule,
                solvers);

        visitor.visit(descr.getInputPattern(),
                collect,
                0);
        collect.setParentPath(rule.getPath());
        collect.setParentType(rule.getVerifierComponentType());

        data.add(collect);

        return collect;
    }

    private VerifierAccumulateDescr visitAccumulateDescr(AccumulateDescr descr) throws UnknownDescriptionException {
        VerifierAccumulateDescr accumulate = new VerifierAccumulateDescr(pattern);

        PatternDescrVisitor visitor = new PatternDescrVisitor(data,
                rule,
                solvers);
        Pattern parentPattern = visitor.visitPatternDescr(descr.getInputPattern(),
                null,
                0);

        accumulate.setInitCode(descr.getInitCode());
        accumulate.setActionCode(descr.getActionCode());
        accumulate.setReverseCode(descr.getReverseCode());
        accumulate.setResultCode(descr.getResultCode());

        // XXX: Array seems to be always null.
        // accumulate.setDeclarations(descr.getDeclarations());

        accumulate.setClassName(descr.getClassName());
        accumulate.setExternalFunction(descr.isExternalFunction());
        accumulate.setFunctionIdentifier(descr.getFunctions().get(0).getFunction());
        accumulate.setExpression(descr.getFunctions().get(0).getParams()[0]);
        accumulate.setParentPath(parentPattern.getPath());
        accumulate.setParentType(parentPattern.getVerifierComponentType());

        data.add(accumulate);

        return accumulate;
    }

    protected VerifierComponent visit(DeclarativeInvokerDescr descr) throws UnknownDescriptionException {

        DeclarativeInvokerDescrVisitor visitor = new DeclarativeInvokerDescrVisitor(data,
                rule);

        return visitor.visit(descr);
    }

    @Override
    protected void visitAndDescr(AndDescr descr) throws UnknownDescriptionException {
        PatternOperatorDescr operatorDescr = new PatternOperatorDescr(pattern,
                OperatorDescrType.AND);
        operatorDescr.setOrderNumber(orderNumber.next());
        operatorDescr.setParentPath(getParent().getPath());
        operatorDescr.setParentType(getParent().getVerifierComponentType());

        data.add(operatorDescr);

        solvers.startOperator(OperatorDescrType.AND);
        visit(descr.getDescrs());
        solvers.endOperator();
    }

    @Override
    protected void visitOrDescr(OrDescr descr) throws UnknownDescriptionException {
        PatternOperatorDescr operatorDescr = new PatternOperatorDescr(pattern,
                OperatorDescrType.OR);
        operatorDescr.setOrderNumber(orderNumber.next());

        data.add(operatorDescr);

        solvers.startOperator(OperatorDescrType.OR);
        visit(descr.getDescrs());
        solvers.endOperator();
    }

}
