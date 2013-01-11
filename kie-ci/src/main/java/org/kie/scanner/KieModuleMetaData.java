package org.kie.scanner;

import org.drools.rule.TypeMetaInfo;
import org.kie.builder.KieModule;
import org.kie.builder.ReleaseId;
import org.kie.builder.impl.InternalKieModule;

import java.io.File;
import java.util.Collection;

public interface KieModuleMetaData {

    Collection<String> getPackages();

    Collection<String> getClasses(String packageName);

    Class<?> getClass(String pkgName, String className);

    TypeMetaInfo getTypeMetaInfo(Class<?> clazz);

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
