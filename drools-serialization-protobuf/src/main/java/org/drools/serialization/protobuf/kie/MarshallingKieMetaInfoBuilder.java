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
package org.drools.serialization.protobuf.kie;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.protobuf.ByteString;
import org.drools.compiler.builder.InternalKnowledgeBuilder;
import org.kie.memorycompiler.resources.ResourceStore;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieMetaInfoBuilder;
import org.drools.base.factmodel.ClassDefinition;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.base.rule.KieModuleMetaInfo;
import org.drools.base.rule.TypeDeclaration;
import org.drools.base.rule.TypeMetaInfo;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.internal.builder.KnowledgeBuilder;

public class MarshallingKieMetaInfoBuilder extends KieMetaInfoBuilder {

    public MarshallingKieMetaInfoBuilder( InternalKieModule kModule) {
        super(kModule);
    }

    public KieModuleMetaInfo generateKieModuleMetaInfo(ResourceStore trgMfs) {
        Map<String, TypeMetaInfo> typeInfos = new HashMap<>();
        Map<String, Set<String>> rulesPerPackage = new HashMap<>();

        KieModuleModel kieModuleModel = kModule.getKieModuleModel();
        for ( String kieBaseName : kieModuleModel.getKieBaseModels().keySet() ) {
            KnowledgeBuilder kBuilder = kModule.getKnowledgeBuilderForKieBase( kieBaseName );
            KieModuleCache.KModuleCache.Builder _kmoduleCacheBuilder = createCacheBuilder();
            KieModuleCache.CompilationData.Builder _compData = createCompilationData();

            for ( KiePackage kPkg : kBuilder.getKnowledgePackages() ) {
                PackageRegistry pkgRegistry = (( InternalKnowledgeBuilder ) kBuilder).getPackageRegistry( kPkg.getName() );
                JavaDialectRuntimeData runtimeData = (JavaDialectRuntimeData) pkgRegistry.getDialectRuntimeRegistry().getDialectData( "java" );

                List<String> types = new ArrayList<>();
                for ( FactType factType : kPkg.getFactTypes() ) {
                    Class< ? > typeClass = ((ClassDefinition) factType).getDefinedClass();
                    TypeDeclaration typeDeclaration = pkgRegistry.getPackage().getTypeDeclaration( typeClass );
                    if ( typeDeclaration != null ) {
                        typeInfos.put( typeClass.getName(), new TypeMetaInfo(typeDeclaration) );
                    }

                    String className = factType.getName();
                    String internalName = className.replace('.', '/') + ".class";
                    if (trgMfs != null) {
                        byte[] bytes = runtimeData.getBytecode( internalName );
                        if ( bytes != null ) {
                            trgMfs.write( internalName, bytes, true );
                        }
                    }
                    types.add( internalName );
                }

                Set<String> rules = rulesPerPackage.get( kPkg.getName() );
                if( rules == null ) {
                    rules = new HashSet<>();
                }
                for ( Rule rule : kPkg.getRules() ) {
                    rules.add(rule.getName());
                }
                if (!rules.isEmpty()) {
                    rulesPerPackage.put(kPkg.getName(), rules);
                }

                addToCompilationData(_compData, runtimeData, types);
            }

            _kmoduleCacheBuilder.addCompilationData( _compData.build() );
            if (trgMfs != null) {
                writeCompilationDataToTrg( _kmoduleCacheBuilder.build(), kieBaseName, trgMfs );
            }
        }
        return new KieModuleMetaInfo(typeInfos, rulesPerPackage);
    }

    private KieModuleCache.KModuleCache.Builder createCacheBuilder() {
        return KieModuleCache.KModuleCache.newBuilder();
    }

    private KieModuleCache.CompilationData.Builder createCompilationData() {
        // Create compilation data cache
        return KieModuleCache.CompilationData.newBuilder().setDialect("java");
    }

    private void addToCompilationData(KieModuleCache.CompilationData.Builder _cdata,
                                      JavaDialectRuntimeData runtimeData,
                                      List<String> types) {
        for ( Map.Entry<String, byte[]> entry : runtimeData.getStore().entrySet() ) {
            if ( !types.contains( entry.getKey() ) ) {
                KieModuleCache.CompDataEntry _entry = KieModuleCache.CompDataEntry.newBuilder()
                                                                    .setId( entry.getKey() )
                                                                    .setData( ByteString.copyFrom(entry.getValue()) )
                                                                    .build();
                _cdata.addEntry( _entry );
            }
        }
    }

    private void writeCompilationDataToTrg(KieModuleCache.KModuleCache _kmoduleCache,
                                           String kieBaseName, ResourceStore trgMfs) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            KieModuleCacheHelper.writeToStreamWithHeader( out, _kmoduleCache );
            String compilatonDataPath = "META-INF/" + kieBaseName.replace( '.', '/' ) + "/kbase.cache";
            trgMfs.write( compilatonDataPath, out.toByteArray(), true );
        } catch ( IOException e ) {
            // what to do here?
        }
    }
}
