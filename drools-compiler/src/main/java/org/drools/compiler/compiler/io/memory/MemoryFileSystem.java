package org.drools.compiler.compiler.io.memory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
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

import org.drools.compiler.commons.jci.readers.ResourceReader;
import org.drools.compiler.commons.jci.stores.ResourceStore;
import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.FileSystem;
import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.Path;
import org.drools.compiler.compiler.io.Resource;
import org.drools.core.util.IoUtils;
import org.drools.core.util.StringUtils;

public class MemoryFileSystem
    implements
    FileSystem,
    ResourceReader,
    ResourceStore {

    private MemoryFolder               folder;

    private Map<String, Set<Resource>> folders;

    private Map<String, byte[]>        fileContents;

    private Set<String>                modifiedFilesSinceLastMark;

    public MemoryFileSystem() {
        folders = new HashMap<String, Set<Resource>>();
        fileContents = new HashMap<String, byte[]>();

        folder = new MemoryFolder( this,
                                   "" );
        folders.put( "",
                     new HashSet<Resource>() );
    }

    public Folder getRootFolder() {
        return folder;
    }

    public File getFile(Path path) {
        return getFile( path.toPortableString() );
    }

    public Collection<String> getFileNames() {
        return fileContents.keySet();
    }

    public Map<String, byte[]> getMap() {
        return this.fileContents;
    }
    
    public File getFile(String path) {   
        path = MemoryFolder.trimLeadingAndTrailing( path );
        int lastSlashPos = path.lastIndexOf( '/' );
        if ( lastSlashPos >= 0 ) {
            Folder folder = getFolder( path.substring( 0,
                                                       lastSlashPos ) );
            String name = path.substring( lastSlashPos + 1 );
            return new MemoryFile( this,
                                   name,
                                   folder );
        } else {
            // path is already at root
            Folder folder = getRootFolder();
            return new MemoryFile( this,
                                   path,
                                   folder );
        }

    }

    public Folder getFolder(Path path) {
        return new MemoryFolder( this,
                                 path.toPortableString() );
    }

    public Folder getFolder(String path) {
        return new MemoryFolder( this,
                                 path );
    }

    public Set< ? extends Resource> getMembers(Folder folder) {
        return folders.get( folder.getPath().toPortableString() );
    }

    public byte[] getFileContents(MemoryFile file) {
        return fileContents.get( file.getPath().toPortableString() );
    }

    public void setFileContents(MemoryFile file,
                                byte[] contents) throws IOException {
        if ( !existsFolder( (MemoryFolder) file.getFolder() ) ) {
            createFolder( (MemoryFolder) file.getFolder() );
        }

        String fileName = file.getPath().toPortableString();
        if (modifiedFilesSinceLastMark != null) {
            byte[] oldContent = fileContents.get( fileName );
            if (oldContent == null || !Arrays.equals(oldContent, contents)) {
                modifiedFilesSinceLastMark.add(fileName);
            }
        }
        fileContents.put( fileName,
                          contents );

        folders.get( file.getFolder().getPath().toPortableString() ).add( file );

    }

    public void mark() {
        modifiedFilesSinceLastMark = new HashSet<String>();
    }

    public Collection<String> getModifiedResourcesSinceLastMark() {
        return modifiedFilesSinceLastMark;
    }

    public boolean existsFolder(MemoryFolder folder) {
        return existsFolder( folder.getPath().toPortableString() ); 
    }

    public boolean existsFolder(String path) {
        return folders.get( path ) != null;
    }

    public boolean existsFile(String path) {
        return fileContents.containsKey( path );
    }

    public void createFolder(MemoryFolder folder) {                
        // create current, if it does not exist.
        if ( !existsFolder( folder ) ) {
            // create parent if it does not exist
            if ( !existsFolder( ( MemoryFolder) folder.getParent() ) ) {
                createFolder( (MemoryFolder) folder.getParent() );
            }
            
            folders.put( folder.getPath().toPortableString(),
                         new HashSet<Resource>() );

            Folder parent = folder.getParent();
            folders.get( parent.getPath().toPortableString() ).add( folder );
        }
    }

    public boolean remove(Folder folder) {
        if ( folder.exists() ) {
            remove( folders.get( folder.getPath().toPortableString() ) );
            folders.remove( folder.getPath().toPortableString() );
            return true;
        } else {
            return false;
        }
    }

    public void remove(Set<Resource> members) {
        for ( Iterator<Resource> it = members.iterator(); it.hasNext(); ) {
            Resource res = it.next();
            if ( res instanceof Folder ) {
                remove( folders.get( res.getPath().toPortableString() ) );
            } else {
                String fileName = res.getPath().toPortableString();
                fileContents.remove( fileName );
                if (modifiedFilesSinceLastMark != null) {
                    modifiedFilesSinceLastMark.add( fileName );
                }
            }
            it.remove();
        }
    }

    public boolean remove(File file) {
        if ( file.exists() ) {
            String fileName = file.getPath().toPortableString();
            fileContents.remove( fileName );
            if (modifiedFilesSinceLastMark != null) {
                modifiedFilesSinceLastMark.add( fileName );
            }
            folders.get( ((MemoryFile) file).getFolder().getPath().toPortableString() ).remove( file );
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
            for ( Resource rs : srcFolder.getMembers() ) {
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
                            trgMfs.setFileContents( trgFile,
                                                    srcMfs.getFileContents( (MemoryFile) rs ) );
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
        result = prime * result + ((fileContents == null) ? 0 : fileContents.hashCode());
        result = prime * result + ((folder == null) ? 0 : folder.hashCode());
        result = prime * result + ((folders == null) ? 0 : folders.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        MemoryFileSystem other = (MemoryFileSystem) obj;
        if ( fileContents == null ) {
            if ( other.fileContents != null ) return false;
        } else if ( !fileContents.equals( other.fileContents ) ) return false;
        if ( folder == null ) {
            if ( other.folder != null ) return false;
        } else if ( !folder.equals( other.folder ) ) return false;
        if ( folders == null ) {
            if ( other.folders != null ) return false;
        } else if ( !folders.equals( other.folders ) ) return false;
        return true;
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
        for ( Resource rs : f.getMembers() ) {
            out.println( rs );
            if ( rs instanceof Folder ) {
                printFs( (Folder) rs,
                         out );
            } else {
                out.println( new String( getFileContents( (MemoryFile) rs ) ) );
            }
        }
    }

    public boolean isAvailable(String pResourceName) {
        return existsFile( pResourceName );
    }

    public byte[] getBytes(String pResourceName) {
        return getFileContents((MemoryFile) getFile(pResourceName));
    }

    public void write(String pResourceName,
                      byte[] pResourceData) {
        write( pResourceName,
               pResourceData,
               false );
    }

    public void write(String pResourceName,
                      byte[] pResourceData,
                      boolean createFolder) {
        MemoryFile memoryFile = (MemoryFile) getFile( pResourceName );
        if ( createFolder ) {
            String folderPath = memoryFile.getFolder().getPath().toPortableString();
            if ( !existsFolder( folderPath ) ) {
                memoryFile.getFolder().create();
            }
        }
        try {
            setFileContents( memoryFile,
                             pResourceData );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public byte[] read(String pResourceName) {
        return getBytes( pResourceName );
    }

    public void remove(String pResourceName) {
        remove(getFile(pResourceName));
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
            }
        }
    }
    
    public void writeAsFs(java.io.File file) {
        file.mkdir();
        writeAsFs(this.getRootFolder(), file);
    }    
    
    public void writeAsFs(Folder f,
                          java.io.File file1) {
        for ( Resource rs : f.getMembers() ) {
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
        for ( Resource rs : f.getMembers() ) {
            String rname = rs.getPath().toPortableString();
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
                out.write( contents );
                out.closeEntry();
            }
        }
    }

    public static MemoryFileSystem readFromJar(java.io.File jarFile) {
        MemoryFileSystem mfs = new MemoryFileSystem();
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile( jarFile );
            Enumeration< ? extends ZipEntry> entries = zipFile.entries();
            while ( entries.hasMoreElements() ) {
                ZipEntry entry = entries.nextElement();
                int separator = entry.getName().lastIndexOf( '/' );
                String path = entry.getName().substring( 0,
                                                         separator );
                String name = entry.getName().substring( separator + 1 );

                Folder folder = mfs.getFolder( path );
                folder.create();

                File file = folder.getFile( name );
                file.create( zipFile.getInputStream( entry ) );
            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        } finally {
            if ( zipFile != null ) {
                try {
                    zipFile.close();
                } catch ( IOException e ) { }
            }
        }
        return mfs;
    }
    
    public static MemoryFileSystem readFromJar(byte[] jarFile) {
        return readFromJar( new ByteArrayInputStream( jarFile ) );
    }
    
    public static MemoryFileSystem readFromJar(InputStream jarFile) {
        MemoryFileSystem mfs = new MemoryFileSystem();
        JarInputStream zipFile = null;
        try {
            zipFile = new JarInputStream( jarFile );
            ZipEntry entry = null;
            while ( (entry = zipFile.getNextEntry()) != null ) {
                // entry.getSize() is not accurate according to documentation, so have to read bytes until -1 is found
                ByteArrayOutputStream content = new ByteArrayOutputStream();
                int b = -1;
                while( (b = zipFile.read()) != -1 ) {
                    content.write( b );
                }
                mfs.write( entry.getName(), content.toByteArray(), true );
            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        } finally {
            if ( zipFile != null ) {
                try {
                    zipFile.close();
                } catch ( IOException e ) { }
            }
        }
        return mfs;
    }
    
    public String findPomProperties() {
        for( Entry<String, byte[]> content : fileContents.entrySet() ) {
            if ( content.getKey().endsWith( "pom.properties" ) && content.getKey().startsWith( "META-INF/maven/" ) ) {
                return StringUtils.readFileAsString( new InputStreamReader( new ByteArrayInputStream( content.getValue() ) ) );
            }
        }
        return null;
    }

    public MemoryFileSystem clone() {
        MemoryFileSystem clone = new MemoryFileSystem();
        for (Map.Entry<String, byte[]> entry : fileContents.entrySet()) {
            clone.write(entry.getKey(), entry.getValue());
        }
        return clone;
    }
}
