package org.kie.builder.impl;

import java.io.IOException;

import org.drools.core.util.IoUtils;
import org.drools.core.util.StringUtils;
import org.drools.kproject.KieProjectImpl;
import org.drools.kproject.memory.MemoryFileSystem;
import org.kie.builder.GAV;
import org.kie.builder.KieFileSystem;
import org.kie.io.Resource;

public class KieFileSystemImpl
        implements
        KieFileSystem {

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
            return write( path, IoUtils.readBytesFromInputStream( resource.getInputStream() ) );
        } catch ( IOException e ) {
            throw new RuntimeException("Unable to write Resource: " + resource.toString(), e);
        }
    }

    public void delete(String... paths) {
        for ( String path : paths ) {
            mfs.remove( path );
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

    public KieFileSystem writeProjectXML(byte[] content) {
        write(KieProjectImpl.KPROJECT_RELATIVE_PATH, content);
        return this;
    }

    public KieFileSystem writeProjectXML(String content) {
        write(KieProjectImpl.KPROJECT_RELATIVE_PATH, content);
        return this;
    }
}
