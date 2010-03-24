package org.drools.verifier.visitor;

import java.util.Collections;
import java.util.List;
import java.util.jar.JarInputStream;

import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.server.rules.SuggestionCompletionLoader;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.Import;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.data.VerifierData;

/**
 * 
 * @author Toni Rikkola
 */
public class PackageDescrVisitor {

    private final VerifierData   data;
    private List<JarInputStream> jars = null;

    private RulePackage          rulePackage;

    public PackageDescrVisitor(VerifierData data,
                               List<JarInputStream> jars) {
        this.data = data;
        this.jars = jars;
    }

    public void visitPackageDescr(PackageDescr descr) throws UnknownDescriptionException {
        rulePackage = data.getPackageByName( descr.getName() );

        if ( rulePackage == null ) {
            rulePackage = new RulePackage();

            rulePackage.setName( descr.getName() );
            data.add( rulePackage );
        }

        visitImports( descr.getImports() );

        TypeDeclarationDescrVisitor typeDeclarationDescrVisitor = new TypeDeclarationDescrVisitor( data );
        typeDeclarationDescrVisitor.visit( descr.getTypeDeclarations() );

        visitRules( descr.getRules() );
    }

    @SuppressWarnings("unchecked")
    private void visitImports(List<ImportDescr> importDescrs) {
        StringBuilder header = new StringBuilder();
        for ( ImportDescr i : importDescrs ) {
            String fullPath = i.getTarget();
            String name = fullPath.substring( fullPath.lastIndexOf( "." ) + 1 );

            header.append( "import " );
            header.append( fullPath );
            header.append( "\n" );

            Import objectImport = new Import( rulePackage );
            objectImport.setName( fullPath );
            objectImport.setShortName( name );
            data.add( objectImport );

            ObjectType objectType = this.data.getObjectTypeByFullName( fullPath );

            if ( objectType == null ) {
                objectType = new ObjectType();
            }

            objectType.setName( name );
            objectType.setFullName( fullPath );
            data.add( objectType );
        }

        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine engine = loader.getSuggestionEngine( header.toString(),
                                                                        jars,
                                                                        Collections.EMPTY_LIST );
        for ( String factTypeName : engine.getFactTypes() ) {
            for ( String fieldName : engine.getFieldCompletions( factTypeName ) ) {
                ObjectType objectType = this.data.getObjectTypeByObjectTypeNameAndPackageName( factTypeName,
                                                                                               rulePackage.getName() );

                Field field = data.getFieldByObjectTypeAndFieldName( objectType.getFullName(),
                                                                     fieldName );
                if ( field == null ) {
                    field = ObjectTypeFactory.createField( fieldName,
                                                           objectType );
                    field.setFieldType( engine.getFieldType( objectType.getName(),
                                                             fieldName ) );
                    data.add( field );
                }
            }
        }
    }

    private void visitRules(List<RuleDescr> rules) throws UnknownDescriptionException {
        for ( RuleDescr ruleDescr : rules ) {
            visitRuleDescr( ruleDescr );
        }
    }

    private void visitRuleDescr(RuleDescr descr) throws UnknownDescriptionException {
        RuleDescrVisitor visitor = new RuleDescrVisitor( data,
                                                         rulePackage );
        visitor.visitRuleDescr( descr );
    }

}
