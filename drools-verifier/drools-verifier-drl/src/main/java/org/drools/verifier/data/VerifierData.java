package org.drools.verifier.data;

import java.util.Collection;

import org.drools.verifier.components.*;

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
