package org.drools.verifier.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.net.ssl.KeyStoreBuilderParameters;

import org.drools.ClassObjectFilter;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.FieldObjectTypeLink;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.Pattern;
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

    public ObjectType getObjectTypeByName(String name) {
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

    public FieldObjectTypeLink getFieldObjectTypeLink(int id,
                                                      int id2) {
        return null;
    }

    public Collection<VerifierComponent> getAll() {
        Collection<Object> list = kSession.getObjects();

        return new ArrayList( list );
    }

    public Collection<Field> getFieldsByObjectTypeId(String id) {
        return null;
    }

    public Collection<VerifierRule> getRulesByObjectTypeId(String id) {
        return null;
    }

    public Collection<VerifierRule> getRulesByFieldId(String id) {
        return null;
    }

    public RulePackage getPackageByName(String name) {
        return null;
    }

    public Collection<Restriction> getRestrictionsByFieldGuid(String id) {
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
    //                                                             String guid) {
    public VerifierComponent getVerifierObject(VerifierComponentType type,
                                               String guid) {
        return null;
    }

    public VerifierEntryPointDescr getEntryPointByEntryId(String entryId) {
        // TODO Auto-generated method stub
        return null;
    }

}
