package org.kie.builder.impl;

import org.drools.kproject.models.KieModuleModelImpl;
import org.kie.builder.GAV;
import org.kie.builder.KieModuleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.drools.compiler.io.memory.MemoryFileSystem.readFromJar;
import static org.kie.builder.impl.KieBuilderImpl.buildKieModule;

public class JarKieModule extends MemoryKieModule {

    private static final Logger log    = LoggerFactory.getLogger(JarKieModule.class);

    private Messages messages;

    public JarKieModule(GAV gav, File jar) {
        super(gav, getKieModuleModelFromJar(jar), readFromJar(jar));
    }

    public Messages build() {
        if (messages == null) {
            messages = new Messages();
            buildKieModule(this, messages);
        }
        return messages;
    }

    private static KieModuleModel getKieModuleModelFromJar(File jar) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile( jar );
            ZipEntry zipEntry = zipFile.getEntry( KieModuleModelImpl.KMODULE_JAR_PATH );
            return KieModuleModelImpl.fromXML(zipFile.getInputStream(zipEntry));
        } catch ( Exception e ) {
            throw new RuntimeException("Unable to load kmodule.xml from" + jar.getAbsolutePath());
        } finally {
            try {
                zipFile.close();
            } catch ( IOException e ) { }
        }
    }
}
