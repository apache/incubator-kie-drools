package org.drools.verifier.data;

import java.util.Collection;

import org.drools.verifier.components.Field;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.Variable;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.VerifierEntryPointDescr;
import org.drools.verifier.components.VerifierRule;

/**
 * 
 * @author Toni Rikkola
 */
public interface VerifierData {

    public void add(VerifierComponent object);

    public <T extends VerifierComponent> T getVerifierObject(VerifierComponentType type,
                                                             String guid);

    public <T extends VerifierComponent> Collection<T> getAll(VerifierComponentType type);

    public Collection<VerifierComponent> getAll();

    public Variable getVariableByRuleAndVariableName(String ruleName,
                                                     String base);

    public ObjectType getObjectTypeByName(String name);

    public Field getFieldByObjectTypeAndFieldName(String base,
                                                  String fieldName);

    public RulePackage getPackageByName(String name);

    public VerifierRule getRuleByName(String name);

    public Collection<VerifierRule> getRulesByFieldId(String guid);

    public Collection<VerifierRule> getRulesByObjectTypeId(String guid);

    public Collection<Restriction> getRestrictionsByFieldGuid(String guid);

    public Collection<ObjectType> getObjectTypesByRuleName(String ruleName);

    public VerifierEntryPointDescr getEntryPointByEntryId(String entryId);

    public Collection<VerifierRule> getRulesByCategoryName(String categoryName);
}
