package org.drools.smf;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class Jarer
{
    private JarOutputStream jos = null;

    /**
     * A Utility class for adding classes in a directory and sub directory to a given jar
     * 
     * @param jar
     *            The jar file to which entries will be added
     * @throws FileNotFoundException
     * @throws IOException
     */
    public Jarer(File jar) throws FileNotFoundException,
                          IOException
    {
        this.jos = new JarOutputStream( new FileOutputStream( jar ) );
    }

    /**
     * A Utility class for adding classes in a directory and sub directory to a given jar
     * 
     * @param jar
     *            The jar file to which entries will be added
     * @param append
     *            Whether to append or overwrite the existing jar
     * @throws FileNotFoundException
     * @throws IOException
     */
    public Jarer(File jar,
                 boolean append) throws FileNotFoundException,
                                IOException
    {
        this.jos = new JarOutputStream( new FileOutputStream( jar,
                                                              append ) );
    }

    /**
     * A Utility class for adding classes in a directory and sub directory to a given jar
     * 
     * @param jar
     *            The jar file to which entries will be added
     * @param append
     *            Whether to append or overwrite the existing jar
     * @throws FileNotFoundException
     * @throws IOException
     */
    public Jarer(ByteArrayOutputStream outputStream) throws FileNotFoundException,
                                                    IOException
    {
        this.jos = new JarOutputStream( outputStream );
    }

    public void close() throws IOException
    {
        this.jos.close();
    }

    public void addObject(String name,
                          Object object) throws IOException
    {
        jos.putNextEntry( new JarEntry( name ) );
        ObjectOutput out = new ObjectOutputStream( this.jos );
        out.writeObject( object );
    }

    /**
     * The directory, and sub directories, whoes .class files will be added to the jar.
     * 
     * @param dirobject
     * @throws IOException
     */
    public void addDirectory(File dir) throws IOException
    {
        addDirectory( dir,
                      dir.toURL().toExternalForm().length() );
    }

    private void addDirectory(File dir,
                              int baseFolderPos) throws IOException
    {
        if ( dir.exists() == true )
        {
            if ( dir.isDirectory() == true )
            {
                File[] fileList = dir.listFiles();
                // Loop through the files
                for ( int i = 0; i < fileList.length; i++ )
                {
                    if ( fileList[i].isDirectory() )
                    {
                        addDirectory( fileList[i],
                                      baseFolderPos );
                    }
                    else if ( fileList[i].isFile() )
                    {
                        addFile( fileList[i],
                                 baseFolderPos );
                    }
                }
            }
            else
            {
                throw new IOException( dir.getAbsolutePath() + " is not a directory." );
            }
        }
        else
        {
            throw new IOException( dir.getAbsolutePath() + " does not exist." );
        }
    }

    public void addFile(File file,
                        String name) throws IOException
    {
        JarEntry entry = new JarEntry( name );
        addFile( file,
                 entry );
    }

    public void addByteArray(byte[] bytes,
                             String name) throws IOException
    {
        jos.putNextEntry( new JarEntry( name ) );
        jos.write( bytes );
    }
    
    public void addCharArray(char[] chars,
                             String name) throws IOException
    {
        addByteArray( new String(chars).getBytes(),
                      name ); 
    }    

    private void addFile(File file,
                         int baseFolderPos) throws IOException
    {
        JarEntry entry = new JarEntry( file.toURL().toExternalForm().substring( baseFolderPos ) );
        addFile( file,
                 entry );
    }

    private void addFile(File file,
                         JarEntry entry) throws IOException
    {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try
        {
            fis = new FileInputStream( file );
            bis = new BufferedInputStream( fis );
            jos.putNextEntry( entry );
            byte[] data = new byte[1024];
            int byteCount;
            while ( (byteCount = bis.read( data,
                                           0,
                                           1024 )) > -1 )
            {
                jos.write( data,
                           0,
                           byteCount );
            }
        }
        finally
        {
            bis.close();
            fis.close();
        }
    }

}