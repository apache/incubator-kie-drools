/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.io.impl;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.util.StringUtils;
import org.drools.io.Resource;
import org.drools.io.internal.InternalResource;
import org.drools.util.codec.Base64;



/**
 * Borrowed gratuitously from Spring under ASL2.0.
 *
 * Added in local file cache ability for http and https urls.
 *
 * Set the system property: "drools.resource.urlcache" to a directory which can be written to and read from
 * as a cache - so remote resources will be cached with last known good copies.
 */
public class UrlResource extends BaseResource
    implements
    InternalResource,
    Externalizable {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static File CACHE_DIR = getCacheDir();

    private URL  url;
    private long lastRead = -1;
    private static final String DROOLS_RESOURCE_URLCACHE = "drools.resource.urlcache";
    private String basicAuthentication = "disabled";
    private String username = "";
    private String password = "";

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

    public String getBasicAuthentication() {
		return basicAuthentication;
	}

	public void setBasicAuthentication(String basicAuthentication) {
		this.basicAuthentication = basicAuthentication;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
        try {
            long lastMod  = grabLastMod();
            if (lastMod == 0) {
                //we will try the cache...
                if (cacheFileExists()) return fromCache();
            }
            if (lastMod > 0 && lastMod > lastRead) {
                if (CACHE_DIR != null && url.getProtocol().equals("http") || url.getProtocol().equals("https")) {
                    //lets grab a copy and cache it in case we need it in future...
                    cacheStream();
                }
            }
            this.lastRead = lastMod;
            return grabStream();
        } catch (IOException e) {
            if (cacheFileExists()) {
                return fromCache();
            } else {
                throw e;
            }
        }
    }

    private boolean cacheFileExists() {
        return CACHE_DIR != null && getCacheFile().exists();
    }

    private InputStream fromCache() throws FileNotFoundException, UnsupportedEncodingException {
       File fi = getCacheFile();
       return new FileInputStream(fi);
    }

    private File getCacheFile()  {
        try {
            return new File(CACHE_DIR, URLEncoder.encode(this.url.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save a copy in the local cache - in case remote source is not available in future.
     */
    private void cacheStream() {
        try {
            File fi = getCacheFile();
            if (fi.exists()) fi.delete();
            FileOutputStream fout = new FileOutputStream(fi);
            InputStream in = grabStream();
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int n;
            while (-1 != (n = in.read(buffer))) {
                fout.write(buffer, 0, n);
            }
            fout.flush();
            fout.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private InputStream grabStream() throws IOException {
        URLConnection con = this.url.openConnection();
        con.setUseCaches( false );

        if ( con instanceof HttpURLConnection) {
            if ("enabled".equalsIgnoreCase(basicAuthentication)) {
				String userpassword = username + ":" + password;
				byte[] authEncBytes = Base64.encodeBase64(userpassword.getBytes());

				((HttpURLConnection) con).setRequestProperty("Authorization",
						"Basic " +  new String(authEncBytes));
			}

        }

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
            long lm = grabLastMod();
            //try the cache.
            if (lm == 0 && cacheFileExists()) {
                //OK we will return it from the local cached copy, as remote one isn't available..
                return getCacheFile().lastModified();
            }
            return lm;
        } catch ( IOException e ) {
            //try the cache...
            if (cacheFileExists()) {
                //OK we will return it from the local cached copy, as remote one isn't available..
                return getCacheFile().lastModified();
            } else {
                throw new RuntimeException( "Unable to get LastMofified for ClasspathResource",
                                            e );
            }
        }
    }

    private long grabLastMod() throws IOException {
        // use File, as http rounds milliseconds on some machines, this fine level of granularity is only really an issue for testing
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4504473
        if ( "file".equals( url.getProtocol() ) ) {
            File file = getFile();
            return file.lastModified();
        } else {
            URLConnection conn = getURL().openConnection();
            if ( conn instanceof HttpURLConnection) {
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

    private static File getCacheDir() {
        String root = System.getProperty(DROOLS_RESOURCE_URLCACHE, "NONE");
        if (root.equals("NONE")) {
            return null;
        } else {
            return new File(root);
        }
    }
}
