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

package org.drools.agent.impl;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import org.drools.SystemEventListener;
import org.drools.SystemEventListenerFactory;
import org.drools.WorkingMemory;
import org.drools.core.util.ReflectiveVisitor;
import org.drools.definition.KnowledgeDefinition;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.agent.ResourceDiffProducer;
import org.drools.rule.Function;
import org.drools.rule.Query;
import org.drools.rule.Rule;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public class BinaryResourceDiffProducerImpl extends ReflectiveVisitor implements ResourceDiffProducer {



    private KnowledgePackageImp newPkg;
    private KnowledgePackageImp currentPkg;
    private Set<KnowledgeDefinition> unmodifiedDefinitions = new HashSet<KnowledgeDefinition>();
    private Set<KnowledgeDefinition> removedDefinitions = new HashSet<KnowledgeDefinition>();

    private SystemEventListener listener;

    //attributes used during rules comparison
    private Calendar now = new GregorianCalendar();
    private Consequence dummyConsequence = new DummyConsequence();

    public ResourceDiffResult diff(Set<KnowledgeDefinition> originalDefinitions, KnowledgePackageImp newPkg, KnowledgePackageImp currentPkg ) {

        this.listener = SystemEventListenerFactory.getSystemEventListener();

        this.newPkg = newPkg;
        this.currentPkg = currentPkg;

        for (KnowledgeDefinition knowledgeDefinition : originalDefinitions) {
            this.visit(knowledgeDefinition);
        }



        //return the whole new package as new
        return new ResourceDiffResult(this.newPkg, this.unmodifiedDefinitions, this.removedDefinitions);
    }

    
    public void visitRule(final Rule oldRule){

       

        //ok, so I get an old rule: is it modified in the new pkg? is it even present on it?
        org.drools.definition.rule.Rule newRule = newPkg.getRule(oldRule.getName());

        if (newRule == null){
            //the old rule is not present on the new package. Add it to
            //removed rules list.
            listener.debug("BinaryResourceDiffProducerImpl: "+oldRule+" is not present anymore. Adding to removed list.");
            this.removedDefinitions.add(oldRule);
            return;
        }

        //it is possible that the old rule doesn't exist anymore in the current
        //pkg. This is because maybe some other resouce updated its definition
        //and after that the same resource removed it. If that is the case,
        //this resource (the one we are processing) still contain a reference
        //to the rule, but it is no present on kbase anymore. If this is the
        //case, we wan't to skip this rule. Because remember that someone
        //modified it and removed it before this resource. We don't even
        //add it to removedDefinitions, because it is no present on kbase. What
        //we have to do is remove it from the new pkg so it won't reapears.
        if (currentPkg.getRule(oldRule.getName()) == null){
            listener.debug("BinaryResourceDiffProducerImpl: "+oldRule+" is not present on current PKG. Removing from new package.");
            newPkg.removeRule(oldRule);
            return;
        }

        //I hate to do this. But if it is not instance of
        //org.drools.rule.Rule I can't get his LHS.
        if (!(newRule instanceof org.drools.rule.Rule)){
            listener.warning("BinaryResourceDiffProducerImpl: Rules must be subclasses of org.drools.rule.Rule.");
            return;
        }

        //Queries are not supported yet.
        if (newRule instanceof Query){
            listener.debug("BinaryResourceDiffProducerImpl: Query diff is not supported yet.");
            return;
        }

        //is newRule equal to oldRule?
        if (this.compareRules((Rule)newRule,oldRule)){
            //if so, we don't need the rule in the new Package.
            listener.debug("BinaryResourceDiffProducerImpl: "+oldRule+" didn't change. Removing from diff package and adding it to unmodified list.");
            newPkg.removeRule((org.drools.rule.Rule)newRule);
            this.unmodifiedDefinitions.add(oldRule);
        }

    }

    public void visitFunction(final Function oldFunction){
        //ok, so I get an old function: is it modified in the new pkg? is it even present on it?
        Function newFunction = newPkg.getFunction(oldFunction.getName());

        if (newFunction == null){
            //the old function is not present on the new package. Add it to
            //removed rules list.
            listener.debug("BinaryResourceDiffProducerImpl: "+oldFunction+" is not present anymore. Adding to removed list.");
            this.removedDefinitions.add(oldFunction);
            return;
        }

        //it is possible that the old function doesn't exist anymore in the current
        //pkg. This is because maybe some other resouce updated its definition
        //and after that the same resource removed it. If that is the case,
        //this resource (the one we are processing) still contain a reference
        //to the function, but it is no present on kbase anymore. If this is the
        //case, we wan't to skip this function. Remember that someone
        //modified it and removed it before this resource. We don't even
        //add it to removedDefinitions, because it is no present on kbase. What
        //we have to do is remove it from the new pkg so it won't reapears.
        if (currentPkg.getFunction(oldFunction.getName()) == null){
            listener.debug("BinaryResourceDiffProducerImpl: "+oldFunction+" is not present on current PKG. Removing from new package.");
            newPkg.removeFunction(oldFunction.getName());
            return;
        }

        //is newFunction equal to oldFunction?
        if (newFunction.equals(oldFunction)){
            //if so, we don't need the function in the new Package.
            listener.debug("BinaryResourceDiffProducerImpl: "+oldFunction+" didn't change. Removing from diff package and adding it to unmodified list.");
            newPkg.removeFunction(newFunction.getName());
            this.unmodifiedDefinitions.add(oldFunction);
        }else{
            //it seems that the kbase doesn't overrides function's definitions.
            //that's why we need to mark this function as removed, but don't
            //remove it from the new pkg.
            listener.debug("BinaryResourceDiffProducerImpl: "+oldFunction+" did change. Marking as removed so it new version could be added later.");
            this.removedDefinitions.add(oldFunction);
        }


    }


    public void visitKnowledgeDefinition(final KnowledgeDefinition oldDefinition){
        listener.debug("BinaryResourceDiffProducerImpl: Couldn't handle "+oldDefinition+". We must leave it in the new Package.");
    }



    private boolean compareRules(Rule r1, Rule r2){
        listener.debug("BinaryResourceDiffProducerImpl: Comparing "+r1+" against "+r2);
        
        

        //compares the salinces
        String v1 = r1.getSalience()== null?"":r1.getSalience().toString();
        String v2 = r2.getSalience()== null?"":r2.getSalience().toString();
        
        if (!v1.equals(v2)){
            listener.debug("BinaryResourceDiffProducerImpl: The rules have different saliences: r1= "+v1+", r2= "+v2);
            return false;
        }

        //compares the activation groups
        v1 = r1.getActivationGroup()== null?"":r1.getActivationGroup();
        v2 = r2.getActivationGroup()== null?"":r2.getActivationGroup();
        if (!v1.equals(v2)){
            listener.debug("BinaryResourceDiffProducerImpl: The rules have different activation groups: r1= "+v1+", r2= "+v2);
            return false;
        }

        //compares no-loop attribute
        if (r1.isNoLoop() != r2.isNoLoop()){
            listener.debug("BinaryResourceDiffProducerImpl: The rules have different values for no-loop attribure: r1= "+r1.isNoLoop()+", r2= "+r2.isNoLoop());
            return false;
        }

        //compares lock-on-active attribute
        if (r1.isLockOnActive() != r2.isLockOnActive()){
            listener.debug("BinaryResourceDiffProducerImpl: The rules have different values for lock-on-active attribure: r1= "+r1.isLockOnActive()+", r2= "+r2.isLockOnActive());
            return false;
        }

        //compares agenda-group attribute
        v1 = r1.getAgendaGroup()== null?"":r1.getAgendaGroup();
        v2 = r2.getAgendaGroup()== null?"":r2.getAgendaGroup();
        if (!v1.equals(v2)){
            listener.debug("BinaryResourceDiffProducerImpl: The rules have different agenda groups: r1= "+v1+", r2= "+v2);
            return false;
        }

        //compares auto-focus attribute
        if (r1.getAutoFocus() != r2.getAutoFocus()){
            listener.debug("BinaryResourceDiffProducerImpl: The rules have different values for auto-focus attribure: r1= "+r1.getAutoFocus()+", r2= "+r2.getAutoFocus());
            return false;
        }

        //compares ruleflow-group attribute
        v1 = r1.getRuleFlowGroup()== null?"":r1.getRuleFlowGroup();
        v2 = r2.getRuleFlowGroup()== null?"":r2.getRuleFlowGroup();
        if (!v1.equals(v2)){
            listener.debug("BinaryResourceDiffProducerImpl: The rules have different ruleflow-group attribute: r1= "+v1+", r2= "+v2);
            return false;
        }

        //compares dialect attribute
        v1 = r1.getDialect()== null?"":r1.getDialect();
        v2 = r2.getDialect()== null?"":r2.getDialect();
        if (!v1.equals(v2)){
            listener.debug("BinaryResourceDiffProducerImpl: The rules have different dialect attribute: r1= "+v1+", r2= "+v2);
            return false;
        }

        //compares date-effective attribute
        Calendar c1 = r1.getDateEffective()== null?now:r1.getDateEffective();
        Calendar c2 = r2.getDateEffective()== null?now:r2.getDateEffective();
        if (!c1.equals(c2)){
            listener.debug("BinaryResourceDiffProducerImpl: The rules have different date-effective attribute: r1= "+c1+", r2= "+c2);
            return false;
        }

        //compares date-expires attribute
        c1 = r1.getDateExpires()== null?now:r1.getDateExpires();
        c2 = r2.getDateExpires()== null?now:r2.getDateExpires();
        if (!c1.equals(c2)){
            listener.debug("BinaryResourceDiffProducerImpl: The rules have different date-expires attribute: r1= "+c1+", r2= "+c2);
            return false;
        }

        //compares the rules' LHS
        if (!r1.getLhs().equals(r2.getLhs())){
            listener.debug("BinaryResourceDiffProducerImpl: The rules have different LHS");
            return false;
        }

        //compares the rules consequences
        Consequence consequence1 = r1.getConsequence()== null?dummyConsequence:r1.getConsequence();
        Consequence consequence2 = r2.getConsequence()== null?dummyConsequence:r2.getConsequence();
        if (!consequence1.equals(consequence2)){
            listener.debug("BinaryResourceDiffProducerImpl: The rules have different Consequences: r1= "+consequence1+", r2= "+consequence2);
            return false;
        }

        return true;
    }

    //Dummy implementation of Consequnce used for rules comparison.
    private class DummyConsequence implements Consequence{

        public void evaluate(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory) throws Exception {
            throw new UnsupportedOperationException("You should never call this method!!");
        }

        public String getName() {
            return "default";
        }        
    }

}
