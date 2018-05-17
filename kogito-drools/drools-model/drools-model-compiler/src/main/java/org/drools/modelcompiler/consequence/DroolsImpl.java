/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler.consequence;

import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.model.BitMask;
import org.drools.model.Drools;
import org.drools.model.DroolsEntryPoint;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;

import static java.util.Arrays.asList;

import static org.drools.core.reteoo.PropertySpecificUtil.calculatePositiveMask;
import static org.drools.modelcompiler.consequence.LambdaConsequence.adaptBitMask;

public class DroolsImpl implements Drools, org.kie.api.runtime.rule.RuleContext {
    private final KnowledgeHelper knowledgeHelper;
    private final WorkingMemory workingMemory;

    private final Map<Object, InternalFactHandle> fhLookup = new IdentityHashMap<>();

    DroolsImpl(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
        this.knowledgeHelper = knowledgeHelper;
    }

    @Override
    public void insert(Object object) {
        workingMemory.insert(object);
    }

    @Override
    public void insert(Object object, boolean dynamic) {
        workingMemory.insert(object, dynamic);
    }

    @Override
    public Rule getRule() {
        return knowledgeHelper.getRule();
    }

    @Override
    public Match getMatch() {
        return knowledgeHelper.getMatch();
    }

    @Override
    public FactHandle insertLogical( Object object ) {
        return knowledgeHelper.insertLogical(object);
    }

    @Override
    public FactHandle insertLogical( Object object, Object value ) {
        return knowledgeHelper.insertLogical(object, value);
    }

    @Override
    public void blockMatch( Match match ) {
        knowledgeHelper.blockMatch(match);
    }

    @Override
    public void unblockAllMatches( Match match ) {
        knowledgeHelper.unblockAllMatches(match);
    }

    @Override
    public void cancelMatch( Match match ) {
        knowledgeHelper.cancelMatch(match);
    }

    @Override
    public void update(Object object, String... modifiedProperties) {
        Class modifiedClass = object.getClass();
        TypeDeclaration typeDeclaration = workingMemory.getKnowledgeBase().getOrCreateExactTypeDeclaration( modifiedClass );
        org.drools.core.util.bitmask.BitMask mask = typeDeclaration.isPropertyReactive() ?
                calculatePositiveMask(modifiedClass, asList(modifiedProperties), typeDeclaration.getAccessibleProperties() ) :
                org.drools.core.util.bitmask.AllSetBitMask.get();

        knowledgeHelper.update( fhLookup.get(object), mask, modifiedClass);
    }

    @Override
    public void update(Object object, BitMask modifiedProperties ) {
        Class<?> modifiedClass = modifiedProperties.getPatternClass();
        knowledgeHelper.update( fhLookup.get(object), adaptBitMask(modifiedProperties), modifiedClass);
    }

    public void update(FactHandle handle, Object newObject) {
        knowledgeHelper.update( handle, newObject );
    }

    @Override
    public void delete(Object object) {
        workingMemory.delete( fhLookup.get(object) );
    }

    void registerFactHandle(InternalFactHandle fh) {
        fhLookup.put( fh.getObject(), fh );
    }

    @Override
    public <T> T getRuntime(Class<T> runtimeClass) {
        return (T)knowledgeHelper.getKieRuntime();
    }

    @Override
    public DroolsEntryPoint getEntryPoint( String name) {
        return new DroolsEntryPointImpl( knowledgeHelper.getEntryPoint( name ), fhLookup );
    }

    @Override
    public void halt() {
        knowledgeHelper.halt();
    }

    @Override
    public void setFocus( String focus ) {
        knowledgeHelper.setFocus( focus );
    }

    @Override
    public KieRuntime getKieRuntime() {
        return knowledgeHelper.getKieRuntime();
    }

    @Override
    public KieRuntime getKnowledgeRuntime() {
        return knowledgeHelper.getKieRuntime();
    }

    public KnowledgeHelper asKnowledgeHelper() {
        return knowledgeHelper;
    }
}
