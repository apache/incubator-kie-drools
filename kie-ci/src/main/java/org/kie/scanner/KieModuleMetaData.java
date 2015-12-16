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

package org.kie.scanner;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kproject.xml.DependencyFilter;
import org.drools.core.rule.TypeMetaInfo;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public interface KieModuleMetaData {

    Collection<String> getPackages();

    Collection<String> getClasses( String packageName );

    Class<?> getClass( String pkgName, String className );

    Map<String, String> getProcesses();

    TypeMetaInfo getTypeMetaInfo( Class<?> clazz );

    Collection<String> getRuleNamesInPackage( String packageName );

    ClassLoader getClassLoader();

    class Factory {
        public static KieModuleMetaData newKieModuleMetaData( KieModule kieModule ) {
            return newKieModuleMetaData( kieModule, DependencyFilter.TAKE_ALL_FILTER );
        }

        public static KieModuleMetaData newKieModuleMetaData( ReleaseId releaseId ) {
            return newKieModuleMetaData( releaseId, DependencyFilter.TAKE_ALL_FILTER );
        }

        public static KieModuleMetaData newKieModuleMetaData( File pomFile ) {
            return newKieModuleMetaData( pomFile, DependencyFilter.TAKE_ALL_FILTER );
        }

        public static KieModuleMetaData newKieModuleMetaData( KieModule kieModule, DependencyFilter dependencyFilter ) {
            return new KieModuleMetaDataImpl( (InternalKieModule) kieModule, dependencyFilter );
        }

        public static KieModuleMetaData newKieModuleMetaData( ReleaseId releaseId, DependencyFilter dependencyFilter ) {
            return new KieModuleMetaDataImpl( releaseId, dependencyFilter );
        }

        public static KieModuleMetaData newKieModuleMetaData( File pomFile, DependencyFilter dependencyFilter ) {
            return new KieModuleMetaDataImpl( pomFile, dependencyFilter );
        }
    }
}

