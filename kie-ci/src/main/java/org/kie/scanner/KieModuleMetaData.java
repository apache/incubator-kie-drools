package org.kie.scanner;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.base.rule.TypeMetaInfo;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.util.maven.support.DependencyFilter;

public interface KieModuleMetaData {

    Collection<String> getPackages();

    Collection<String> getClasses( String packageName );

    Class<?> getClass( String pkgName, String className );

    Map<String, String> getProcesses();

    Map<String, String> getForms();

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

        public static KieModuleMetaData newKieModuleMetaData( KieModule kieModule, List<URI> dependencies ) {
            return new KieModuleMetaDataImpl( (InternalKieModule) kieModule, dependencies );
        }

        public static KieModuleMetaData newKieModuleMetaData( ReleaseId releaseId, DependencyFilter dependencyFilter ) {
            return new KieModuleMetaDataImpl( releaseId, dependencyFilter );
        }

        public static KieModuleMetaData newKieModuleMetaData( File pomFile, DependencyFilter dependencyFilter ) {
            return new KieModuleMetaDataImpl( pomFile, dependencyFilter );
        }

        public static KieModuleMetaData newInJarKieModuleMetaData(ReleaseId releaseId, DependencyFilter compileFilter) {
            return new KieInJarModuleMetaDataImpl(releaseId, compileFilter);
        }
    }
}

