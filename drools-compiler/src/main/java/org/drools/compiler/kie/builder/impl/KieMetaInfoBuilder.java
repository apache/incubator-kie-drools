/*
 * Copyright 2015 JBoss Inc
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

package org.drools.compiler.kie.builder.impl;

import com.google.protobuf.ByteString;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.commons.jci.stores.ResourceStore;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.rule.KieModuleMetaInfo;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.rule.TypeMetaInfo;
import org.drools.core.util.IoUtils;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KieMetaInfoBuilder {

    private final ResourceStore trgMfs;
    private final InternalKieModule kModule;

    public KieMetaInfoBuilder(ResourceStore trgMfs, InternalKieModule kModule) {
        this.trgMfs = trgMfs;
        this.kModule = kModule;
    }

    public void writeKieModuleMetaInfo() {
        KieModuleMetaInfo info = generateKieModuleMetaInfo();
        trgMfs.write( KieModuleModelImpl.KMODULE_INFO_JAR_PATH,
                      info.marshallMetaInfos().getBytes( IoUtils.UTF8_CHARSET ),
                      true );
    }

    private KieModuleMetaInfo generateKieModuleMetaInfo() {
        // TODO: I think this method is wrong because it is only inspecting packages that are included
        // in at least one kbase, but I believe it should inspect all packages, even if not included in
        // any kbase, as they could be included in the future
        Map<String, TypeMetaInfo> typeInfos = new HashMap<String, TypeMetaInfo>();
        Map<String, Set<String>> rulesPerPackage = new HashMap<String, Set<String>>();

        KieModuleModel kieModuleModel = kModule.getKieModuleModel();
        for ( String kieBaseName : kieModuleModel.getKieBaseModels().keySet() ) {
            KnowledgeBuilderImpl kBuilder = (KnowledgeBuilderImpl) kModule.getKnowledgeBuilderForKieBase( kieBaseName );
            Map<String, PackageRegistry> pkgRegistryMap = kBuilder.getPackageRegistry();

            KieModuleCache.KModuleCache.Builder _kmoduleCacheBuilder = createCacheBuilder();
            KieModuleCache.CompilationData.Builder _compData = createCompilationData();

            for ( KiePackage kPkg : kBuilder.getKnowledgePackages() ) {
                PackageRegistry pkgRegistry = pkgRegistryMap.get( kPkg.getName() );
                JavaDialectRuntimeData runtimeData = (JavaDialectRuntimeData) pkgRegistry.getDialectRuntimeRegistry().getDialectData( "java" );

                List<String> types = new ArrayList<String>();
                for ( FactType factType : kPkg.getFactTypes() ) {
                    Class< ? > typeClass = ((ClassDefinition) factType).getDefinedClass();
                    TypeDeclaration typeDeclaration = pkgRegistry.getPackage().getTypeDeclaration( typeClass );
                    if ( typeDeclaration != null ) {
                        typeInfos.put( typeClass.getName(), new TypeMetaInfo(typeDeclaration) );
                    }

                    String className = factType.getName();
                    String internalName = className.replace('.', '/') + ".class";
                    byte[] bytes = runtimeData.getBytecode(internalName);
                    if (bytes != null) {
                        trgMfs.write( internalName, bytes, true );
                    }
                    types.add( internalName );
                }

                Set<String> rules = rulesPerPackage.get( kPkg.getName() );
                if( rules == null ) {
                    rules = new HashSet<String>();
                }
                for ( Rule rule : kPkg.getRules() ) {
                    if( !rules.contains( rule.getName() ) ) {
                        rules.add(rule.getName());
                    }
                }
                if (!rules.isEmpty()) {
                    rulesPerPackage.put(kPkg.getName(), rules);
                }

                addToCompilationData(_compData, runtimeData, types);
            }

            _kmoduleCacheBuilder.addCompilationData( _compData.build() );
            writeCompilationDataToTrg( _kmoduleCacheBuilder.build(), kieBaseName );
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
                                           String kieBaseName) {
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
