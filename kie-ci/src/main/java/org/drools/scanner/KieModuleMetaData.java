package org.drools.scanner;

import org.kie.builder.ReleaseId;

import java.io.File;
import java.util.Collection;

public interface KieModuleMetaData {

    Collection<String> getPackages();

    Collection<String> getClasses(String packageName);

    Class<?> getClass(String pkgName, String className);

    public static class Factory {
        public static KieModuleMetaData newKieModuleMetaData(ReleaseId releaseId) {
            return new KieModuleMetaDataImpl(releaseId);
        }

        public KieModuleMetaData newKieModuleMetaDataImpl(File pomFile) {
            return new KieModuleMetaDataImpl(pomFile);
        }
    }
}
