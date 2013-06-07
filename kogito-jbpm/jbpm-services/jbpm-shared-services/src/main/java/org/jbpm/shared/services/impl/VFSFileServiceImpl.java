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
import javax.enterprise.inject.Instance;
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
    private Instance<IOService> ioService;

    @PostConstruct
    public void init() {
        fetchChanges();
    }

    public void fetchChanges() {
        getIOService().getFileSystem( URI.create( REPO_PLAYGROUND + "?fetch" ) );
    }
    
    @Override
    public byte[] loadFile( final Path file ) throws FileException {
        
        checkNotNull( "file", file );

        try {
            return getIOService().readAllBytes( file );
        } catch ( IOException ex ) {
            throw new FileException( ex.getMessage(), ex );
        }
    }
    
    

    @Override
    public Iterable<Path> loadFilesByType( final Path path,
                                           final String fileType ) {
        return getIOService().newDirectoryStream( path, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( final Path entry ) throws IOException {
                if ( !org.kie.commons.java.nio.file.Files.isDirectory(entry) && 
                        (entry.getFileName().toString().endsWith( fileType )
                                || entry.getFileName().toString().matches(fileType))) {
                    return true;
                }
                return false;
            }
        } );
    }
    
    public Iterable<Path> listDirectories(final Path path){
      return getIOService().newDirectoryStream( path, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( final Path entry ) throws IOException {
                if ( org.kie.commons.java.nio.file.Files.isDirectory(entry) ) {
                    return true;
                }
                return false;
            }
        } );
    
    }
    
    public Path getPath(String path){
        return getIOService().get(path);
    }

    @Override
    public boolean exists(Path file){
        return getIOService().exists(file);
    }

    @Override
    public void move(Path source, Path dest){
        
        this.copy(source, dest);
        getIOService().delete(source);
    }
    
    @Override
    public void copy(Path source, Path dest){
        
        checkNotNull( "source", source );
        checkNotNull( "dest", dest );
        
        getIOService().copy(source, dest);
    }
    
    @Override
    public Path createDirectory(Path path){
        
        checkNotNull( "path", path );
        
        return getIOService().createDirectory(path);
    }
    
    @Override
    public Path createFile(Path path){
        return getIOService().createFile(path);
    }
    
    @Override
    public boolean deleteIfExists(Path path){
        
        checkNotNull( "path", path );
        
        return getIOService().deleteIfExists(path);
    }
    
    @Override
    public OutputStream openFile(Path path){
        
        checkNotNull( "path", path );
        
        return getIOService().newOutputStream(path);
    }
    
    protected IOService getIOService() {
        return ioService.get();
    }
    
}
