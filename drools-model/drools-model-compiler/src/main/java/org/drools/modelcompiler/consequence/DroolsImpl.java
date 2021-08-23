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

import org.drools.core.WorkingMemory;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.model.BitMask;
import org.drools.model.Channel;
import org.drools.model.Drools;
import org.drools.model.DroolsEntryPoint;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;

import static java.util.Arrays.asList;

import static org.drools.core.reteoo.PropertySpecificUtil.calculatePositiveMask;
import static org.drools.modelcompiler.util.EvaluationUtil.adaptBitMask;

public class DroolsImpl implements Drools, org.kie.api.runtime.rule.RuleContext {
    private final KnowledgeHelper knowledgeHelper;
    private final WorkingMemory workingMemory;

    private final FactHandleLookup fhLookup;

    DroolsImpl(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory, FactHandleLookup fhLookup) {
        this.workingMemory = workingMemory;
        this.knowledgeHelper = knowledgeHelper;
        this.fhLookup = fhLookup;
    }

    @Override
    public void insert(Object object) {
        insert( object, false );
    }

    @Override
    public void insert(Object object, boolean dynamic) {
        TerminalNode terminalNode = (( AgendaItem )getMatch()).getTerminalNode();
        ((InternalWorkingMemoryActions)workingMemory).insert(object, dynamic, getRule(), terminalNode);
    }

    @Override
    public void logicalInsert(Object object) {
        knowledgeHelper.insertLogical( object );
    }

    @Override
    public RuleImpl getRule() {
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
        org.drools.core.util.bitmask.BitMask mask = org.drools.core.util.bitmask.AllSetBitMask.get();

        if (modifiedProperties.length > 0) {
            TypeDeclaration typeDeclaration = workingMemory.getKnowledgeBase().getOrCreateExactTypeDeclaration( modifiedClass );
            if (typeDeclaration.isPropertyReactive()) {
                mask = calculatePositiveMask( modifiedClass, asList( modifiedProperties ), typeDeclaration.getAccessibleProperties() );
            }
        }

        knowledgeHelper.update( getFactHandleForObject( object ), mask, modifiedClass);
    }

    private InternalFactHandle getFactHandleForObject( Object object ) {
        InternalFactHandle fh = fhLookup.get(object);
        return fh != null ? fh : (InternalFactHandle) workingMemory.getFactHandle( object );
    }

    @Override
    public void update(Object object, BitMask modifiedProperties ) {
        Class<?> modifiedClass = modifiedProperties.getPatternClass();
        knowledgeHelper.update( getFactHandleForObject( object ), adaptBitMask(modifiedProperties), modifiedClass);
    }

    public void update(FactHandle handle, Object newObject) {
        knowledgeHelper.update( handle, newObject );
    }

    @Override
    public void delete(Object object) {
        knowledgeHelper.delete( getFactHandleForObject( object ) );
    }

    @Override
    public <T> T getRuntime(Class<T> runtimeClass) {
        return (T)knowledgeHelper.getKieRuntime();
    }

    @Override
    public <T> T getContext(Class<T> contextClass) {
        return (T)knowledgeHelper.getContext(contextClass);
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

    @Override
    public Channel getChannel(String name) {
        return new ChannelImpl(knowledgeHelper.getChannel(name));
    }
}
