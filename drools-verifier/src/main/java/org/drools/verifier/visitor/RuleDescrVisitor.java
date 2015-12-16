/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.drools.compiler.lang.descr.*;
import org.drools.verifier.components.*;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.solver.Solvers;

public class RuleDescrVisitor extends ConditionalElementDescrVisitor {

    private final RulePackage rulePackage;

    public RuleDescrVisitor(VerifierData data,
                            RulePackage rulePackage) {
        super(data,
                new Solvers());

        this.rulePackage = rulePackage;
    }

    public void visitRuleDescr(RuleDescr descr) throws UnknownDescriptionException {

        rule = new VerifierRule(descr, rulePackage,descr.getNamedConsequences());

        rule.setName(descr.getName());

        for (AttributeDescr attribute : descr.getAttributes().values()) {
            rule.getAttributes().put(attribute.getName(),
                    attribute.getValue());
        }

        Consequence consequence = visitConsequence(rule,
                descr.getConsequence());
        for (String name :descr.getNamedConsequences().keySet()){
           visitNamedConsequence(name, descr.getNamedConsequences().get(name));
        }

        // TODO: NEEDS TO BE FIXED
//        rule.getMetadata().putAll( descr.get );
        rule.setConsequencePath(consequence.getPath());
        rule.setConsequenceType(consequence.getConsequenceType());
        rule.setLineNumber(descr.getLine());

        data.add(rule);

        rulePackage.getRules().add(rule);

        solvers.startRuleSolver(rule);

        visitAndDescr(descr.getLhs());

        solvers.endRuleSolver();

        addSubItems();
    }

    @Override
    protected VerifierComponent getParent() {
        return rule;
    }

    public void visit(Object descr) throws UnknownDescriptionException {

        if (descr instanceof AndDescr) {
            visitAndDescr((AndDescr) descr);
        } else if (descr instanceof EvalDescr) {
            visitEvalDescr((EvalDescr) descr);
        } else if (descr instanceof ExistsDescr) {
            visitExistsDescr((ExistsDescr) descr);
        } else if (descr instanceof ForallDescr) {
            visitForallDescr((ForallDescr) descr);
        } else if (descr instanceof NotDescr) {
            visitNotDescr((NotDescr) descr);
        } else if (descr instanceof OrDescr) {
            visitOrDescr((OrDescr) descr);
        } else if (descr instanceof PatternDescr) {
            visitPattern((PatternDescr) descr);
        } else if (descr instanceof ConditionalBranchDescr) {
            visitPattern((ConditionalBranchDescr) descr);
        } else if (descr instanceof NamedConsequenceDescr) {
            visitPattern((NamedConsequenceDescr) descr);
        } else {
            throw new UnknownDescriptionException((BaseDescr) descr);
        }
    }

    private void visitNamedConsequence(String name, Object consequence) {
        if (consequence instanceof String) {
            data.add(new NamedConsequence(rule, name, (String) consequence));
        } else {
            //TODO: -Rikkola-
//            throw new UnknownDescriptionException();
        }
    }

    private void visitPattern(NamedConsequenceDescr descr) {
//                                              new NamedConsequence(descr.getName(),descr.get);
//        ;
        //TODO: -Rikkola-
    }

    private void visitPattern(ConditionalBranchDescr descr) {

        //TODO: -Rikkola-
    }

    private void visitNotDescr(NotDescr descr) throws UnknownDescriptionException {
        solvers.startNot();
        visit(descr.getDescrs());
        solvers.endNot();
    }

    private void visitForallDescr(ForallDescr descr) throws UnknownDescriptionException {
        solvers.startForall();
        visit(descr.getDescrs());
        solvers.endForall();
    }

    private void visitExistsDescr(ExistsDescr descr) throws UnknownDescriptionException {
        solvers.startExists();
        visit(descr.getDescrs());
        solvers.endExists();
    }

    /**
     * End leaf
     *
     * @param descr
     * @return
     */
    private RuleEval visitEvalDescr(EvalDescr descr) {

        RuleEval eval = new RuleEval(rule);
        eval.setContent(descr.getContent().toString());
        eval.setClassMethodName(descr.getClassMethodName());
        eval.setOrderNumber(orderNumber.next());
        eval.setParentPath(rule.getPath());
        eval.setParentType(rule.getVerifierComponentType());

        solvers.addRuleComponent(eval);

        data.add(eval);

        return eval;
    }

    /**
     * Creates verifier object from rule consequence. Currently only supports
     * text based consequences.
     *
     * @param o Consequence object.
     * @return Verifier object that implements the Consequence interface.
     */
    private Consequence visitConsequence(VerifierComponent parent,
                                         Object o) {

        TextConsequence consequence = new TextConsequence(rule);

        String text = o.toString();

        /*
         * Strip all comments out of the code.
         */
        StringBuffer buffer = new StringBuffer(text);
        int commentIndex = buffer.indexOf("//");

        while (commentIndex != -1) {
            buffer = buffer.delete(commentIndex,
                    buffer.indexOf("\n",
                            commentIndex));
            commentIndex = buffer.indexOf("//");
        }

        text = buffer.toString();

        /*
         * Strip all useless characters out of the code.
         */
        text = text.replaceAll("\n",
                "");
        text = text.replaceAll("\r",
                "");
        text = text.replaceAll("\t",
                "");
        text = text.replaceAll(" ",
                "");

        consequence.setText(text);
        consequence.setParentPath(parent.getPath());
        consequence.setParentType(parent.getVerifierComponentType());

        data.add(consequence);

        return consequence;
    }

    private void visitPattern(PatternDescr descr) throws UnknownDescriptionException {
        PatternDescrVisitor visitor = new PatternDescrVisitor(data,
                rule,
                solvers);
        visitor.visit(descr,
                orderNumber.next());
    }

    private void addSubItems() {
        for (SubPattern subPattern : solvers.getPatternPossibilities()) {
            data.add(subPattern);
        }

        for (SubRule subRule : solvers.getRulePossibilities()) {
            data.add(subRule);
        }
    }

    @Override
    protected void visitAndDescr(AndDescr descr) throws UnknownDescriptionException {
        RuleOperatorDescr operatorDescr = new RuleOperatorDescr(descr, rule,
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
        RuleOperatorDescr operatorDescr = new RuleOperatorDescr(descr, rule,
                OperatorDescrType.OR);
        operatorDescr.setOrderNumber(orderNumber.next());

        data.add(operatorDescr);

        solvers.startOperator(OperatorDescrType.OR);
        visit(descr.getDescrs());
        solvers.endOperator();
    }

}
