package org.drools.io.impl;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.io.InternalResource;
import org.drools.io.Resource;
import org.drools.util.StringUtils;

import com.sun.net.ssl.HttpsURLConnection;

/**
 * Borrowed gratuitously from Spring under ASL2.0.
 *
 */
public class UrlResource extends BaseResource
    implements
    InternalResource,
    Externalizable {
    private URL  url;
    private long lastRead = -1;

    public UrlResource() {

    }

    public UrlResource(URL url) {
        this.url = getCleanedUrl( url,
                                  url.toString() );
    }

    public UrlResource(String path) {
        try {
            this.url = getCleanedUrl( new URL( path ),
                                      path );
        } catch ( MalformedURLException e ) {
            throw new IllegalArgumentException( "'" + path + "' path is malformed",
                                                e );
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.url );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.url = (URL) in.readObject();
    }

    /**
     * This implementation opens an InputStream for the given URL.
     * It sets the "UseCaches" flag to <code>false</code>,
     * mainly to avoid jar file locking on Windows.
     * @see java.net.URL#openConnection()
     * @see java.net.URLConnection#setUseCaches(boolean)
     * @see java.net.URLConnection#getInputStream()
     */
    public InputStream getInputStream() throws IOException {
        this.lastRead = getLastModified();
        URLConnection con = this.url.openConnection();
        con.setUseCaches( false );
        return con.getInputStream();
    }

    public Reader getReader() throws IOException {
        return new InputStreamReader( getInputStream() );
    }

    /**
     * Determine a cleaned URL for the given original URL.
     * @param originalUrl the original URL
     * @param originalPath the original URL path
     * @return the cleaned URL
     * @see org.springframework.util.StringUtils#cleanPath
     */
    private URL getCleanedUrl(URL originalUrl,
                              String originalPath) {
        try {
            return new URL( StringUtils.cleanPath( originalPath ) );
        } catch ( MalformedURLException ex ) {
            // Cleaned URL path cannot be converted to URL
            // -> take original URL.
            return originalUrl;
        }
    }

    public URL getURL() throws IOException {
        return this.url;
    }

    public boolean hasURL() {
        return true;
    }

    public File getFile() throws IOException {
        try {
            return new File( StringUtils.toURI( url.toString() ).getSchemeSpecificPart() );
        } catch ( Exception e ) {
            throw new RuntimeException( "Unable to get File for url " + this.url, e);
        }
    }

    public long getLastModified() {
        try {
            // use File, as http rounds milliseconds on some machines, this fine level of granularity is only really an issue for testing
            // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4504473
            if ( "file".equals( url.getProtocol() ) ) {
                File file = getFile();
                return file.lastModified();
            } else {
                URLConnection conn = getURL().openConnection();
                if ( conn instanceof HttpURLConnection ) {
                    ((HttpURLConnection) conn).setRequestMethod( "HEAD" );
                }
                long date =  conn.getLastModified();
                if (date == 0) {
                     try {
                         date = Long.parseLong(conn.getHeaderField("lastModified"));
                     } catch (Exception e) { /* well, we tried ... */ }
                }
                return date;
            }
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get LastMofified for ClasspathResource",
                                        e );
        }
    }

    public long getLastRead() {
        return this.lastRead;
    }

    public boolean isDirectory() {
        try {
            URL url = getURL();

            if ( "file".equals( url.getProtocol() ) ) {

                File file = new File( StringUtils.toURI( url.toString() ).getSchemeSpecificPart() );

                return file.isDirectory();
            }
        } catch ( Exception e ) {
            // swallow as returned false
        }

        return false;
    }

    public Collection<Resource> listResources() {
        try {
            URL url = getURL();

            if ( "file".equals( url.getProtocol() ) ) {
                File dir = getFile();

                List<Resource> resources = new ArrayList<Resource>();

                for ( File file : dir.listFiles() ) {
                    resources.add( new FileSystemResource( file ) );
                }

                return resources;
            }
        } catch ( Exception e ) {
            // swallow as we'll throw an exception anyway            
        }
        throw new RuntimeException( "This Resource cannot be listed, or is not a directory" );
    }

    /**
     * This implementation compares the underlying URL references.
     */
    public boolean equals(Object obj) {
        if ( obj == null ) {
            return false;
        }
        return (obj == this || (obj instanceof UrlResource && this.url.equals( ((UrlResource) obj).url )));
    }

    /**
     * This implementation returns the hash code of the underlying URL reference.
     */
    public int hashCode() {
        return this.url.hashCode();
    }

    public String toString() {
        return "[UrlResource path='" + this.url.toString() + "']";
    }

}
