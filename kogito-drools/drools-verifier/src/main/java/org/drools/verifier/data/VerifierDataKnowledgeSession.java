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

package org.drools.verifier.data;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.ClassObjectFilter;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.definition.KnowledgePackage;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.Import;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.Variable;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.EntryPoint;
import org.drools.verifier.components.VerifierRule;

/**
 * 
 * @author Toni Rikkola
 */
class VerifierDataKnowledgeSession
    implements
    VerifierData {

    private final KnowledgeBuilder         kbuilder;
    private final StatefulKnowledgeSession kSession;

    public VerifierDataKnowledgeSession() {
        this.kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        // TODO: Add queries

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( "Unable to compile queries." );
        }

        Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( pkgs );

        this.kSession = kbase.newStatefulKnowledgeSession();
    }

    public Collection<ObjectType> getObjectTypesByRuleName(String ruleName) {
        Collection<Object> list = kSession.getObjects( new ClassObjectFilter( Integer.class ) );
        return new ArrayList( list );
    }

    public ObjectType getObjectTypeByFullName(String name) {
        return null;
    }

    public Field getFieldByObjectTypeAndFieldName(String objectTypeName,
                                                  String fieldName) {
        return null;
    }

    public Variable getVariableByRuleAndVariableName(String ruleName,
                                                     String variableName) {
        return null;
    }

    public Collection<VerifierComponent> getAll() {
        Collection<Object> list = kSession.getObjects();

        return new ArrayList( list );
    }

    public Collection<Field> getFieldsByObjectTypeId(String id) {
        return null;
    }

    public Collection<VerifierRule> getRulesByObjectTypePath(String id) {
        return null;
    }

    public Collection<VerifierRule> getRulesByFieldPath(String id) {
        return null;
    }

    public RulePackage getPackageByName(String name) {
        return null;
    }

    public Collection<Restriction> getRestrictionsByFieldPath(String id) {
        return null;
    }

    public void add(VerifierComponent object) {
        kSession.insert( object );
    }

    //    public <T extends VerifierComponent> Collection<T> getAll(VerifierComponentType type) {
    public Collection< ? extends VerifierComponent> getAll(VerifierComponentType type) {
        return null;
    }

    //    public <T extends VerifierComponent> T getVerifierObject(VerifierComponentType type,
    //                                                             String path) {
    public VerifierComponent getVerifierObject(VerifierComponentType type,
                                               String path) {
        return null;
    }

    public EntryPoint getEntryPointByEntryId(String entryId) {
        // TODO Auto-generated method stub
        return null;
    }

    public VerifierRule getRuleByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<VerifierRule> getRulesByCategoryName(String categoryName) {
        // TODO Auto-generated method stub
        return null;
    }

    public Import getImportByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public ObjectType getObjectTypeByObjectTypeNameAndPackageName(String factTypeName,
                                                                  String ru) {
        // TODO Auto-generated method stub
        return null;
    }

}
