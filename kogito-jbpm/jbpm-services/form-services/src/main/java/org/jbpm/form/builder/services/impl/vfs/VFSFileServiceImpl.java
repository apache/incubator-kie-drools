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
package org.jbpm.form.builder.services.impl.vfs;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.jbpm.form.builder.services.api.FileException;
import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceDotFileImpl;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Path;

import static org.kie.commons.io.FileSystemType.Bootstrap.*;

/**
 *
 */
public class VFSFileServiceImpl {

    private static final String REPO_PLAYGROUND = "git://playground";
    private static final String ORIGIN_URL      = "https://github.com/guvnorngtestuser1/formbuilder-playground.git";

    private final IOService ioService = new IOServiceDotFileImpl();

    public void checkFileSystem() {
        if ( ioService.getFileSystem( URI.create( REPO_PLAYGROUND ) ) == null ) {
            final String userName = "guvnorngtestuser1";
            final String password = "test1234";
            final URI fsURI = URI.create( REPO_PLAYGROUND );

            final Map<String, Object> env = new HashMap<String, Object>() {
                {
                    put( "username", userName );
                    put( "password", password );
                    put( "giturl", ORIGIN_URL );
                }
            };

            ioService.newFileSystem( fsURI, env, BOOTSTRAP_INSTANCE );
        }
    }

    public byte[] loadFile( final Path file ) throws FileException {
        if ( file == null ) {
            throw new IllegalArgumentException();
        }

        checkFileSystem();

        return ioService.readAllBytes( file );

    }

    public Iterable<Path> loadFilesByType( final String fileType ) throws FileException {
        checkFileSystem();

        return ioService.newDirectoryStream( ioService.get( "default://playground" ), new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( final Path entry ) {
                if ( entry.getFileName().toString().endsWith( fileType ) ) {
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

}
