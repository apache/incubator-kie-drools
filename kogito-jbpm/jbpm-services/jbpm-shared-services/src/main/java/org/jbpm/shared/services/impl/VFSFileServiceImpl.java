/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.shared.services.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceDotFileImpl;
import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Path;

import static org.kie.commons.io.FileSystemType.Bootstrap.*;
import static org.kie.commons.validation.PortablePreconditions.*;

/**
 *
 */
@ApplicationScoped
public class VFSFileServiceImpl implements FileService {

    private static final String REPO_PLAYGROUND = "git://jbpm-playground";
    private static final String ORIGIN_URL      = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground.git";

    private final IOService ioService = new IOServiceDotFileImpl();

    @PostConstruct
    public void init() {
        if ( ioService.getFileSystem( URI.create( REPO_PLAYGROUND ) ) != null ) {
            fetchChanges();
        } else {
            try {
                final String userName = "guvnorngtestuser1";
                final String password = "test1234";
                final URI fsURI = URI.create( "git://jbpm-playground" );

                final Map<String, Object> env = new HashMap<String, Object>();
                env.put( "username", userName );
                env.put( "password", password );
                env.put( "origin", ORIGIN_URL );
                ioService.newFileSystem( fsURI, env, BOOTSTRAP_INSTANCE );
            } catch ( Exception e ) {
                System.out.println( ">>>>>>>>>>>>>>>>>>> E " + e.getMessage() );
            }
        }

    }

    public void fetchChanges() {
        ioService.getFileSystem( URI.create( REPO_PLAYGROUND + "?fetch" ) );
    }

    @Override
    public byte[] loadFile( final String path ) throws FileException {
        Path file = ioService.get( "git://jbpm-playground/" + path );
        
        checkNotNull( "file", file );

        try {
            return ioService.readAllBytes( file );
        } catch ( IOException ex ) {
            throw new FileException( ex.getMessage(), ex );
        }
    }
    
    @Override
    public byte[] loadFile( final Path file ) throws FileException {
        
        checkNotNull( "file", file );

        try {
            return ioService.readAllBytes( file );
        } catch ( IOException ex ) {
            throw new FileException( ex.getMessage(), ex );
        }
    }
    
    

    @Override
    public Iterable<Path> loadFilesByType( final String path,
                                           final String fileType ) {
        return ioService.newDirectoryStream( ioService.get( "git://jbpm-playground/" + path ), new DirectoryStream.Filter<Path>() {
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

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Override
    public boolean exists(Path file){
        return ioService.exists(file);
    }
    
    @Override
    public boolean exists(String file){
        Path path = ioService.get( "git://jbpm-playground/" + file );
        return ioService.exists(path);
    }

    @Override
    public void move(String source, String dest){
        
        checkNotNull( "source", source );
        checkNotNull( "dest", dest );
        
        Path sourcePath = ioService.get( "git://jbpm-playground/" + source );
        Path targetPath = ioService.get( "git://jbpm-playground/" + dest );
        ioService.copy(sourcePath, targetPath);
        ioService.delete(sourcePath);
    }
    
    @Override
    public Path createDirectory(String path){
        
        checkNotNull( "path", path );
        
        return ioService.createDirectory(ioService.get( "git://jbpm-playground/" + path));
    }
    
}
