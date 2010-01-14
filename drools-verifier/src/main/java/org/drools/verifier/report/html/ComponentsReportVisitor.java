package org.drools.verifier.report.html;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.verifier.components.Field;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.RangeCheckCause;
import org.mvel2.templates.TemplateRuntime;

class ComponentsReportVisitor extends ReportVisitor {

    public static String getCss(String fileName) {
        return readFile( fileName );
    }

    public static String visitRulePackageCollection(String sourceFolder,
                                                    Collection<RulePackage> packages) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "sourceFolder",
                 sourceFolder );
        map.put( "ruleFolder",
                 UrlFactory.RULE_FOLDER );

        map.put( "rulePackages",
                 packages );

        String myTemplate = readFile( "packages.htm" );

        String result = String.valueOf( TemplateRuntime.eval( myTemplate,
                                                              map ) );

        return result;
    }

    public static String visitObjectTypeCollection(String sourceFolder,
                                                   Collection<ObjectType> objectTypes) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "sourceFolder",
                 sourceFolder );
        map.put( "objectTypeFolder",
                 sourceFolder + "/" + UrlFactory.OBJECT_TYPE_FOLDER );
        map.put( "fieldFolder",
                 UrlFactory.FIELD_FOLDER );
        map.put( "objectTypes",
                 objectTypes );

        String myTemplate = readFile( "objectTypes.htm" );

        return String.valueOf( TemplateRuntime.eval( myTemplate,
                                                     map ) );
    }

    public static String visitRule(String sourceFolder,
                                   VerifierRule rule,
                                   VerifierData data) {
        Collection<ObjectType> objectTypes = data.getObjectTypesByRuleName( rule.getRuleName() );

        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "sourceFolder",
                 sourceFolder );
        map.put( "objectTypeFolder",
                 UrlFactory.OBJECT_TYPE_FOLDER );

        map.put( "rule",
                 rule );
        map.put( "objectTypes",
                 objectTypes );

        String myTemplate = readFile( "rule.htm" );

        return String.valueOf( TemplateRuntime.eval( myTemplate,
                                                     map ) );
    }

    public static String visitObjectType(String sourceFolder,
                                         ObjectType objectType,
                                         VerifierData data) {
        Collection<VerifierRule> rules = data.getRulesByObjectTypeId( objectType.getGuid() );

        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "sourceFolder",
                 sourceFolder );
        map.put( "ruleFolder",
                 UrlFactory.RULE_FOLDER );
        map.put( "fieldFolder",
                 UrlFactory.FIELD_FOLDER );

        map.put( "objectType",
                 objectType );
        map.put( "rules",
                 rules );

        String myTemplate = readFile( "objectType.htm" );

        return String.valueOf( TemplateRuntime.eval( myTemplate,
                                                     map ) );
    }

    public static String visitField(String sourceFolder,
                                    Field field,
                                    VerifierReport result) {
        VerifierData data = result.getVerifierData();
        ObjectType objectType = data.getVerifierObject( VerifierComponentType.OBJECT_TYPE,
                                                        field.getObjectTypeGuid() );
        Collection<VerifierRule> rules = data.getRulesByFieldId( field.getGuid() );

        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "sourceFolder",
                 sourceFolder );
        map.put( "ruleFolder",
                 UrlFactory.RULE_FOLDER );
        map.put( "objectTypeFolder",
                 UrlFactory.OBJECT_TYPE_FOLDER );
        map.put( "fieldFolder",
                 UrlFactory.FIELD_FOLDER );

        map.put( "field",
                 field );
        map.put( "objectType",
                 objectType );
        map.put( "rules",
                 rules );

        if ( field.getFieldType() == Field.DOUBLE || field.getFieldType() == Field.DATE || field.getFieldType() == Field.INT ) {
            Collection<RangeCheckCause> causes = result.getRangeCheckCausesByFieldId( field.getGuid() );
            Collection<Restriction> restrictions = data.getRestrictionsByFieldGuid( field.getGuid() );
            map.put( "ranges",
                     "Ranges:" + MissingRangesReportVisitor.visitRanges( UrlFactory.PREVIOUS_FOLDER,
                                                                         restrictions,
                                                                         causes ) );
        } else {
            map.put( "ranges",
                     "" );
        }

        String myTemplate = readFile( "field.htm" );

        return String.valueOf( TemplateRuntime.eval( myTemplate,
                                                     map ) );
    }
}
