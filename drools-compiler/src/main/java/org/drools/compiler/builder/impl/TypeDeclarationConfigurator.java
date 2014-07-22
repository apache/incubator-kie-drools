package org.drools.compiler.builder.impl;

import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.compiler.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.core.base.ClassFieldAccessor;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.evaluators.TimeIntervalParser;
import org.drools.core.base.mvel.MVELCompileable;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.util.ClassUtils;
import org.kie.internal.builder.conf.PropertySpecificOption;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;

public class TypeDeclarationConfigurator {

    protected TimeIntervalParser timeParser;
    protected KnowledgeBuilderImpl kbuilder;

    public TypeDeclarationConfigurator( KnowledgeBuilderImpl kbuilder ) {
        this.kbuilder = kbuilder;
    }



    public void finalize( TypeDeclaration type, AbstractClassTypeDeclarationDescr typeDescr, PackageRegistry pkgRegistry, Map<String, PackageRegistry> pkgRegistryMap, ClassHierarchyManager hierarchyManager ) {
        // prefer definitions where possible
        if ( type.getNature() == TypeDeclaration.Nature.DEFINITION ) {
            hierarchyManager.addDeclarationToPackagePreservingOrder( type, typeDescr, pkgRegistry.getPackage(), pkgRegistryMap );
        } else {
            TypeDeclaration oldType = pkgRegistry.getPackage().getTypeDeclaration( type.getTypeName() );
            if ( oldType == null ) {
                pkgRegistry.getPackage().addTypeDeclaration( type );
            } else {
                if (type.getRole() == TypeDeclaration.Role.EVENT) {
                    oldType.setRole(TypeDeclaration.Role.EVENT);
                    if ( type.getDurationAttribute() != null ) {
                        oldType.setDurationAttribute( type.getDurationAttribute() );
                        oldType.setDurationExtractor( type.getDurationExtractor() );
                    }
                    if ( type.getTimestampAttribute() != null ) {
                        oldType.setTimestampAttribute( type.getTimestampAttribute() );
                        oldType.setTimestampExtractor( type.getTimestampExtractor() );
                    }
                    if ( type.getExpirationOffset() >= 0 ) {
                        oldType.setExpirationOffset( type.getExpirationOffset() );
                    }
                }
                if (type.isPropertyReactive()) {
                    oldType.setPropertyReactive(true);
                }
            }
        }
    }


    public boolean wireFieldAccessors( PackageRegistry pkgRegistry,
                                       AbstractClassTypeDeclarationDescr typeDescr,
                                       TypeDeclaration type ) {

        if ( type.getTypeClassDef() != null ) {
            try {
                buildFieldAccessors( type, pkgRegistry );
            } catch ( Throwable e ) {
                kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                   "Error creating field accessors for TypeDeclaration '" + type.getTypeName() +
                                                                   "' for type '" +
                                                                   type.getTypeName() +
                                                                   " : " + e.getMessage() +
                                                                   "'"));
                return false;
            }
        }

        wireTimestampAccessor( typeDescr, type, pkgRegistry );
        wireDurationAccessor( typeDescr, type, pkgRegistry );
        configureExpirationOffset( typeDescr, type );

        configurePropertyReactivity( typeDescr, type );

        return true;
    }

    protected void buildFieldAccessors(final TypeDeclaration type,
                                       final PackageRegistry pkgRegistry) throws SecurityException,
            IllegalArgumentException,
            InstantiationException,
            IllegalAccessException,
            IOException,
            IntrospectionException,
            ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            NoSuchFieldException {
        ClassDefinition cd = type.getTypeClassDef();
        ClassFieldAccessorStore store = pkgRegistry.getPackage().getClassFieldAccessorStore();
        for ( FieldDefinition attrDef : cd.getFieldsDefinitions() ) {
            ClassFieldAccessor accessor = store.getAccessor( cd.getDefinedClass().getName(),
                                                             attrDef.getName() );
            attrDef.setReadWriteAccessor( accessor );
        }
    }

    protected void wireTimestampAccessor( AbstractClassTypeDeclarationDescr typeDescr, TypeDeclaration type, PackageRegistry pkgRegistry ) {
        AnnotationDescr annotationDescr = typeDescr.getAnnotation( TypeDeclaration.ATTR_TIMESTAMP );
        String timestamp = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
        if ( timestamp != null ) {
            type.setTimestampAttribute( timestamp );
            InternalKnowledgePackage pkg = pkgRegistry.getPackage();

            MVELDialect dialect = (MVELDialect) pkgRegistry.getDialectCompiletimeRegistry().getDialect( "mvel" );
            PackageBuildContext context = new PackageBuildContext();
            context.init( kbuilder, pkg, typeDescr, pkgRegistry.getDialectCompiletimeRegistry(), dialect, null );
            if ( ! type.isTypesafe() ) {
                context.setTypesafe( false );
            }

            MVELAnalysisResult results = (MVELAnalysisResult)
                    context.getDialect().analyzeExpression(context,
                                                           typeDescr,
                                                           timestamp,
                                                           new BoundIdentifiers( Collections.EMPTY_MAP,
                                                                                 Collections.EMPTY_MAP,
                                                                                 Collections.EMPTY_MAP,
                                                                                 type.getTypeClass()));

            if (results != null) {
                InternalReadAccessor reader = pkg.getClassFieldAccessorStore().getMVELReader( ClassUtils.getPackage( type.getTypeClass() ),
                                                                                              type.getTypeClass().getName(),
                                                                                              timestamp,
                                                                                              type.isTypesafe(),
                                                                                              results.getReturnType());

                MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData("mvel");
                data.addCompileable((MVELCompileable) reader);
                ((MVELCompileable) reader).compile(data);
                type.setTimestampExtractor(reader);
            } else {
                kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                   "Error creating field accessors for timestamp field '" + timestamp +
                                                                   "' for type '" +
                                                                   type.getTypeName() +
                                                                   "'"));
            }
        }

    }

    protected void wireDurationAccessor( AbstractClassTypeDeclarationDescr typeDescr, TypeDeclaration type, PackageRegistry pkgRegistry ) {
        AnnotationDescr annotationDescr = typeDescr.getAnnotation(TypeDeclaration.ATTR_DURATION);
        String duration = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
        if (duration != null) {
            type.setDurationAttribute(duration);
            InternalKnowledgePackage pkg = pkgRegistry.getPackage();

            MVELDialect dialect = (MVELDialect) pkgRegistry.getDialectCompiletimeRegistry().getDialect("mvel");
            PackageBuildContext context = new PackageBuildContext();
            context.init(kbuilder, pkg, typeDescr, pkgRegistry.getDialectCompiletimeRegistry(), dialect, null);
            if (!type.isTypesafe()) {
                context.setTypesafe(false);
            }

            MVELAnalysisResult results = (MVELAnalysisResult)
                    context.getDialect().analyzeExpression(context,
                                                           typeDescr,
                                                           duration,
                                                           new BoundIdentifiers(Collections.EMPTY_MAP,
                                                                                Collections.EMPTY_MAP,
                                                                                Collections.EMPTY_MAP,
                                                                                type.getTypeClass()));

            if (results != null) {
                InternalReadAccessor reader = pkg.getClassFieldAccessorStore().getMVELReader(ClassUtils.getPackage(type.getTypeClass()),
                                                                                             type.getTypeClass().getName(),
                                                                                             duration,
                                                                                             type.isTypesafe(),
                                                                                             results.getReturnType());

                MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData("mvel");
                data.addCompileable((MVELCompileable) reader);
                ((MVELCompileable) reader).compile(data);
                type.setDurationExtractor(reader);
            } else {
                kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                   "Error processing @duration for TypeDeclaration '" + type.getFullName() +
                                                                   "': cannot access the field '" + duration + "'"));
            }
        }

    }

    protected void configureExpirationOffset( AbstractClassTypeDeclarationDescr typeDescr, TypeDeclaration type ) {
        AnnotationDescr annotationDescr = typeDescr.getAnnotation(TypeDeclaration.ATTR_EXPIRE);
        String expiration = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
        if (expiration != null) {
            if (timeParser == null) {
                timeParser = new TimeIntervalParser();
            }
            type.setExpirationOffset(timeParser.parse(expiration)[0]);
        }

    }

    protected void configurePropertyReactivity( AbstractClassTypeDeclarationDescr typeDescr, TypeDeclaration type ) {
        PropertySpecificOption propertySpecificOption = kbuilder.getBuilderConfiguration().getOption( PropertySpecificOption.class );
        boolean propertyReactive = propertySpecificOption.isPropSpecific( typeDescr.getAnnotationNames().contains( TypeDeclaration.ATTR_PROP_SPECIFIC ),
                                                                          typeDescr.getAnnotationNames().contains( TypeDeclaration.ATTR_NOT_PROP_SPECIFIC ) );
        kbuilder.setPropertyReactive( typeDescr.getResource(), type, propertyReactive );

    }






}
