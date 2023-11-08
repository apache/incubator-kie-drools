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
package org.drools.compiler.compiler.io.memory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.FileSystem;
import org.drools.compiler.compiler.io.FileSystemItem;
import org.drools.compiler.compiler.io.Folder;
import org.drools.io.ByteArrayResource;
import org.drools.io.InternalResource;
import org.drools.util.IoUtils;
import org.drools.util.PortablePath;
import org.drools.util.StringUtils;
import org.kie.api.io.Resource;
import org.kie.memorycompiler.resources.ResourceReader;
import org.kie.memorycompiler.resources.ResourceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.util.PortablePath.ROOT_PATH;

public class MemoryFileSystem
    implements
    FileSystem,
    ResourceReader,
    Serializable,
    ResourceStore {

    private static final Logger log = LoggerFactory.getLogger( MemoryFileSystem.class );

    private final MemoryFolder               folder;

    private final Map<PortablePath, Set<FileSystemItem>> folders = new HashMap<>();

    private final Map<PortablePath, Folder>        folderMap = new HashMap<>();

    private final Map<PortablePath, InternalResource> fileContents = new HashMap<>();

    private Set<String>                      modifiedFilesSinceLastMark;

    public MemoryFileSystem() {
        folder = new MemoryFolder( this, ROOT_PATH );
        folders.put( ROOT_PATH, new HashSet<>() );
    }

    public Folder getRootFolder() {
        return folder;
    }

    public Collection<PortablePath> getFilePaths() {
        return fileContents.keySet();
    }

    public Map<PortablePath, byte[]> getMap() {
        Map<PortablePath, byte[]> bytesMap = new HashMap<>();
        for (Entry<PortablePath, InternalResource> kv : fileContents.entrySet() ) {
            bytesMap.put( kv.getKey(), resourceToBytes( kv.getValue() ) );
        }
        return bytesMap;
    }

    private byte[] resourceToBytes(Resource resource) {
        return resource != null ? (( InternalResource )resource).getBytes() : null;
    }
    
    public File getFile(PortablePath path) {
        PortablePath parent = path.getParent();
        return new MemoryFile( this, path.getFileName(), parent != null ? getFolder( parent ) : getRootFolder() );
    }

    @Override
    public Folder getFolder(PortablePath path) {
        return folderMap.computeIfAbsent(path, p -> new MemoryFolder( this, p ));
    }

    public Set< ? extends FileSystemItem> getMembers( Folder folder) {
        return folders.get( folder.getPath() );
    }

    public byte[] getFileContents(MemoryFile file) {
        return resourceToBytes( getResource(file) );
    }

    public InternalResource getResource(MemoryFile file) {
        return fileContents.get( file.getPath() );
    }

    public void setFileContents(MemoryFile file, byte[] contents) throws IOException {
        setFileContents(file, new ByteArrayResource( contents ));
    }

    public void setFileContents(MemoryFile file, Resource resource) throws IOException {
        if ( !existsFolder( (MemoryFolder) file.getFolder() ) ) {
            createFolder( (MemoryFolder) file.getFolder() );
        }

        PortablePath filePath = file.getPath();
        if (modifiedFilesSinceLastMark != null) {
            byte[] contents = resourceToBytes( resource );
            byte[] oldContent = resourceToBytes( fileContents.get( filePath ) );
            if (oldContent == null || !Arrays.equals(oldContent, contents)) {
                modifiedFilesSinceLastMark.add(filePath.asString());
            }
        }
        fileContents.put( filePath, (InternalResource) resource );
        resource.setSourcePath( file.getPath().asString() );
        folders.get( file.getFolder().getPath() ).add( file );
    }

    public void mark() {
        modifiedFilesSinceLastMark = new HashSet<>();
    }

    public Collection<String> getModifiedResourcesSinceLastMark() {
        return modifiedFilesSinceLastMark;
    }

    public boolean existsFolder(MemoryFolder folder) {
        return existsFolder( folder.getPath() );
    }

    public boolean existsFolder(PortablePath path) {
        return folders.get(path) != null;
    }

    public boolean existsFile(PortablePath path) {
        return fileContents.containsKey(path);
    }

    public void createFolder(MemoryFolder folder) {
        // create current, if it does not exist.
        if ( !existsFolder( folder ) ) {
            // create parent if it does not exist
            if ( !existsFolder( ( MemoryFolder) folder.getParent() ) ) {
                createFolder( (MemoryFolder) folder.getParent() );
            }

            folders.put( folder.getPath(), new HashSet<>() );

            Folder parent = folder.getParent();
            folders.get( parent.getPath() ).add( folder );
        }
    }

    public boolean remove(Folder folder) {
        if ( folder.exists() ) {
            remove( folders.get( folder.getPath() ) );
            folders.remove( folder.getPath() );
            return true;
        } else {
            return false;
        }
    }

    public void remove(Set<FileSystemItem> members) {
        for (Iterator<FileSystemItem> it = members.iterator(); it.hasNext(); ) {
            FileSystemItem res = it.next();
            if ( res instanceof Folder ) {
                remove( folders.get( res.getPath() ) );
            } else {
                PortablePath filePath = res.getPath();
                fileContents.remove( filePath );
                if (modifiedFilesSinceLastMark != null) {
                    modifiedFilesSinceLastMark.add( filePath.asString() );
                }
            }
            it.remove();
        }
    }

    public boolean remove(File file) {
        if ( file.exists() ) {
            PortablePath filePath = file.getPath();
            fileContents.remove( filePath );
            if (modifiedFilesSinceLastMark != null) {
                modifiedFilesSinceLastMark.add( filePath.asString() );
            }
            folders.get( ((MemoryFile) file).getFolder().getPath() ).remove( file );
            return true;
        } else {
            return false;
        }
    }

    public int copyFolder(Folder srcFolder,
                          MemoryFileSystem trgMfs,
                          Folder trgFolder,
                          String... filters) {
        return copyFolder( this,
                           srcFolder,
                           trgMfs,
                           trgFolder,
                           0,
                           filters );
    }

    private static int copyFolder(MemoryFileSystem srcMfs,
                                  Folder srcFolder,
                                  MemoryFileSystem trgMfs,
                                  Folder trgFolder,
                                  int count,
                                  String... filters) {
        if ( !trgFolder.exists() ) {
            trgMfs.getFolder( trgFolder.getPath() ).create();
        }

        if ( srcFolder != null ) {
            for ( FileSystemItem rs : srcFolder.getMembers() ) {
                if ( rs instanceof Folder ) {
                    count = copyFolder( srcMfs,
                                        (Folder) rs,
                                        trgMfs,
                                        trgFolder.getFolder( ((Folder) rs).getName() ),
                                        count,
                                        filters );
                } else {
                    MemoryFile trgFile = (MemoryFile) trgFolder.getFile( ((org.drools.compiler.compiler.io.File) rs).getName() );
                    boolean accept = false;

                    if ( filters == null || filters.length == 0 ) {
                        accept = true;
                    } else {
                        for ( String filter : filters ) {
                            if ( trgFile.getName().endsWith( filter ) ) {
                                accept = true;
                                break;
                            }
                        }
                    }

                    if ( accept ) {
                        try {
                            trgMfs.setFileContents( trgFile, srcMfs.getResource( (MemoryFile) rs ) );
                            count++;
                        } catch ( IOException e ) {
                            throw new RuntimeException( e );
                        }
                    }
                }
            }
        }
        return count;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + fileContents.hashCode();
        result = prime * result + ((folder == null) ? 0 : folder.hashCode());
        result = prime * result + folders.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        MemoryFileSystem other = (MemoryFileSystem) obj;

        if ( folder == null ) {
            if ( other.folder != null ) {
                return false;
            }
        } else if ( !folder.equals( other.folder ) ) {
            return false;
        }

        return fileContents.equals( other.fileContents ) && folders.equals( other.folders );
    }

    @Override
    public String toString() {
        return "MemoryFileSystem [folder=" + folder + ", folders=" + folders + ", fileContents=" + fileContents + "]";
    }

    public void printFs(PrintStream out) {
        printFs( getRootFolder(),
                 out );

    }

    public void printFs(Folder f,
                        PrintStream out) {
        for ( FileSystemItem rs : f.getMembers() ) {
            out.println( rs );
            if ( rs instanceof Folder ) {
                printFs( (Folder) rs,
                         out );
            } else {
                out.println( new String( getFileContents( (MemoryFile) rs ), IoUtils.UTF8_CHARSET ) );
            }
        }
    }

    @Override
    public boolean isAvailable(PortablePath resourcePath) {
        return existsFile( resourcePath );
    }

    @Override
    public byte[] getBytes(PortablePath resourcePath) {
        return getFileContents((MemoryFile) getFile(resourcePath));
    }

    public InternalResource getResource(PortablePath resourcePath) {
        return getResource((MemoryFile) getFile(resourcePath));
    }

    @Override
    public void write(PortablePath resourcePath, byte[] pResourceData) {
        write( resourcePath, pResourceData, false );
    }

    @Override
    public void write(PortablePath resourcePath, byte[] pResourceData, boolean createFolder) {
        write( resourcePath, new ByteArrayResource( pResourceData ), createFolder );
    }

    public void write(PortablePath resourcePath, Resource resource) {
        write( resourcePath, resource, false );
    }

    public void write(PortablePath resourcePath, Resource resource, boolean createFolder) {
        MemoryFile memoryFile = (MemoryFile) getFile( resourcePath );
        if ( createFolder ) {
            PortablePath folderPath = memoryFile.getFolder().getPath();
            if ( !existsFolder( folderPath ) ) {
                memoryFile.getFolder().create();
            }
        }
        try {
            setFileContents( memoryFile, resource );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public byte[] read(PortablePath resourcePath) {
        return getBytes( resourcePath );
    }

    @Override
    public void remove(PortablePath resourcePath) {
        remove(getFile(resourcePath));
    }

    public byte[] writeAsBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        zip( baos );
        return baos.toByteArray();
    }

    public java.io.File writeAsJar(java.io.File folder,
                                   String jarName) {
        try {
            java.io.File jarFile = new java.io.File( folder,
                                                     jarName + ".jar" );
            System.out.println( jarFile );
            zip( new FileOutputStream( jarFile ) );
            return jarFile;
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    private void zip(OutputStream outputStream) {
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream( outputStream );

            writeJarEntries( getRootFolder(),
                             out );
            out.close();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch ( IOException e ) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void writeAsFs(java.io.File file) {
        file.mkdir();
        writeAsFs(this.getRootFolder(), file);
    }

    public void writeAsFs(Folder f,
                          java.io.File file1) {
        for ( FileSystemItem rs : f.getMembers() ) {
            if ( rs instanceof Folder ) {
                java.io.File file2 = new java.io.File( file1, ((Folder) rs).getName());
                file2.mkdir();
                writeAsFs( (Folder) rs, file2 );
            } else {
                byte[] bytes = getFileContents( (MemoryFile) rs );

                try {
                    IoUtils.write(new java.io.File(file1, ((File) rs).getName()), bytes);
                } catch ( IOException e ) {
                    throw new RuntimeException("Unable to write project to file system\n", e);
                }
            }
        }
    }

    private void writeJarEntries(Folder f,
                                 ZipOutputStream out) throws IOException {
        for ( FileSystemItem rs : f.getMembers() ) {
            String rname = rs.getPath().asString();
            if ( rs instanceof Folder ) {
                rname = rname.endsWith("/") ? rname : rname + "/"; // a folder name must end with / according to ZIP spec
                ZipEntry entry = new ZipEntry( rname );
                out.putNextEntry( entry );

                writeJarEntries( (Folder) rs,
                                 out );
            } else {
                ZipEntry entry = new ZipEntry( rname );
                out.putNextEntry( entry );

                byte[] contents = getFileContents( (MemoryFile) rs );
                if (contents == null) {
                    IOException e = new IOException("No content found for: " + rs);
                    log.error(e.getMessage(), e);
                    throw e;
                }
                out.write( contents );
                out.closeEntry();
            }
        }
    }

    public static MemoryFileSystem readFromJar(java.io.File jarFile) {
        MemoryFileSystem mfs = new MemoryFileSystem();
        try ( ZipFile zipFile = new ZipFile( jarFile ) ) {
            Enumeration< ? extends ZipEntry> entries = zipFile.entries();
            while ( entries.hasMoreElements() ) {
                ZipEntry entry = entries.nextElement();
                int separator = entry.getName().lastIndexOf( '/' );
                PortablePath path = separator > 0 ? PortablePath.of( entry.getName().substring( 0, separator ) ) : ROOT_PATH;
                String name = entry.getName().substring( separator + 1 );

                Folder folder = mfs.getFolder( path );
                folder.create();

                File file = folder.getFile( name );
                file.create( zipFile.getInputStream( entry ) );
            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        return mfs;
    }

    public static MemoryFileSystem readFromJar(byte[] jarFile) {
        return readFromJar( new ByteArrayInputStream( jarFile ) );
    }

    public static MemoryFileSystem readFromJar(InputStream jarFile) {
        MemoryFileSystem mfs = new MemoryFileSystem();
        try (JarInputStream zipFile = new JarInputStream( jarFile )) {
            ZipEntry entry;
            while ( (entry = zipFile.getNextEntry()) != null ) {
                if (entry.isDirectory()) {
                    continue;
                }
                // entry.getSize() is not accurate according to documentation, so have to read bytes until -1 is found
                ByteArrayOutputStream content = new ByteArrayOutputStream();
                int b;
                while( (b = zipFile.read()) != -1 ) {
                    content.write( b );
                }
                mfs.write( entry.getName(), content.toByteArray(), true );
            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        return mfs;
    }

    public String findPomProperties() {
        for( Entry<PortablePath, InternalResource> content : fileContents.entrySet() ) {
            if ( content.getKey().endsWith( "pom.properties" ) && content.getKey().startsWith( "META-INF/maven/" ) ) {
                try (InputStream resourceStream = content.getValue().getInputStream()) {
                    return StringUtils.readFileAsString( new InputStreamReader( resourceStream, IoUtils.UTF8_CHARSET ) );
                } catch (IOException ioe) {
                    throw new RuntimeException( ioe );
                }
            }
        }
        return null;
    }

    public MemoryFileSystem clone() {
        MemoryFileSystem clone = new MemoryFileSystem();
        for (Map.Entry<PortablePath, InternalResource> entry : fileContents.entrySet()) {
            clone.write(entry.getKey(), entry.getValue());
        }
        return clone;
    }

    public ClassLoader memoryClassLoader(ClassLoader parent) {
        return new ByteClassLoader(parent, this);
    }

    static class ByteClassLoader extends URLClassLoader {
        private final Map<String, byte[]> extraClassDefs = new HashMap<>();

        public ByteClassLoader(ClassLoader parent, MemoryFileSystem memoryFileSystem) {
            super(new URL[0], parent);
            memoryFileSystem
                    .getFilePaths()
                    .stream()
                    .filter(fn -> fn.endsWith(".class"))
                    .forEach(f -> {
                        MemoryFile file = (MemoryFile) memoryFileSystem.getFile(f);
                        byte[] fileContents = memoryFileSystem.getFileContents(file);
                        extraClassDefs.put(f.asClassName(), fileContents);
                    });
        }

        @Override
        protected Class<?> findClass(final String name) throws ClassNotFoundException {
            byte[] classBytes = this.extraClassDefs.remove(name);
            if (classBytes != null) {
                return defineClass(name, classBytes, 0, classBytes.length);
            }
            return super.findClass(name);
        }

    }

}
