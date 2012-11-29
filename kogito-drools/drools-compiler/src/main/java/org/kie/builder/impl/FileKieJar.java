package org.kie.builder.impl;

import org.kie.builder.KieContainer;
import org.kie.builder.KieProject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.drools.kproject.KieProjectImpl.fromXML;

public class FileKieJar extends AbstractKieJar {

    private final File file;

    public FileKieJar(File file) {
        super(getKieProject(file));
        this.file = file;
    }

    public File asFile() {
        return file;
    }

    public byte[] getBytes() {
        throw new UnsupportedOperationException("org.kie.builder.impl.FileKieJar.getBytes -> TODO");
    }

    public InputStream getInputStream() {
        throw new UnsupportedOperationException("org.kie.builder.impl.FileKieJar.getInputStream -> TODO");
    }

    public List<String> getFiles() {
        throw new UnsupportedOperationException("org.kie.builder.impl.FileKieJar.getFiles -> TODO");
    }

    public byte[] getBytes(String path) {
        throw new UnsupportedOperationException("org.kie.builder.impl.FileKieJar.getBytes -> TODO");
    }

    public InputStream getInputStream(String path) {
        throw new UnsupportedOperationException("org.kie.builder.impl.FileKieJar.getInputStream -> TODO");
    }

    private static KieProject getKieProject(File kJar) {
        ZipFile zipFile;
        try {
            zipFile = new ZipFile( kJar );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ZipEntry zipEntry = zipFile.getEntry( KieContainer.KPROJECT_JAR_PATH );
        if (zipEntry != null) {
            InputStream zipStream = null;
            try {
                zipStream = zipFile.getInputStream( zipEntry );
                return fromXML(zipStream);
            } catch (IOException e) {
                new RuntimeException("Cannot open kproject.xml", e);
            } finally {
                if (zipStream != null) {
                    try {
                        zipStream.close();
                    } catch (IOException e) { }
                }
            }
        }
        throw new RuntimeException("Invalid kjar: doesn't have a kproject.xml file");
    }
}
