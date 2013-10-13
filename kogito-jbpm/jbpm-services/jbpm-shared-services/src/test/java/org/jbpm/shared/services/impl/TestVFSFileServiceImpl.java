package org.jbpm.shared.services.impl;

import java.io.File;
import java.io.OutputStream;
import java.net.URI;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Alternative;

import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardOpenOption;

import static org.uberfire.commons.validation.PortablePreconditions.*;

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
    public byte[] loadFile(Path file) throws FileException {
        checkNotNull( "file", file );

        try {
            return ioService.readAllBytes( file );
        } catch ( IOException ex ) {
            throw new FileException( ex.getMessage(), ex );
        }
    }

    @Override
    public Iterable<Path> loadFilesByType(Path path, final String fileType)
            throws FileException {
        return ioService.newDirectoryStream( path, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( final Path entry ) throws IOException {
                if ( !Files.isDirectory( entry ) &&
                        (entry.getFileName().toString().endsWith( fileType )
                                || entry.getFileName().toString().matches(fileType))) {
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
    public void move(Path source, Path dest) {
        this.copy(source, dest);
        ioService.delete(source);
    }

    @Override
    public void copy(Path source, Path dest) {
        checkNotNull( "source", source );
        checkNotNull( "dest", dest );
        ioService.copy(source, dest);
    }

    @Override
    public Path createDirectory(Path path) {
        checkNotNull( "path", path );
        
        return ioService.createDirectory(path);
    }

    @Override
    public boolean deleteIfExists(Path path) {
        checkNotNull( "path", path );
        
        return ioService.deleteIfExists(path );
    }

    @Override
    public OutputStream openFile(Path path) {
        checkNotNull( "path", path );
        
        return ioService.newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Override
    public Path createFile(Path path) {
      return ioService.createFile(path);
    }

    @Override
    public Iterable<Path> listDirectories(Path path) {
        return ioService.newDirectoryStream(
                path,
                new DirectoryStream.Filter<Path>() {
                    @Override
                    public boolean accept(final Path entry) throws IOException {
                        if (Files.isDirectory( entry )) {
                            return true;
                        }
                        return false;
                    }
                });
    }

    @Override
    public Path getPath(String path) {
        return ioService.get(repositoryRoot + path);
    }

}
