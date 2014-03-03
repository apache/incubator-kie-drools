package org.kie.scanner;

import org.drools.core.rule.TypeMetaInfo;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.drools.compiler.kie.builder.impl.InternalKieModule;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public interface KieModuleMetaData {

    Collection<String> getPackages();

    Collection<String> getClasses(String packageName);

    Class<?> getClass(String pkgName, String className);
    
    Map<String, String> getProcesses();

    TypeMetaInfo getTypeMetaInfo(Class<?> clazz);

    Collection<String> getRuleNamesInPackage(String packageName);

    ClassLoader getClassLoader();

    public static class Factory {
        public static KieModuleMetaData newKieModuleMetaData(KieModule kieModule) {
            return new KieModuleMetaDataImpl((InternalKieModule) kieModule);
        }

        public static KieModuleMetaData newKieModuleMetaData(ReleaseId releaseId) {
            return new KieModuleMetaDataImpl(releaseId);
        }

        public static KieModuleMetaData newKieModuleMetaData(File pomFile) {
            return new KieModuleMetaDataImpl(pomFile);
        }
    }
}
