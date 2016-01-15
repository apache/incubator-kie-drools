/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.builder.impl;

import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.compiler.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.compiler.lang.descr.Annotated;
import org.drools.compiler.lang.descr.BaseDescr;
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
import org.kie.api.definition.type.ClassReactive;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;
import org.kie.internal.builder.conf.PropertySpecificOption;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;

public class TypeDeclarationConfigurator {

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
                if (type.getRole() == Role.Type.EVENT) {
                    oldType.setRole(Role.Type.EVENT);
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

        processTypeAnnotations( kbuilder, pkgRegistry, typeDescr, type );
        return true;
    }

    static void processTypeAnnotations( KnowledgeBuilderImpl kbuilder, PackageRegistry pkgRegistry, Annotated annotated, TypeDeclaration type ) {
        wireTimestampAccessor( kbuilder, annotated, type, pkgRegistry );
        wireDurationAccessor( kbuilder, annotated, type, pkgRegistry );
        configureExpirationOffset( kbuilder, annotated, type );
        configurePropertyReactivity( kbuilder, annotated, type );
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

    private static void wireTimestampAccessor( KnowledgeBuilderImpl kbuilder, Annotated annotated, TypeDeclaration type, PackageRegistry pkgRegistry ) {
        Timestamp timestamp = annotated.getTypedAnnotation(Timestamp.class);
        if ( timestamp != null ) {
            BaseDescr typeDescr = annotated instanceof BaseDescr ? ( (BaseDescr) annotated ) : new BaseDescr();
            String timestampField;
            try {
                timestampField = timestamp.value();
            } catch (Exception e) {
                kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, e.getMessage()));
                return;
            }
            type.setTimestampAttribute( timestampField );
            InternalKnowledgePackage pkg = pkgRegistry.getPackage();

            MVELAnalysisResult results = getMvelAnalysisResult( kbuilder, typeDescr, type, pkgRegistry, timestampField, pkg );
            if (results != null) {
                type.setTimestampExtractor(getFieldExtractor( type, timestampField, pkg, results ));
            } else {
                kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                   "Error creating field accessors for timestamp field '" + timestamp +
                                                                   "' for type '" + type.getTypeName() + "'"));
            }
        }
    }

    private static void wireDurationAccessor( KnowledgeBuilderImpl kbuilder, Annotated annotated, TypeDeclaration type, PackageRegistry pkgRegistry ) {
        Duration duration = annotated.getTypedAnnotation(Duration.class);
        if (duration != null) {
            BaseDescr typeDescr = annotated instanceof BaseDescr ? ( (BaseDescr) annotated ) : new BaseDescr();
            String durationField;
            try {
                durationField = duration.value();
            } catch (Exception e) {
                kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, e.getMessage()));
                return;
            }
            type.setDurationAttribute(durationField);
            InternalKnowledgePackage pkg = pkgRegistry.getPackage();

            MVELAnalysisResult results = getMvelAnalysisResult( kbuilder, typeDescr, type, pkgRegistry, durationField, pkg );
            if (results != null) {
                type.setDurationExtractor(getFieldExtractor( type, durationField, pkg, results ));
            } else {
                kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                   "Error processing @duration for TypeDeclaration '" + type.getFullName() +
                                                                   "': cannot access the field '" + durationField + "'"));
            }
        }
    }

    private static MVELAnalysisResult getMvelAnalysisResult( KnowledgeBuilderImpl kbuilder, BaseDescr typeDescr, TypeDeclaration type, PackageRegistry pkgRegistry, String durationField, InternalKnowledgePackage pkg ) {
        MVELDialect dialect = (MVELDialect) pkgRegistry.getDialectCompiletimeRegistry().getDialect("mvel");
        PackageBuildContext context = new PackageBuildContext();
        context.init(kbuilder, pkg, typeDescr, pkgRegistry.getDialectCompiletimeRegistry(), dialect, null);
        if (!type.isTypesafe()) {
            context.setTypesafe(false);
        }

        return (MVELAnalysisResult)
                context.getDialect().analyzeExpression(context,
                                                       typeDescr,
                                                       durationField,
                                                       new BoundIdentifiers( Collections.EMPTY_MAP,
                                                                            Collections.EMPTY_MAP,
                                                                            Collections.EMPTY_MAP,
                                                                            type.getTypeClass()));
    }

    private static InternalReadAccessor getFieldExtractor( TypeDeclaration type, String timestampField, InternalKnowledgePackage pkg, MVELAnalysisResult results ) {
        InternalReadAccessor reader = pkg.getClassFieldAccessorStore().getMVELReader( ClassUtils.getPackage( type.getTypeClass() ),
                                                                                      type.getTypeClass().getName(),
                                                                                      timestampField,
                                                                                      type.isTypesafe(),
                                                                                      results.getReturnType());

        MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData("mvel");
        data.addCompileable((MVELCompileable) reader);
        ((MVELCompileable) reader).compile(data);
        return reader;
    }

    private static void configureExpirationOffset( KnowledgeBuilderImpl kbuilder, Annotated annotated, TypeDeclaration type ) {
        Expires expires = annotated.getTypedAnnotation(Expires.class);
        if (expires != null) {
            String expiration;
            try {
                expiration = expires.value();
            } catch (Exception e) {
                if (annotated instanceof BaseDescr) {
                    kbuilder.addBuilderResult( new TypeDeclarationError( (BaseDescr)annotated, e.getMessage() ) );
                }
                return;
            }
            long offset = TimeIntervalParser.parseSingle( expiration );
            // @Expires( -1 ) means never expire
            type.setExpirationOffset(offset == -1L ? Long.MAX_VALUE : offset);
        }
    }

    private static void configurePropertyReactivity( KnowledgeBuilderImpl kbuilder, Annotated annotated, TypeDeclaration type ) {
        PropertySpecificOption propertySpecificOption = kbuilder.getBuilderConfiguration().getPropertySpecificOption();
        boolean propertyReactive = propertySpecificOption.isPropSpecific( annotated.hasAnnotation( PropertyReactive.class ),
                                                                          annotated.hasAnnotation( ClassReactive.class ) );
        type.setPropertyReactive(propertyReactive);
    }
}
