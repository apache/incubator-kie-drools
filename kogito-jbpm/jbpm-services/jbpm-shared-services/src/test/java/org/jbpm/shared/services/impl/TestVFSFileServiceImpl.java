package org.jbpm.shared.services.impl;

import static org.kie.commons.validation.PortablePreconditions.checkNotNull;

import java.io.File;
import java.io.OutputStream;
import java.net.URI;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Alternative;

import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceNio2WrapperImpl;
import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.java.nio.file.StandardOpenOption;

@Alternative
public class TestVFSFileServiceImpl implements FileService {

    private static final String TEST_RESOURCES = "src/test/resources/repo/";
    private String repositoryRoot;
    
    
    private final IOService ioService = new IOServiceNio2WrapperImpl();
    

    
    @Override
    @PostConstruct
    public void init() throws FileException {
        repositoryRoot = new File(TEST_RESOURCES).toURI().toString();
        ioService.getFileSystem( URI.create( repositoryRoot )); 
    }

    @Override
    public void fetchChanges() {
        // no op

    }

    @Override
    public byte[] loadFile(String path) throws FileException {
        Path file = ioService.get( repositoryRoot + path );
        
        checkNotNull( "file", file );

        try {
            return ioService.readAllBytes( file );
        } catch ( IOException ex ) {
            throw new FileException( ex.getMessage(), ex );
        }
    }

    @Override
    public byte[] loadFile(Path file) throws FileException {
        checkNotNull( "file", file );

        try {
            return ioService.readAllBytes( file );
        } catch ( IOException ex ) {
            throw new FileException( ex.getMessage(), ex );
        }
    }

    @Override
    public Iterable<Path> loadFilesByType(String path, final String fileType)
            throws FileException {
        return ioService.newDirectoryStream( ioService.get( repositoryRoot + path ), new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( final Path entry ) throws IOException {
                if ( !org.kie.commons.java.nio.file.Files.isDirectory(entry) && 
                        entry.getFileName().toString().endsWith( fileType ) ) {
                    return true;
                }
                return false;
            }
        } );
    }

    @Override
    public boolean exists(Path file) {
        return ioService.exists(file);
    }

    @Override
    public boolean exists(String file) {
        Path path = ioService.get( repositoryRoot + file );
        return ioService.exists(path);
    }

    @Override
    public void move(String source, String dest) {
        this.copy(source, dest);
        ioService.delete(ioService.get( repositoryRoot + source ));
    }

    @Override
    public void copy(String source, String dest) {
        checkNotNull( "source", source );
        checkNotNull( "dest", dest );
        
        Path sourcePath = ioService.get( repositoryRoot + source );
        Path targetPath = ioService.get( repositoryRoot + dest );
        ioService.copy(sourcePath, targetPath);
    }

    @Override
    public Path createDirectory(String path) {
        checkNotNull( "path", path );
        
        return ioService.createDirectory(ioService.get( repositoryRoot + path));
    }

    @Override
    public boolean deleteIfExists(String path) {
        checkNotNull( "path", path );
        
        return ioService.deleteIfExists(ioService.get( repositoryRoot + path ));
    }

    @Override
    public OutputStream openFile(String path) {
        checkNotNull( "path", path );
        
        return ioService.newOutputStream(ioService.get( repositoryRoot + path ), StandardOpenOption.TRUNCATE_EXISTING);
    }

}
