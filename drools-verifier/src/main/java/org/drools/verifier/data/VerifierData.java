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

import java.util.Collection;

import org.drools.verifier.components.Field;
import org.drools.verifier.components.Import;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.Variable;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.EntryPoint;
import org.drools.verifier.components.VerifierRule;

public interface VerifierData {

    public void add(VerifierComponent object);

    public <T extends VerifierComponent> T getVerifierObject(VerifierComponentType type,
                                                             String path);

    public <T extends VerifierComponent> Collection<T> getAll(VerifierComponentType type);

    public Collection<VerifierComponent> getAll();

    public Variable getVariableByRuleAndVariableName(String ruleName,
                                                     String base);

    public ObjectType getObjectTypeByFullName(String name);

    public Field getFieldByObjectTypeAndFieldName(String base,
                                                  String fieldName);

    public RulePackage getPackageByName(String name);

    public VerifierRule getRuleByName(String name);

    public Collection<VerifierRule> getRulesByFieldPath(String path);

    public Collection<VerifierRule> getRulesByObjectTypePath(String path);

    public Collection<Restriction> getRestrictionsByFieldPath(String path);

    public Collection<ObjectType> getObjectTypesByRuleName(String ruleName);

    public EntryPoint getEntryPointByEntryId(String entryId);

    public Collection<VerifierRule> getRulesByCategoryName(String categoryName);

    public ObjectType getObjectTypeByObjectTypeNameAndPackageName(String factTypeName,
                                                                  String packageName);

    public Import getImportByName(String name);
}
