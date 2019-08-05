/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.io.impl;

import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import org.drools.core.io.internal.InternalResource;
import org.drools.core.util.IoUtils;
import org.drools.core.util.StringUtils;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

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

    private static final int    DEFAULT_BUFFER_SIZE      = 1024 * 4;

    public static final File    CACHE_DIR                = getCacheDir();

    private URL                 url;
    private long                lastRead                 = -1;
    private static final String DROOLS_RESOURCE_URLCACHE = "drools.resource.urlcache";
    private String              basicAuthentication      = "disabled";
    private String              username                 = "";
    private String              password                 = "";
    private String              encoding;

    private static final String DROOLS_RESOURCE_URLTIMEOUT = "drools.resource.urltimeout";
    private static final int DEFAULT_TIMEOUT = 10000; // 10 seconds
    private static final int TIMEOUT = initTimeout();

    public UrlResource() {

    }

    public UrlResource(final URL url) {
        this.url = getCleanedUrl(url,
                url.toString());
        setSourcePath(this.url.getPath());
        setResourceType(ResourceType.determineResourceType(this.url.getPath()));
    }

    public UrlResource(final URL url, final String encoding) {
        this(url);
        this.encoding = encoding;
    }

    public UrlResource(final String path) {
        try {
            this.url = getCleanedUrl(new URL(path),
                    path);
            setSourcePath(this.url.getPath());
            setResourceType(ResourceType.determineResourceType(this.url.getPath()));
        } catch (final MalformedURLException e) {
            throw new IllegalArgumentException("'" + path + "' path is malformed",
                    e);
        }
    }

    public UrlResource(final String path, final String encoding) {
        this(path);
        this.encoding = encoding;
    }

    public void writeExternal(final ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject(this.url);
        out.writeObject(this.encoding);
    }

    public void readExternal(final ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal( in );
        this.url = (URL) in.readObject();
        this.encoding = (String) in.readObject();
    }

    public String getEncoding() {
        return this.encoding;
    }

    public String getBasicAuthentication() {
        return basicAuthentication;
    }

    public void setBasicAuthentication(final String basicAuthentication) {
        this.basicAuthentication = basicAuthentication;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
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
            long lastMod = grabLastMod();
            if (lastMod == 0 && cacheFileExists()) {
                return fromCache();
            }
            if ((lastMod > 0 && lastMod > lastRead)
                    && (CACHE_DIR != null && (url.getProtocol().equals("http") || url.getProtocol().equals("https")))) {
                //lets grab a copy and cache it in case we need it in future...
                cacheStream();
                lastMod = getCacheFile().lastModified();
                this.lastRead = lastMod;
                return fromCache();
            }
            this.lastRead = lastMod;
            return grabStream();
        } catch (final IOException e) {
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

    private InputStream fromCache() throws FileNotFoundException {
        final File fi = getCacheFile();
        return new FileInputStream(fi);
    }

    private File getCacheFile() {
        try {
            return new File(CACHE_DIR, URLEncoder.encode(this.url.toString(), "UTF-8"));
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private File getTemproralCacheFile() {
        try {
            return new File(CACHE_DIR, URLEncoder.encode(this.url.toString(), "UTF-8") + "_tmp");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save a copy in the local cache - in case remote source is not available in future.
     */
    private void cacheStream() {
        try {
            final File fi = getTemproralCacheFile();
            if (fi.exists() && !fi.delete()) {
                throw new IllegalStateException("Cannot delete file " + fi.getAbsolutePath() + "!");
            }
            try (final FileOutputStream fout = new FileOutputStream(fi);
                 final InputStream in = grabStream();) {
                final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int n;
                while (-1 != (n = in.read(buffer))) {
                    fout.write(buffer, 0, n);
                }
                fout.flush();
            }
            
            final File cacheFile = getCacheFile();
            if (!fi.renameTo(cacheFile)) {
                throw new IllegalStateException("Cannot rename file \"" + fi.getAbsolutePath() + "\" to \"" + cacheFile.getAbsolutePath() + "\"!");
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private URLConnection openURLConnection(final URL url) throws IOException {
        final URLConnection con = url.openConnection();
        con.setConnectTimeout(TIMEOUT);
        con.setReadTimeout(TIMEOUT);
        return con;
    }

    private InputStream grabStream() throws IOException {
        final URLConnection con = openURLConnection(this.url);
        con.setUseCaches(false);

        if ((con instanceof HttpURLConnection) && ("enabled".equalsIgnoreCase(basicAuthentication))) {
            final String userpassword = username + ":" + password;
            final byte[] authEncBytes = Base64.getEncoder().encode(userpassword.getBytes(IoUtils.UTF8_CHARSET));
            con.setRequestProperty("Authorization",
                                   "Basic " + new String(authEncBytes, IoUtils.UTF8_CHARSET));
        }

        return con.getInputStream();
    }

    public Reader getReader() throws IOException {
        if (this.encoding != null) {
            return new InputStreamReader( getInputStream(), this.encoding );
        } else {
            return new InputStreamReader( getInputStream(), IoUtils.UTF8_CHARSET );
        }
    }

    /**
     * Determine a cleaned URL for the given original URL.
     * @param originalUrl the original URL
     * @param originalPath the original URL path
     * @return the cleaned URL
     */
    private URL getCleanedUrl(final URL originalUrl,
                              final String originalPath) {
        try {
            return new URL(StringUtils.cleanPath(originalPath));
        } catch (final MalformedURLException ex) {
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
            return new File(StringUtils.toURI(url.toString()).getSchemeSpecificPart());
        } catch (final Exception e) {
            throw new RuntimeException("Unable to get File for url " + this.url, e);
        }
    }

    public long getLastModified() {
        try {
            final long lm = grabLastMod();
            //try the cache.
            if (lm == 0 && cacheFileExists()) {
                //OK we will return it from the local cached copy, as remote one isn't available..
                return getCacheFile().lastModified();
            }
            return lm;
        } catch (final IOException e) {
            //try the cache...
            if (cacheFileExists()) {
                //OK we will return it from the local cached copy, as remote one isn't available..
                return getCacheFile().lastModified();
            } else {
                throw new RuntimeException("Unable to get LastModified for ClasspathResource",
                        e);
            }
        }
    }

    private long grabLastMod() throws IOException {
        // use File if possible, as http rounds milliseconds on some machines, this fine level of granularity is only really an issue for testing
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4504473
        if ("file".equals(url.getProtocol())) {
            final File file = getFile();
            return file.lastModified();
        } else {
            final URLConnection conn = openURLConnection(getURL());
            if (conn instanceof HttpURLConnection) {
                ((HttpURLConnection) conn).setRequestMethod("HEAD");
                if ("enabled".equalsIgnoreCase(basicAuthentication)) {
                    final String userpassword = username + ":" + password;
                    final byte[] authEncBytes = Base64.getEncoder().encode(userpassword.getBytes(IoUtils.UTF8_CHARSET) );

                    ((HttpURLConnection) conn).setRequestProperty("Authorization",
                            "Basic " + new String(authEncBytes, IoUtils.UTF8_CHARSET));
                }
            }
            long date = conn.getLastModified();
            if (date == 0) {
                try {
                    date = Long.parseLong(conn.getHeaderField("lastModified"));
                } catch (final Exception e) { /* well, we tried ... */
                }
            }
            return date;
        }
    }

    public long getLastRead() {
        return this.lastRead;
    }

    public boolean isDirectory() {
        try {
            final URL thisUrl = getURL();

            if ("file".equals(thisUrl.getProtocol())) {

                final File file = new File(StringUtils.toURI(thisUrl.toString()).getSchemeSpecificPart());

                return file.isDirectory();
            }
        } catch (final Exception e) {
            // swallow as returned false
        }

        return false;
    }

    public Collection<Resource> listResources() {
        try {
            final URL thisUrl = getURL();

            if ("file".equals(thisUrl.getProtocol())) {
                final File dir = getFile();

                final List<Resource> resources = new ArrayList<>();

                for (final File file : dir.listFiles()) {
                    resources.add(new FileSystemResource(file));
                }

                return resources;
            }
        } catch (final Exception e) {
            // swallow as we'll throw an exception anyway
        }
        throw new RuntimeException("This Resource cannot be listed, or is not a directory");
    }

    /**
     * This implementation compares the underlying URL references.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        return (obj == this || (obj instanceof UrlResource && this.url.equals(((UrlResource) obj).url)));
    }

    /**
     * This implementation returns the hash code of the underlying URL reference.
     */
    @Override
    public int hashCode() {
        return this.url.hashCode();
    }

    @Override
    public String toString() {
        return "UrlResource[path=" + this.url.toString() + "]";
    }

    private static File getCacheDir() {
        final String root = System.getProperty(DROOLS_RESOURCE_URLCACHE, "NONE");
        if (root.equals("NONE")) {
            return null;
        } else {
            return new File(root);
        }
    }

    private static int initTimeout() {
        try {
            return Integer.parseInt(System.getProperty( DROOLS_RESOURCE_URLTIMEOUT ));
        } catch (final Exception e) {
            return DEFAULT_TIMEOUT;
        }
    }
}
