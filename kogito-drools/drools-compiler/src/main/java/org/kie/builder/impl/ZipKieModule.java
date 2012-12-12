package org.kie.builder.impl;

import org.drools.core.util.IoUtils;
import org.drools.kproject.models.KieModuleModelImpl;
import org.kie.builder.ReleaseId;
import org.kie.builder.KieModuleModel;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipKieModule extends AbstractKieModule implements InternalKieModule {
    private final File             file;    
    private Map<String, ZipEntry> zipEntries;

    public ZipKieModule(ReleaseId releaseId, File jar) {
        this(releaseId, getKieModuleModelFromJar(jar), jar);
    }

    public ZipKieModule(ReleaseId releaseId,
                        KieModuleModel kieProject,
                        File file) {
        super(releaseId, kieProject );
        this.file = file;
        this.zipEntries = IoUtils.buildZipFileMapEntries( file );
    }

    private static KieModuleModel getKieModuleModelFromJar(File jar) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile( jar );
            ZipEntry zipEntry = zipFile.getEntry(KieModuleModelImpl.KMODULE_JAR_PATH);
            return KieModuleModelImpl.fromXML(zipFile.getInputStream(zipEntry));
        } catch ( Exception e ) {
            throw new RuntimeException("Unable to load kmodule.xml from " + jar.getAbsolutePath(), e);
        } finally {
            try {
                zipFile.close();
            } catch ( IOException e ) { }
        }
    }

    @Override
    public File getFile() {
        return this.file;
    }


    @Override
    public boolean isAvailable(String name ) {
        return this.zipEntries.containsKey( name );
    }


    @Override
    public byte[] getBytes(String name) {
        ZipEntry entry = this.zipEntries.get( name );
        if ( entry == null ) {
            return null;
        }
        
        ZipFile zipFile = null;
        byte[] bytes = null;
        try {
            zipFile = new ZipFile( file );
            bytes = IoUtils.readBytesFromInputStream(  zipFile.getInputStream( entry ) );
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get ZipFile bytes for :  " + name + " : " + file, e );
        } finally {
            if ( zipFile != null ) {
                try {
                    zipFile.close();
                } catch ( IOException e ) {
                    throw new RuntimeException( "Unable to close ZipFile when getting bytes for :  " + name + " : " + file, e );
                }
            }
        }
        return bytes;
    }

    @Override
    public Collection<String> getFileNames() {
        return this.zipEntries.keySet();
    }

    @Override
    public byte[] getBytes() {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return "ZipKieModule[ ReleaseId=" + getReleaseId() + "file=" + file + "]";
    }
}
