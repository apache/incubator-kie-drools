package org.kie.builder.impl;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.drools.compiler.io.memory.MemoryFileSystem;
import org.drools.kproject.models.KieModuleModelImpl;
import org.kie.builder.GAV;
import org.kie.builder.KieFileSystem;
import org.kie.io.Resource;
import org.kie.io.ResourceConfiguration;
import org.kie.io.ResourceType;

public class KieFileSystemImpl
        implements
        KieFileSystem {
    
    private static final String RESOURCE_PATH_PREFIX = "src/main/resource/"; 

    private final MemoryFileSystem mfs;

    public KieFileSystemImpl() {
        this(new MemoryFileSystem());
    }

    public KieFileSystemImpl(MemoryFileSystem mfs) {
        this.mfs = mfs;
    }

    public KieFileSystem write(String path,
                               byte[] content) {
        mfs.write( path, content, true );
        return this;
    }

    public KieFileSystem write(String path,
                               String text) {
        return write( path, text.getBytes() );
    }

    public KieFileSystem write(String path,
                               Resource resource) {
        try {
            return write( path, readBytesFromInputStream(resource.getInputStream()) );
        } catch (IOException e) {
            throw new RuntimeException("Unable to write Resource: " + resource.toString(), e);
        }
    }

    public KieFileSystem write(Resource resource) {
        try {
            if( resource.getName() != null ) {
                write( RESOURCE_PATH_PREFIX+resource.getName(), readBytesFromInputStream(resource.getInputStream()) );
                ResourceConfiguration conf = resource.getConfiguration();
                if( conf != null ) {
                    Properties prop = ResourceType.toProperties( conf );
                    ByteArrayOutputStream buff = new ByteArrayOutputStream();
                    prop.store( buff, "Configuration properties for resource: "+resource.getName() );
                    write( RESOURCE_PATH_PREFIX+resource.getName()+".properties", buff.toByteArray() );
                }
                return this;
            } else {
                throw new RuntimeException( "Resource does not have a name. Impossible to add it to the bundle. Please set the name of the resource before adding it." + resource.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to write Resource: " + resource.toString(), e);
        }
    }
    
    public void delete(String... paths) {
        for ( String path : paths ) {
            mfs.remove(path);
        }
    }

    public byte[] read(String path) {
        return mfs.read( path );
    }

    public MemoryFileSystem asMemoryFileSystem() {
        return mfs;
    }

    public KieFileSystem generateAndWritePomXML(GAV gav) {
        write("pom.xml", KieBuilderImpl.generatePomXml( gav ) );        
        return this;
    }

    public KieFileSystem writePomXML(byte[] content) {
        write("pom.xml", content);
        return this;
    }

    public KieFileSystem writePomXML(String content) {
        write("pom.xml", content);
        return this;
    }

    public KieFileSystem writeKModuleXML(byte[] content) {
        write(KieModuleModelImpl.KMODULE_SRC_PATH, content);
        return this;
    }

    public KieFileSystem writeKModuleXML(String content) {
        write(KieModuleModelImpl.KMODULE_SRC_PATH, content);
        return this;
    }
    
}
