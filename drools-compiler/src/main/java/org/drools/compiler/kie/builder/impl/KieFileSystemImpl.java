/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.kie.builder.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.Properties;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.io.InternalResource;
import org.drools.util.IoUtils;
import org.drools.util.PortablePath;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceTypeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.JAVA_ROOT;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.RESOURCES_ROOT;

public class KieFileSystemImpl
        implements
        KieFileSystem,
        Serializable {

    private static Logger logger = LoggerFactory.getLogger( KieFileSystemImpl.class );

    private final MemoryFileSystem mfs;

    public KieFileSystemImpl() {
        this(new MemoryFileSystem());
    }

    public KieFileSystemImpl(MemoryFileSystem mfs) {
        this.mfs = mfs;
    }

    public KieFileSystem write(String path, byte[] content) {
        mfs.write( path, content, true );
        return this;
    }

    public KieFileSystem write(PortablePath path, byte[] content) {
        mfs.write( path, content, true );
        return this;
    }

    public KieFileSystem write(String path, String text) {
        return write( path, text.getBytes( IoUtils.UTF8_CHARSET ) );
    }

    public KieFileSystem write(PortablePath path, String text) {
        return write( path, text.getBytes( IoUtils.UTF8_CHARSET ) );
    }

    public KieFileSystem write(String path, Resource resource) {
        mfs.write( PortablePath.of(path), resource );
        return this;
    }

    public KieFileSystem write(PortablePath path, Resource resource) {
        mfs.write( path, resource );
        return this;
    }

    public KieFileSystem write(Resource resource) {
        try {
            String target = resource.getTargetPath() != null ? resource.getTargetPath() : resource.getSourcePath();
            if( target != null ) {
                String prefix = resource.getResourceType() == ResourceType.JAVA ? JAVA_ROOT : RESOURCES_ROOT;
                int prefixPos = target.indexOf( prefix );
                String path = prefixPos >= 0 ? target.substring( prefixPos ) : prefix + target;
                if (resource.getResourceType() == ResourceType.XSD) {
                    write( path, (( InternalResource ) resource).getBytes() );
                } else {
                    write( path, resource );
                }
                ResourceConfiguration conf = resource.getConfiguration();
                if( conf != null ) {
                    Properties prop = ResourceTypeImpl.toProperties(conf);
                    ByteArrayOutputStream buff = new ByteArrayOutputStream();
                    prop.store( buff, "Configuration properties for resource: " + target );
                    write( path + ".properties", buff.toByteArray() );
                }
                return this;
            } else {
                throw new RuntimeException( "Resource does not have neither a source nor a target path. Impossible to add it to the bundle. Please set either the source or target name of the resource before adding it." + resource.toString());
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to write Resource: " + resource.toString(), e);
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

    public KieFileSystem generateAndWritePomXML(ReleaseId releaseId) {
        write("pom.xml", KieBuilderImpl.generatePomXml(releaseId) );
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

    public MemoryFileSystem getMfs() {
        return mfs;
    }

    public KieFileSystem clone() {
        try {
            final ByteArrayOutputStream byteArray = writeToByteArray( this );
            return readFromByteArray( byteArray );
        } catch ( IOException | ClassNotFoundException ioe ) {
            logger.warn( "Unable to clone KieFileSystemImpl", ioe );
            return null;
        }
    }

    private KieFileSystem readFromByteArray( final ByteArrayOutputStream byteArrayOutputStream ) throws IOException, ClassNotFoundException {
        final byte[] byteArray = byteArrayOutputStream.toByteArray();
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( byteArray );
        final ObjectInputStream inputStream = new ObjectInputStream( byteArrayInputStream );

        return (KieFileSystem) inputStream.readObject();
    }

    private ByteArrayOutputStream writeToByteArray( final KieFileSystemImpl obj ) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ObjectOutputStream outputStream = new ObjectOutputStream( byteArrayOutputStream );

        outputStream.writeObject( obj );
        outputStream.flush();
        outputStream.close();

        return byteArrayOutputStream;
    }
}
