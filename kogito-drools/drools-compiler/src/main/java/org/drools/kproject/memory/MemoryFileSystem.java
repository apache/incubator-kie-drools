package org.drools.kproject.memory;

import org.drools.commons.jci.readers.ResourceReader;
import org.drools.commons.jci.stores.ResourceStore;
import org.drools.kproject.File;
import org.drools.kproject.FileSystem;
import org.drools.kproject.Folder;
import org.drools.kproject.Path;
import org.drools.kproject.Resource;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class MemoryFileSystem implements FileSystem, ResourceReader, ResourceStore {
    
    private MemoryFolder folder;
    
    private Map<String, Set<Resource>> folders;
    
    private Map<String, byte[]> fileContents;
    
    public MemoryFileSystem() {        
        folders = new HashMap<String, Set<Resource>>();
        fileContents = new HashMap<String, byte[]>();
        
        folder = new MemoryFolder( this, "" );
        folders.put( "", new HashSet<Resource>() );     
    }

    public Folder getRootFolder() {
        return folder;
    }
    
    public File getFile(Path path) {
        return getFile(path.toPortableString());
    }
    
    public File getFile(String path) {
        int lastSlashPos = path.lastIndexOf( '/' );
        if ( lastSlashPos >=0 ) {
            Folder folder = getFolder( path.substring( 0, lastSlashPos ) );
            String name = path.substring( lastSlashPos + 1 );
            return new MemoryFile( this, name, folder );
        } else {
            // path is already at root
            Folder folder = getRootFolder();
            return new MemoryFile( this, path, folder ); 
        }
        
    }    
    
    public Folder getFolder(Path path) {
        return new MemoryFolder( this, path.toPortableString() );
    }

    public Folder getFolder(String path) {
        return new MemoryFolder( this, path );
    }
    
    public Set<? extends Resource> getMembers(Folder folder) {
        return folders.get( folder.getPath().toPortableString() );
    }
    
    public byte[] getFileContents(MemoryFile file) {
        return fileContents.get( file.getPath().toPortableString() );
    }
    
    public void setFileContents(MemoryFile file, byte[] contents) throws IOException {
        if ( !existsFolder( file.getFolder().getPath().toPortableString() )) {
            throw new IOException( "Folder  does not exist, cannot write contents" );    
        }
        
        fileContents.put( file.getPath().toPortableString(), contents );
        
        folders.get( file.getFolder().getPath().toPortableString() ).add( file );
        
    }
    
    public boolean existsFolder(String path) {
        return folders.get( path ) != null;
    }
    
    public boolean existsFile(String path) {
        return fileContents.containsKey( path );
    }    
    
    public void createFolder(MemoryFolder folder) {
        if ( existsFolder( folder.getParent().getPath().toPortableString() ) && 
                !existsFolder( folder.getPath().toPortableString() ) ) {
            folders.put( folder.getPath().toPortableString(), new HashSet<Resource>() );
            
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
        //for( Resource res : members ) {
            if ( res instanceof Folder ) {
                remove( folders.get( res.getPath().toPortableString() ) );
                //folders.remove( folder.getPath().toPortableString() );
            } else {
                fileContents.remove( res.getPath().toPortableString() );
            }
            it.remove();
        }
    }
    
    public boolean remove(File file) {
        if ( file.exists() ) {
            fileContents.remove( file.getPath().toPortableString() );          
            folders.get( ((MemoryFile)file).getFolder().getPath().toPortableString() ).remove( file );
            return true;
        } else {
            return false;
        }
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

    public boolean isAvailable(String pResourceName) {
        return existsFile(pResourceName);
    }

    public byte[] getBytes(String pResourceName) {
        return getFileContents((MemoryFile) getFile(pResourceName));
    }

    public void write(String pResourceName,
                      byte[] pResourceData) {
        try {
            setFileContents( ( MemoryFile ) getFile( pResourceName ), pResourceData );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        
    }

    public byte[] read(String pResourceName) {
        return getBytes(pResourceName);
    }

    public void remove(String pResourceName) {
        throw new UnsupportedOperationException();
    }

    public java.io.File writeAsJar(java.io.File folder, String jarName) {
        ZipOutputStream out = null;
        try {
            java.io.File jarFile = new java.io.File( folder, jarName + ".jar" );
            out = new ZipOutputStream( new FileOutputStream(jarFile) );

            writeJarEntries( getRootFolder(), out );
            out.close();

            return jarFile;
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        } finally {
            try {
                out.close();
            } catch (IOException e) { }
        }
    }

    private void writeJarEntries(Folder f, ZipOutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        for ( Resource rs : f.getMembers() ) {
            if ( rs instanceof Folder ) {
                writeJarEntries( (Folder) rs, out );
            } else {
                out.putNextEntry( new ZipEntry( rs.getPath().toPortableString() ) );

                byte[] contents = getFileContents( (MemoryFile) rs );

                ByteArrayInputStream bais = new ByteArrayInputStream( contents );

                int len;
                while ( (len = bais.read( buf )) > 0 ) {
                    out.write( buf, 0, len );
                }

                out.closeEntry();
                bais.close();
            }
        }
    }

    public static MemoryFileSystem readFromJar(java.io.File jarFile) {
        MemoryFileSystem mfs = new MemoryFileSystem();
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(jarFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                int separator = entry.getName().lastIndexOf('/');
                String path = entry.getName().substring(0, separator);
                String name = entry.getName().substring(separator + 1);

                Folder folder = mfs.getFolder(path);
                folder.create();

                File file = folder.getFile( name );
                file.create( zipFile.getInputStream(entry) );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return mfs;
    }
}
