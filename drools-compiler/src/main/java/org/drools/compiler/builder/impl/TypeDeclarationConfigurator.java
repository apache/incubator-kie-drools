/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.builder.impl;

import java.util.Map;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.rule.Annotated;
import org.drools.base.rule.TypeDeclaration;
import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.drl.ast.descr.AbstractClassTypeDeclarationDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;
import org.kie.internal.builder.conf.PropertySpecificOption;

import static org.drools.base.rule.TypeDeclaration.processTypeAnnotations;
import static org.drools.compiler.rule.builder.util.AnnotationFactory.toAnnotated;

public class TypeDeclarationConfigurator {

    protected final TypeDeclarationContext context;
    private final BuildResultCollector results;

    public TypeDeclarationConfigurator( TypeDeclarationContext context, BuildResultCollector results ) {
        this.context = context;
        this.results = results;
    }

    public void finalizeConfigurator(TypeDeclaration type, AbstractClassTypeDeclarationDescr typeDescr, PackageRegistry pkgRegistry, Map<String, PackageRegistry> pkgRegistryMap, ClassHierarchyManager hierarchyManager ) {
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
                        oldType.setExpirationType( type.getExpirationPolicy() );
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
                pkgRegistry.getPackage().buildFieldAccessors( type );
            } catch ( Throwable e ) {
                results.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                   "Error creating field accessors for TypeDeclaration '" + type.getTypeName() +
                                                                   "' for type '" +
                                                                   type.getTypeName() +
                                                                   " : " + e.getMessage() +
                                                                   "'"));
                return false;
            }
        }

        Annotated annotatedType = toAnnotated(typeDescr);
        processMvelBasedAccessors(context, results, pkgRegistry, annotatedType, type );
        processTypeAnnotations( type, annotatedType, context.getBuilderConfiguration().getOption(PropertySpecificOption.KEY));
        return true;
    }

    static void processMvelBasedAccessors(TypeDeclarationContext context, BuildResultCollector results, PackageRegistry pkgRegistry, Annotated annotated, TypeDeclaration type ) {
        wireTimestampAccessor(context, results, annotated, type, pkgRegistry );
        wireDurationAccessor(context, results, annotated, type, pkgRegistry );
    }

    private static void wireTimestampAccessor(TypeDeclarationContext context, BuildResultCollector resultCollector, Annotated annotated, TypeDeclaration type, PackageRegistry pkgRegistry ) {
        Timestamp timestamp = annotated.getTypedAnnotation(Timestamp.class);
        if ( timestamp != null ) {
            BaseDescr typeDescr = annotated instanceof BaseDescr ? ( (BaseDescr) annotated ) : new BaseDescr();
            String timestampField;
            try {
                timestampField = timestamp.value();
            } catch (Exception e) {
                resultCollector.addBuilderResult(new TypeDeclarationError(typeDescr, e.getMessage()));
                return;
            }
            type.setTimestampAttribute( timestampField );
            InternalKnowledgePackage pkg = pkgRegistry.getPackage();

            AnalysisResult results = getMvelAnalysisResult(context, typeDescr, type, pkgRegistry, timestampField, pkg );
            if (results != null) {
                type.setTimestampExtractor(pkg.getFieldExtractor( type, timestampField, results.getReturnType() ));
            } else {
                resultCollector.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                   "Error creating field accessors for timestamp field '" + timestamp +
                                                                   "' for type '" + type.getTypeName() + "'"));
            }
        }
    }

    private static void wireDurationAccessor(TypeDeclarationContext context, BuildResultCollector resultCollector, Annotated annotated, TypeDeclaration type, PackageRegistry pkgRegistry ) {
        Duration duration = annotated.getTypedAnnotation(Duration.class);
        if (duration != null) {
            BaseDescr typeDescr = annotated instanceof BaseDescr ? ( (BaseDescr) annotated ) : new BaseDescr();
            String durationField;
            try {
                durationField = duration.value();
            } catch (Exception e) {
                resultCollector.addBuilderResult(new TypeDeclarationError(typeDescr, e.getMessage()));
                return;
            }
            type.setDurationAttribute(durationField);
            InternalKnowledgePackage pkg = pkgRegistry.getPackage();

            AnalysisResult results = getMvelAnalysisResult(context, typeDescr, type, pkgRegistry, durationField, pkg );
            if (results != null) {
                type.setDurationExtractor(pkg.getFieldExtractor( type, durationField, results.getReturnType() ));
            } else {
                resultCollector.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                   "Error processing @duration for TypeDeclaration '" + type.getFullName() +
                                                                   "': cannot access the field '" + durationField + "'"));
            }
        }
    }

    private static AnalysisResult getMvelAnalysisResult(TypeDeclarationContext tdContext, BaseDescr typeDescr, TypeDeclaration type, PackageRegistry pkgRegistry, String durationField, InternalKnowledgePackage pkg ) {
        Dialect dialect = pkgRegistry.getDialectCompiletimeRegistry().getDialect("mvel");
        PackageBuildContext pkgContext = new PackageBuildContext();
        pkgContext.initContext(tdContext,
                pkg, typeDescr, pkgRegistry.getDialectCompiletimeRegistry(), dialect, null);
        if (!type.isTypesafe()) {
            pkgContext.setTypesafe(false);
        }

        return pkgContext.getDialect().analyzeExpression( pkgContext,
                                                       typeDescr,
                                                       durationField,
                                                       new BoundIdentifiers( type.getTypeClass() ) );
    }
}
