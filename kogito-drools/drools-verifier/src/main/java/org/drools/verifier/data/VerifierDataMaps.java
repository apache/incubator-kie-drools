package org.drools.verifier.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

/**
 * 
 * @author Toni Rikkola
 */
class VerifierDataMaps
    implements
    VerifierData {

    private Map<VerifierComponentType, Map<String, VerifierComponent>> all                            = new TreeMap<VerifierComponentType, Map<String, VerifierComponent>>();

    private Map<String, RulePackage>                                   packagesByName                 = new TreeMap<String, RulePackage>();
    private Map<String, ObjectType>                                    objectTypesByName              = new TreeMap<String, ObjectType>();
    private Map<String, Field>                                         fieldsByObjectTypeAndFieldName = new TreeMap<String, Field>();
    private Multimap<String, Field>                                    fieldsByObjectTypeId           = new TreeMultimap<String, Field>();
    private Map<String, FieldObjectTypeLink>                           fieldObjectTypeLinkByPath      = new TreeMap<String, FieldObjectTypeLink>();
    private Multimap<String, Pattern>                                  patternsByObjectTypeId         = new TreeMultimap<String, Pattern>();
    private Multimap<String, Pattern>                                  patternsByRuleName             = new TreeMultimap<String, Pattern>();
    private Multimap<String, Restriction>                              restrictionsByFieldId          = new TreeMultimap<String, Restriction>();
    private Map<String, Variable>                                      variablesByRuleAndVariableName = new TreeMap<String, Variable>();
    private Map<String, VerifierEntryPointDescr>                       entryPointsByEntryId           = new TreeMap<String, VerifierEntryPointDescr>();
    private Map<String, VerifierRule>                                  rulesByName                    = new TreeMap<String, VerifierRule>();
    private Multimap<String, VerifierRule>                             rulesByCategory                = new TreeMultimap<String, VerifierRule>();

    public Collection<ObjectType> getObjectTypesByRuleName(String ruleName) {
        Set<ObjectType> set = new HashSet<ObjectType>();

        for ( Pattern pattern : patternsByRuleName.get( ruleName ) ) {
            ObjectType objectType = (ObjectType) getVerifierObject( VerifierComponentType.OBJECT_TYPE,
                                                                    pattern.getObjectTypeGuid() );
            set.add( objectType );
        }

        return set;
    }

    public ObjectType getObjectTypeByName(String name) {
        return objectTypesByName.get( name );
    }

    public Field getFieldByObjectTypeAndFieldName(String objectTypeName,
                                                  String fieldName) {
        return fieldsByObjectTypeAndFieldName.get( objectTypeName + "." + fieldName );
    }

    public Variable getVariableByRuleAndVariableName(String ruleName,
                                                     String variableName) {
        return variablesByRuleAndVariableName.get( ruleName + "." + variableName );
    }

    public FieldObjectTypeLink getFieldObjectTypeLink(int id,
                                                      int id2) {
        return fieldObjectTypeLinkByPath.get( id + "." + id2 );
    }

    public Collection<VerifierComponent> getAll() {
        List<VerifierComponent> objects = new ArrayList<VerifierComponent>();

        for ( VerifierComponentType type : all.keySet() ) {
            objects.addAll( all.get( type ).values() );
        }

        return objects;
    }

    public Collection<Field> getFieldsByObjectTypeId(String id) {
        return fieldsByObjectTypeId.get( id );
    }

    public Collection<VerifierRule> getRulesByObjectTypeId(String id) {
        Set<VerifierRule> rules = new HashSet<VerifierRule>();

        for ( Pattern pattern : patternsByObjectTypeId.get( id ) ) {

            rules.add( (VerifierRule) getVerifierObject( VerifierComponentType.RULE,
                                                         pattern.getRuleGuid() ) );
        }

        return rules;
    }

    public Collection<VerifierRule> getRulesByFieldId(String id) {

        Set<VerifierRule> rules = new HashSet<VerifierRule>();

        for ( Restriction restriction : restrictionsByFieldId.get( id ) ) {

            rules.add( (VerifierRule) getVerifierObject( VerifierComponentType.RULE,
                                                         restriction.getRuleGuid() ) );
        }

        return rules;
    }

    public RulePackage getPackageByName(String name) {
        return packagesByName.get( name );
    }

    public Collection<Restriction> getRestrictionsByFieldGuid(String id) {
        return restrictionsByFieldId.get( id );
    }

    public void add(VerifierComponent object) {
        if ( VerifierComponentType.FIELD.equals( object.getVerifierComponentType() ) ) {
            Field field = (Field) object;
            ObjectType objectType = (ObjectType) getVerifierObject( VerifierComponentType.OBJECT_TYPE,
                                                                    field.getObjectTypeGuid() );
            fieldsByObjectTypeAndFieldName.put( objectType.getName() + "." + field.getName(),
                                                field );

            fieldsByObjectTypeId.put( field.getObjectTypeGuid(),
                                      field );
        } else if ( VerifierComponentType.RULE.equals( object.getVerifierComponentType() ) ) {
            VerifierRule rule = (VerifierRule) object;
            rulesByName.put( rule.getRuleName(),
                             rule );
            if ( rule.getMetadata().containsKey( "category" ) ) {
                rulesByCategory.put( rule.getMetadata().get( "category" ),
                                     rule );
            }
        } else if ( VerifierComponentType.VARIABLE.equals( object.getVerifierComponentType() ) ) {
            Variable variable = (Variable) object;
            variablesByRuleAndVariableName.put( variable.getRuleName() + "." + variable.getName(),
                                                variable );
        } else if ( VerifierComponentType.PATTERN.equals( object.getVerifierComponentType() ) ) {
            Pattern pattern = (Pattern) object;

            patternsByObjectTypeId.put( pattern.getObjectTypeGuid(),
                                        pattern );
            patternsByRuleName.put( pattern.getRuleName(),
                                    pattern );
        } else if ( VerifierComponentType.RESTRICTION.equals( object.getVerifierComponentType() ) ) {
            Restriction restriction = (Restriction) object;

            restrictionsByFieldId.put( restriction.getFieldGuid(),
                                       restriction );
        } else if ( VerifierComponentType.FIELD_OBJECT_TYPE_LINK.equals( object.getVerifierComponentType() ) ) {
            FieldObjectTypeLink link = (FieldObjectTypeLink) object;
            fieldObjectTypeLinkByPath.put( link.getFieldId() + "." + link.getObjectTypeId(),
                                           link );
        } else if ( VerifierComponentType.RULE_PACKAGE.equals( object.getVerifierComponentType() ) ) {
            RulePackage rulePackage = (RulePackage) object;

            packagesByName.put( rulePackage.getName(),
                                rulePackage );
        } else if ( VerifierComponentType.OBJECT_TYPE.equals( object.getVerifierComponentType() ) ) {
            ObjectType objectType = (ObjectType) object;
            objectTypesByName.put( objectType.getName(),
                                   objectType );
        } else if ( VerifierComponentType.ENTRY_POINT_DESCR.equals( object.getVerifierComponentType() ) ) {
            VerifierEntryPointDescr entryPoint = (VerifierEntryPointDescr) object;
            entryPointsByEntryId.put( entryPoint.getEntryId(),
                                      entryPoint );
        }

        Map<String, VerifierComponent> map = all.get( object.getVerifierComponentType() );

        if ( map == null ) {
            map = new TreeMap<String, VerifierComponent>();
            all.put( object.getVerifierComponentType(),
                     map );
        }

        map.put( object.getGuid(),
                 object );

    }

    public Collection<VerifierRule> getRulesByCategoryName(String categoryName) {
        return rulesByCategory.get( categoryName );
    }

    //    public <T extends VerifierComponent> Collection<T> getAll(VerifierComponentType type) {
    public Collection< ? extends VerifierComponent> getAll(VerifierComponentType type) {
        return all.get( type ).values();
    }

    //    public <T extends VerifierComponent> T getVerifierObject(VerifierComponentType type,
    //                                                             String guid) {
    public VerifierComponent getVerifierObject(VerifierComponentType type,
                                               String guid) {
        return all.get( type ).get( guid );
    }

    public VerifierEntryPointDescr getEntryPointByEntryId(String entryId) {
        return entryPointsByEntryId.get( entryId );
    }

    public VerifierRule getRuleByName(String name) {
        return rulesByName.get( name );
    }

}
