package org.drools.scanner;

import org.kie.builder.GAV;

import java.io.File;
import java.util.Collection;

public interface KieModuleMetaData {

    Collection<String> getPackages();

    Collection<String> getClasses(String packageName);

    Class<?> getClass(String pkgName, String className);

    public static class Factory {
        public static KieModuleMetaData newKieModuleMetaData(GAV gav) {
            return new KieModuleMetaDataImpl(gav);
        }

        public KieModuleMetaData newKieModuleMetaDataImpl(File pomFile) {
            return new KieModuleMetaDataImpl(pomFile);
        }
    }
}
