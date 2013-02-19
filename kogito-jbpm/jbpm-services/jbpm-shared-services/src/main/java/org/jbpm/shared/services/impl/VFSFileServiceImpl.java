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

import static org.kie.commons.validation.PortablePreconditions.checkNotNull;

import java.io.OutputStream;
import java.net.URI;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Path;

/**
 *
 */
@Singleton
public class VFSFileServiceImpl implements FileService {

    private static final String REPO_PLAYGROUND = "git://jbpm-playground/";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @PostConstruct
    public void init() {
        fetchChanges();
    }

    public void fetchChanges() {
        ioService.getFileSystem( URI.create( REPO_PLAYGROUND + "?fetch" ) );
    }

    @Override
    public byte[] loadFile( final String path ) throws FileException {
        Path file = ioService.get(REPO_PLAYGROUND + path );
        
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
        return ioService.newDirectoryStream( ioService.get( REPO_PLAYGROUND + path ), new DirectoryStream.Filter<Path>() {
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
    
    public Iterable<Path> listDirectories(final String path){
      return ioService.newDirectoryStream( ioService.get( REPO_PLAYGROUND + path ), new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( final Path entry ) throws IOException {
                if ( org.kie.commons.java.nio.file.Files.isDirectory(entry) ) {
                    return true;
                }
                return false;
            }
        } );
    
    }

    @Override
    public boolean exists(Path file){
        return ioService.exists(file);
    }
    
    @Override
    public boolean exists(String file){
        Path path = ioService.get( REPO_PLAYGROUND + file );
        return ioService.exists(path);
    }

    @Override
    public void move(String source, String dest){
        
        this.copy(source, dest);
        ioService.delete(ioService.get( REPO_PLAYGROUND + source ));
    }
    
    @Override
    public void copy(String source, String dest){
        
        checkNotNull( "source", source );
        checkNotNull( "dest", dest );
        
        Path sourcePath = ioService.get( REPO_PLAYGROUND + source );
        Path targetPath = ioService.get( REPO_PLAYGROUND + dest );
        ioService.copy(sourcePath, targetPath);
    }
    
    @Override
    public Path createDirectory(String path){
        
        checkNotNull( "path", path );
        
        return ioService.createDirectory(ioService.get( REPO_PLAYGROUND + path));
    }
    
    @Override
    public Path createFile(String path){
        return ioService.createFile(ioService.get( REPO_PLAYGROUND + path));
    }
    
    @Override
    public boolean deleteIfExists(String path){
        
        checkNotNull( "path", path );
        
        return ioService.deleteIfExists(ioService.get( REPO_PLAYGROUND + path ));
    }
    
    @Override
    public OutputStream openFile(String path){
        
        checkNotNull( "path", path );
        
        return ioService.newOutputStream(ioService.get( REPO_PLAYGROUND + path ));
    }

    @Override
    public String getRepositoryRoot() {
        return REPO_PLAYGROUND;
    }
    
}
