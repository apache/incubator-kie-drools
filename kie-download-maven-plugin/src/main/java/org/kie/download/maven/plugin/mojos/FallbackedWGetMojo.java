/*
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
package org.kie.download.maven.plugin.mojos;

import javax.inject.Inject;

import io.github.download.maven.plugin.internal.DownloadFailureException;
import io.github.download.maven.plugin.internal.FileNameUtils;
import io.github.download.maven.plugin.internal.FilePermissions;
import io.github.download.maven.plugin.internal.HttpCodes;
import io.github.download.maven.plugin.internal.HttpFileRequester;
import io.github.download.maven.plugin.internal.LoggingProgressReport;
import io.github.download.maven.plugin.internal.SSLProtocols;
import io.github.download.maven.plugin.internal.SilentProgressReport;
import io.github.download.maven.plugin.internal.cache.DownloadCache;
import io.github.download.maven.plugin.internal.checksum.Checksums;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.apache.http.Header;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.utils.StringUtils;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.bzip2.BZip2UnArchiver;
import org.codehaus.plexus.archiver.gzip.GZipUnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.snappy.SnappyUnArchiver;
import org.codehaus.plexus.archiver.xz.XZUnArchiver;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.AuthenticationContext;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.transfer.TransferListener;
import org.sonatype.plexus.build.incremental.BuildContext;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;

/**
 * Will download a file from a website using the standard HTTP protocol.
 * It will iterate over the provided urls until the download succeed, or eventually manage the failure as in the original plugin
 */
@Mojo(
    name = "wget", defaultPhase = LifecyclePhase.PROCESS_RESOURCES,
    requiresProject = false, threadSafe = true
)
@SuppressWarnings("checkstyle:ClassFanOutComplexity")
public final class FallbackedWGetMojo extends AbstractMojo {

    /**
     * Http connection pool.
     */
    private static final PoolingHttpClientConnectionManager CONN_POOL;

    /**
     * A map of file caches by their location paths.
     * Ensures one cache instance per path and enables safe execution in parallel
     * builds against the same cache.
     */
    private static final Map<String, DownloadCache> DOWNLOAD_CACHES = new ConcurrentHashMap<>();

    /**
     * A map of file locks by files to be downloaded.
     * Ensures exclusive access to a target file.
     */
    private static final Map<String, Lock> FILE_LOCKS = new ConcurrentHashMap<>();

    /**
     * Represent the URLs to fetch information from.
     */
    @Parameter(alias = "urls", property = "download.urls", required = true)
    private List<URI> uris;

    /**
     * Flag to overwrite the file by redownloading it.
     * {@code overwrite=true} means that if the target file pre-exists
     * at the expected target-location for the current plugin execution,
     * then the pre-existing file will be overwritten and replaced anyway;
     * whereas default {@code overwrite=false} will entirely skip all the
     * execution if the target file pre-exists and matches specification
     * (name, signatures...).
     */
    @Parameter(property = "download.overwrite")
    private boolean overwrite;

    /**
     * Represent the file name to use as output value.
     * If not set, will use last segment of "url".
     */
    @Parameter(property = "download.outputFileName")
    private String outputFileName;

    /**
     * Represent the directory where the file should be downloaded.
     */
    @Parameter(
        property = "download.outputDirectory", defaultValue = "${project.build.directory}",
        required = true
    )
    private File outputDirectory;

    /**
     * The md5 of the file. If set, file checksum will be compared to this
     * checksum and plugin will fail.
     */
    @Parameter(property = "download.verify.md5")
    private String md5;

    /**
     * The sha1 of the file. If set, file checksum will be compared to this
     * checksum and plugin will fail.
     */
    @Parameter(property = "download.verify.sha1")
    private String sha1;

    /**
     * The sha256 of the file. If set, file checksum will be compared to this
     * checksum and plugin will fail.
     */
    @Parameter(property = "download.verify.sha256")
    private String sha256;

    /**
     * The sha512 of the file. If set, file checksum will be compared to this
     * checksum and plugin will fail.
     */
    @Parameter(property = "download.verify.sha512")
    private String sha512;

    /**
     * Whether to unpack the file in case it is an archive (.zip).
     */
    @Parameter(property = "download.unpack", defaultValue = "false")
    private boolean unpack;

    /**
     * Whether to unpack the artifact only when the downloaded file changes.
     */
    @Parameter(property = "download.unpackWhenChanged", defaultValue = "false")
    private boolean unpackWhenChanged;

    /**
     * Server Id from settings file to use for authentication.
     * Only one of serverId or (username/password) may be supplied
     */
    @Parameter(property = "download.auth.serverId")
    private String serverId;

    /**
     * Custom username for the download.
     */
    @Parameter(property = "download.auth.username")
    private String username;

    /**
     * Custom password for the download.
     */
    @Parameter(property = "download.auth.password")
    private String password;

    /**
     * How many retries for a download.
     */
    @Parameter(property = "download.retries", defaultValue = "2")
    private int retries;

    /**
     * Read timeout for a download in milliseconds.
     */
    @Parameter(defaultValue = "3000")
    private int readTimeOut;

    /**
     * Download file without polling cache.
     * Means that the download operation will not look in the global cache
     * to resolve the file to download, and will directly proceed with
     * the download and won't store this download in the cache.
     * It's recommended for urls that have "volatile" content.
     */
    @Parameter(property = "download.cache.skip", defaultValue = "false")
    private boolean skipCache;

    /**
     * The directory to use as a cache. Default is ${local-repo}/.cache/download-maven-plugin
     */
    @Parameter(property = "download.cache.directory")
    private File cacheDirectory;

    /**
     * Flag to determine whether to fail on an unsuccessful download.
     */
    @Parameter(defaultValue = "true")
    private boolean failOnError;

    /**
     * Whether to skip execution of Mojo.
     */
    @Parameter(property = "download.plugin.skip", defaultValue = "false")
    private boolean skip;

    /**
     * Whether to verify the checksum of an existing file.
     * <p>
     * By default, checksum verification only occurs after downloading a file. This option
     * additionally enforces
     * checksum verification for already existing, previously downloaded (or manually copied)
     * files. If the checksum
     * does not match, re-download the file.
     * <p>
     * Use this option in order to ensure that a new download attempt is made after a previously
     * interrupted build or
     * network connection or some other event corrupted a file.
     */
    @Parameter(property = "alwaysVerifyChecksum", defaultValue = "false")
    private boolean alwaysVerifyChecksum;

    /**
     * The option name is counter-intuitive and not related to signatures but to
     * checksums, in fact.
     * Please use {@link #alwaysVerifyChecksum} instead. This option might be removed in a future
     * release.
     * @deprecated
     */
    @Parameter(property = "checkSignature", defaultValue = "false")
    @Deprecated
    private boolean checkSignature;

    /**
     * Whether to follow redirects (301 Moved Permanently, 302 Found, 303 See Other).
     * <p>If this option is disabled and the returned resource returns a redirect, the plugin
     * will report an error and exit unless {@link #failOnError} is {@code false}.</p>
     */
    @Parameter(property = "download.plugin.followRedirects", defaultValue = "true")
    private boolean followRedirects = true;

    /**
     * A list of additional HTTP headers to send with the request.
     */
    @Parameter(property = "download.plugin.headers")
    private Map<String, String> headers = new HashMap<>();

    /**
     * Maven session.
     */
    @Parameter(property = "session", readonly = true)
    private MavenSession session;

    /**
     * Maven Security Dispatcher.
     */
    @Component(hint = "mng-4384")
    private SecDispatcher securityDispatcher;

    /**
     * Maven Archiver Manager.
     */
    @Inject
    private ArchiverManager archiverManager;

    /**
     * Maven Build Context.
     */
    @Inject
    private BuildContext buildContext;

    /**
     * Runs the plugin only if the current project is the execution root.
     * This is helpful, if the plugin is defined in a profile and should only run once
     * to download a shared file.
     */
    @Parameter(property = "runOnlyAtRoot", defaultValue = "false")
    private boolean runOnlyAtRoot;

    /**
     * Maximum time (ms) to wait to acquire a file lock.
     * Customize the time when using the plugin to download the same file
     * from several submodules in parallel build.
     */
    @Parameter(property = "maxLockWaitTime", defaultValue = "30000")
    private long maxLockWaitTime;

    /**
     * {@link FileMapper}s to be used for rewriting each target path, or {@code null} if no rewriting shall happen.
     * @since 1.6.8
     */
    @Parameter(property = "download.fileMappers")
    private FileMapper[] fileMappers;

    /**
     * If {@code true}, preemptive authentication will be used.
     * @since 1.6.9
     */
    @Parameter(property = "preemptiveAuth", defaultValue = "false")
    private boolean preemptiveAuth;

    /**
     * Files to include when unpacking.
     *
     * <p>If left empty all files will be eligible to be unpacked.</p>
     *
     * <p>Entries are interpreted as Ant-style path patterns.</p>
     * @since 1.9.0
     */
    @Parameter(property = "download.unpack.includes")
    private String[] includes;

    /**
     * Files to ignore when unpacking.
     *
     * <p>If left empty no file will be excluded when unpacking.</p>
     *
     * <p>Entries are interpreted as Ant-style path patterns.</p>
     * @since 1.9.0
     */
    @Parameter(property = "download.unpack.excludes")
    private String[] excludes;

    /**
     * If {@code true}, SSL certificate verification is skipped.
     * @since 1.8.1
     */
    @Parameter(property = "insecure", defaultValue = "false")
    private boolean insecure;

    /**
     * Set specific permission to output file.
     * Currently, only {@code +x} option is supported to set executable permission.
     * @since 1.13.0
     */
    @Parameter(property = "download.outputFilePermissions")
    private String outputFilePermissions;

    static {
        CONN_POOL = new PoolingHttpClientConnectionManager(
            RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", new SSLConnectionSocketFactory(
                        SSLContexts.createSystemDefault(),
                        SSLProtocols.supported(),
                        null,
                        SSLConnectionSocketFactory.getDefaultHostnameVerifier()
                ))
                .build(),
            null,
            null,
            null,
            1,
            TimeUnit.MINUTES
        );
    }

    /**
     * Ensures that the output directory does not contain unresolved path variables, i.e. when
     * running without a pom.xml.
     * If unresolved path variables are detected, set the output directory to the current working
     * directory.
     * @throws MojoExecutionException If the current working directory could not be resolved.
     * This should never happen.
     * @since 1.7.2
     */
    private void adjustOutputDirectory() throws MojoExecutionException {
        if (this.outputDirectory.getPath().contains("${")) {
            this.getLog().info(
                String.format(
                    "Could not resolve outputDirectory '%s'.  Consider using -Ddownload.outputDirectory=.",
                    this.outputDirectory.getPath()
                )
            );
            this.outputDirectory = new File(".");
            try {
                this.getLog().info(
                    String.format(
                        "Adjusting outputDirectory to %s", this.outputDirectory.getCanonicalPath()
                    )
                );
            } catch (final IOException exc) {
                throw new MojoExecutionException(
                    "Current working directory could not be resolved. This should never happen.",
                    exc
                );
            }
        }
    }

    /**
     * Method call when the mojo is executed for the first time.
     * @throws MojoExecutionException if an error is occurring in this mojo.
     * @throws MojoFailureException if an error is occurring in this mojo.
     */
    @SuppressWarnings(
        {
            "checkstyle:ReturnCount", "checkstyle:JavaNCSS", "checkstyle:NPathComplexity",
            "checkstyle:ExecutableStatementCount", "checkstyle:NestedIfDepth", "checkstyle:IllegalCatch"
        }
    )
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (this.skip) {
            this.getLog().info("download-maven-plugin:wget skipped");
            return;
        }
        if (this.runOnlyAtRoot && !this.session.getCurrentProject().isExecutionRoot()) {
            this.getLog().info("download-maven-plugin:wget skipped (not project root)");
            return;
        }
        if (
                StringUtils.isNotBlank(this.serverId)
                        && (StringUtils.isNotBlank(this.username) || StringUtils.isNotBlank(this.password))
        ) {
            throw new MojoExecutionException("Specify either serverId or username/password, not both");
        }
        if (this.session.getSettings() == null) {
            this.getLog().warn("settings is null");
        }
        if (this.session.getSettings().isOffline()) {
            this.getLog().debug("download-maven-plugin:wget offline mode");
        }
        this.getLog().debug("Got settings");
        if (this.retries < 1) {
            throw new MojoFailureException("retries must be at least 1");
        }
        final Optional<DownloadCache> cache;
        if (!this.skipCache) {
            if (this.cacheDirectory == null) {
                this.cacheDirectory = new File(this.session.getLocalRepository()
                                                       .getBasedir(), ".cache/kie-download-maven-plugin");
            } else if (this.cacheDirectory.exists() && !this.cacheDirectory.isDirectory()) {
                throw new MojoFailureException(
                        String.format("cacheDirectory is not a directory: %s", this.cacheDirectory.getAbsolutePath())
                );
            }
            this.getLog().debug(String.format("Cache is: %s", this.cacheDirectory.getAbsolutePath()));
            cache = Optional.of(DOWNLOAD_CACHES.computeIfAbsent(
                    this.cacheDirectory.getAbsolutePath(),
                    directory -> new DownloadCache(this.cacheDirectory, this.getLog())
            ));
        } else {
            this.getLog().debug("Cache is skipped");
            cache = Optional.empty();
        }
        // PREPARE
        this.adjustOutputDirectory();
        if (this.outputDirectory.exists() && !this.outputDirectory.isDirectory()) {
            throw new MojoExecutionException(
                    "outputDirectory is not a directory: " + this.outputDirectory.getAbsolutePath());
        } else {
            this.outputDirectory.mkdirs();
        }
        for (URI uri : uris) {
            try {
                if (internalExecute(uri, cache)) {
                    return;
                } else {
                    String message = "Failed to download " + uri;
                    this.getLog().warn(message);
                }
            } catch (Exception e) {
                String error = e.getMessage() != null ? e.getMessage() : e.toString();
                String message = String.format("Failed to download %s due to %s", uri, error);
                this.getLog().warn(message);
            }
        }
    }


    private boolean internalExecute(URI uri, Optional<DownloadCache> cache) throws MojoExecutionException, MojoFailureException {
//        if (this.outputFileName == null) {
            this.outputFileName = FileNameUtils.getOutputFileName(uri);
//        }

        final File outputFile = new File(this.outputDirectory, this.outputFileName);
        final Lock fileLock = FILE_LOCKS.computeIfAbsent(
            outputFile.getAbsolutePath(), ignored -> new ReentrantLock()
        );
        final Checksums checksums = new Checksums(
            this.md5, this.sha1, this.sha256, this.sha512, this.getLog()
        );
        // DO
        boolean lockAcquired = false;
        try {
            lockAcquired = fileLock.tryLock(
                this.maxLockWaitTime, TimeUnit.MILLISECONDS
            );
            if (!lockAcquired) {
                final String message = String.format(
                    "Could not acquire lock for File: %s in %dms",
                    outputFile, this.maxLockWaitTime
                );
                if (this.failOnError) {
                    throw new MojoExecutionException(message);
                } else {
                    this.getLog().warn(message);
                    return false;
                }
            }
            boolean haveFile = outputFile.exists();
            if (haveFile) {
                boolean checksumMatch = true;
                if (this.alwaysVerifyChecksum || this.checkSignature) {
                    try {
                        checksums.validate(outputFile);
                    } catch (final MojoFailureException exc) {
                        this.getLog().warn(
                            String.format(
                                "The local version of file %s %s %s", outputFile.getName(),
                                "doesn't match the expected checksum.",
                                "You should consider checking the specified checksum is correctly set."
                            )
                        );
                        checksumMatch = false;
                    }
                }
                if (!checksumMatch || this.overwrite) {
                    outputFile.delete();
                    haveFile = false;
                } else {
                    this.getLog().info("File already exists, skipping");
                }
            }
            Optional<File> cachedFile = Optional.empty();
            boolean fileWasCached = false;
            if (!haveFile) {
                cachedFile = cache.map(c -> c.getArtifact(uri, checksums));
                fileWasCached = cachedFile.map(File::exists).orElse(false);
                if (fileWasCached) {
                    this.getLog().debug("File was cached: " + cachedFile.get().getAbsolutePath());
                    if (!this.unpack && !this.unpackWhenChanged) {
                        // only copy cached file to output file
                        // if it won't be unpacked, otherwise unpack
                        // directly from cached file below
                        this.getLog().debug("Copying cached file to " + outputFile.getAbsolutePath());
                        Files.copy(cachedFile.get().toPath(), outputFile.toPath());
                    }
                } else {
                    if (this.session.getRepositorySession().isOffline()) {
                        if (this.failOnError) {
                            throw new MojoExecutionException(
                                "No file in cache and maven is in offline mode");
                        } else {
                            this.getLog().warn("Ignoring download failure.");
                        }
                    }
                    boolean done = false;
                    for (int retriesLeft = this.retries; !done && retriesLeft > 0; --retriesLeft) {
                        try {
                            this.doGet(outputFile, uri);
                            checksums.validate(outputFile);
                            done = true;
                        } catch (final DownloadFailureException ex) {
                            // treating HTTP codes >= 500 as transient and thus always retriable
                            if (this.failOnError && ex.getHttpCode() < HttpCodes.INTERNAL_SERVER_ERROR.getCode()) {
                                throw new MojoExecutionException(ex.getMessage(), ex);
                            } else {
                                this.getLog().warn(ex.getMessage());
                            }
                        } catch (final IOException ex) {
                            if (this.failOnError) {
                                throw new MojoExecutionException(ex.getMessage(), ex);
                            } else {
                                this.getLog().warn(ex.getMessage());
                            }
                        }
                        if (!done) {
                            this.getLog().warn(String.format("Retrying (%d more)", retriesLeft - 1));
                        }
                    }
                    if (!done) {
                        if (this.failOnError) {
                            throw new MojoFailureException(
                                String.format("Could not get content after %d failed attempts.", this.retries)
                            );
                        } else {
                            this.getLog().warn("Ignoring download failure(s).");
                            return false;
                        }
                    }
                }
            }
            if (cache.isPresent()) {
                cache.get().install(uri, outputFile, checksums);
            }
            new FilePermissions(this.outputFilePermissions, this.getLog()).applyTo(outputFile);
            if (this.unpack || this.unpackWhenChanged) {
                if (!this.unpack && this.unpackWhenChanged && fileWasCached) {
                    this.getLog().info("Skipping unpacking as the file has not changed");
                } else {
                    if (this.unpackWhenChanged && fileWasCached) {
                        this.getLog().info(
                            "Unpacking even though unchanged cache file exists because unpack = true"
                        );
                    }
                    this.unpack(outputFile, cachedFile);
                    this.buildContext.refresh(this.outputDirectory);
                }
            } else {
                this.buildContext.refresh(outputFile);
            }
        } catch (final MojoExecutionException exc) {
            throw exc;
        } catch (final IOException exc) {
            throw new MojoExecutionException("IO Error: ", exc);
        } catch (final NoSuchArchiverException exc) {
            throw new MojoExecutionException(String.format("No such archiver: %s)", exc.getMessage()));
        } catch (final Exception exc) {
            throw new MojoExecutionException("General error: ", exc);
        } finally {
            if (lockAcquired) {
                fileLock.unlock();
            }
        }
        return true;
    }

    /**
     * Unpacks the given output file or cached file using an appropriate UnArchiver.
     * @param outputFile The file intended to be unpacked.
     * @param cachedFile An optional cached file that might be used instead of the output file.
     * @throws NoSuchArchiverException If there is no suitable UnArchiver for the output file.
     * @throws IllegalStateException If neither outputFile nor cachedFile exist for unpacking.
     */
    private void unpack(final File outputFile, final Optional<File> cachedFile) throws NoSuchArchiverException {
        final UnArchiver unarchiver = this.archiverManager.getUnArchiver(outputFile);
        if (cachedFile.isPresent() && cachedFile.get().exists()) {
            unarchiver.setSourceFile(cachedFile.get());
        } else if (outputFile.exists()) {
            unarchiver.setSourceFile(outputFile);
        } else {
            throw new IllegalStateException("No file to unpack");
        }
        if (isFileUnArchiver(unarchiver)) {
            unarchiver.setDestFile(
                new File(
                    this.outputDirectory, this.outputFileName.substring(0, this.outputFileName.lastIndexOf('.'))
                )
            );
        } else {
            unarchiver.setDestDirectory(this.outputDirectory);
        }
        unarchiver.setFileMappers(this.fileMappers);
        this.addFileSelectorIfNeeded(unarchiver);
        unarchiver.extract();
        if (outputFile.exists()) {
            outputFile.delete();
        }
    }

    /**
     * Determines if the provided UnArchiver instance is of a supported file format
     * that indicates it is a file unarchiver.
     * @param unarchiver The UnArchiver instance to check.
     * @return True if the unarchiver is an instance of supported types; false otherwise.
     */
    private static boolean isFileUnArchiver(final UnArchiver unarchiver) {
        return unarchiver instanceof BZip2UnArchiver
            || unarchiver instanceof GZipUnArchiver
            || unarchiver instanceof SnappyUnArchiver
            || unarchiver instanceof XZUnArchiver;
    }

    /**
     * Creates a remote repository with the given server ID and URI.
     * @param serverId The server ID to associate with the remote repository. If blank, certain default
     *  settings will be applied based on the URI's scheme and host.
     * @param uri The URI of the remote repository.
     * @return A configured instance of RemoteRepository.
     */
    private static RemoteRepository createRemoteRepository(final String serverId, final URI uri) {
        return new RemoteRepository.Builder(
            StringUtils.isBlank(serverId) ? null : serverId,
            StringUtils.isBlank(serverId) ? uri.getScheme() : null,
            StringUtils.isBlank(serverId) ? String.format("%s://%s", uri.getScheme(), uri.getHost()) : null
        ).build();
    }

    /**
     * Determines whether to show transfer progress. Progress is shown if the
     * session is interactive and the transfer listener is null or not a QuietMavenTransferListener.
     * @param session The current Maven session.
     * @return Whether to show transfer progress.
     */
    private static boolean showTransferProgress(final MavenSession session) {
        final boolean result;
        if (session.getSettings().isInteractiveMode()) {
            final TransferListener transferListener = session.getRequest().getTransferListener();
            if (transferListener == null) {
                result = true;
            } else {
                result = !"QuietMavenTransferListener".equals(transferListener.getClass().getSimpleName());
            }
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Downloads a file from a remote repository and stores it to the specified output file.
     * @param outputFile The file to which the downloaded content will be saved.
     * @throws IOException If an I/O error occurs during the file download.
     * @throws MojoExecutionException If an error specific to Maven Mojo execution occurs.
     */
    private void doGet(final File outputFile, URI uri) throws IOException, MojoExecutionException {
        final HttpFileRequester.Builder fileRequesterBuilder = new HttpFileRequester.Builder();
        final RemoteRepository repository = createRemoteRepository(this.serverId, uri);
        // set proxy if present
        Optional.ofNullable(this.session.getRepositorySession().getProxySelector())
            .map(selector -> selector.getProxy(repository))
            .ifPresent(proxy -> this.addProxy(fileRequesterBuilder, repository, proxy));
        Optional.ofNullable(this.session.getRepositorySession().getAuthenticationSelector())
            .map(selector -> selector.getAuthentication(repository))
            .ifPresent(auth -> this.addAuthentication(fileRequesterBuilder, repository, auth));
        final HttpFileRequester fileRequester = fileRequesterBuilder
            .withProgressReport(showTransferProgress(this.session)
                ? new LoggingProgressReport(this.getLog())
                : new SilentProgressReport(this.getLog()))
            .withConnectTimeout(this.readTimeOut)
            .withSocketTimeout(this.readTimeOut)
            .withUri(uri)
            .withUsername(this.username)
            .withPassword(this.password)
            .withServerId(this.serverId)
            .withPreemptiveAuth(this.preemptiveAuth)
            .withMavenSession(this.session)
            .withSecDispatcher(this.securityDispatcher)
            .withRedirectsEnabled(this.followRedirects)
            .withLog(this.getLog())
            .withInsecure(this.insecure)
            .build();
        fileRequester.download(outputFile, this.getAdditionalHeaders());
    }

    /**
     * Configures the provided HttpFileRequester.Builder with proxy settings derived
     * from the specified Proxy and RemoteRepository.
     * @param fileRequesterBuilder The builder for HttpFileRequester to configure with proxy settings.
     * @param repository The remote repository for which the proxy settings should be applied.
     * @param proxy The proxy whose settings (host, port, and authentication) will be used for the configuration.
     */
    private void addProxy(
        final HttpFileRequester.Builder fileRequesterBuilder,
        final RemoteRepository repository,
        final Proxy proxy
    ) {
        fileRequesterBuilder.withProxyHost(proxy.getHost());
        fileRequesterBuilder.withProxyPort(proxy.getPort());
        final RemoteRepository proxyRepo = new RemoteRepository.Builder(repository)
            .setProxy(proxy)
            .build();
        try (
            AuthenticationContext ctx = AuthenticationContext.forProxy(
                this.session.getRepositorySession(),
                proxyRepo
            )
        ) {
            if (ctx != null) {
                fileRequesterBuilder.withProxyUserName(ctx.get(AuthenticationContext.USERNAME));
                fileRequesterBuilder.withProxyPassword(ctx.get(AuthenticationContext.PASSWORD));
                fileRequesterBuilder.withNtlmDomain(ctx.get(AuthenticationContext.NTLM_DOMAIN));
                fileRequesterBuilder.withNtlmHost(ctx.get(AuthenticationContext.NTLM_WORKSTATION));
            }
        }
    }

    /**
     * Configures the provided HttpFileRequester.Builder with authentication settings derived
     * from the specified RemoteRepository and Authentication instances.
     * @param fileRequesterBuilder The builder for HttpFileRequester to configure with authentication settings.
     * @param repository The remote repository for which the authentication settings should be applied.
     * @param authentication The authentication credentials to use for configuring the file requester.
     */
    private void addAuthentication(
        final HttpFileRequester.Builder fileRequesterBuilder,
        final RemoteRepository repository,
        final Authentication authentication
    ) {
        final RemoteRepository authRepo = new RemoteRepository.Builder(repository)
            .setAuthentication(authentication)
            .build();
        try (
            AuthenticationContext authCtx = AuthenticationContext.forRepository(
                this.session.getRepositorySession(),
                authRepo
            )
        ) {
            final String uname = authCtx.get(AuthenticationContext.USERNAME);
            final String pass = authCtx.get(AuthenticationContext.PASSWORD);
            final String ntlmDomain = authCtx.get(AuthenticationContext.NTLM_DOMAIN);
            final String ntlmHost = authCtx.get(AuthenticationContext.NTLM_WORKSTATION);
            this.getLog().debug("providing custom authentication");
            this.getLog().debug(String.format("username: %s and password: ***", uname));
            fileRequesterBuilder.withUsername(uname);
            fileRequesterBuilder.withPassword(pass);
            fileRequesterBuilder.withNtlmDomain(ntlmDomain);
            fileRequesterBuilder.withNtlmHost(ntlmHost);
        }
    }

    /**
     * Constructs additional HTTP headers for requests.
     * @return A list of headers derived from the internal headers map.
     */
    private List<Header> getAdditionalHeaders() {
        return this.headers.entrySet().stream()
            .map(pair -> new BasicHeader(pair.getKey(), pair.getValue()))
            .collect(Collectors.toList());
    }

    /**
     * Adds a file selector to the provided UnArchiver if the includes or excludes
     * arrays are not empty.
     * @param unarchiver The UnArchiver where the file selector should be added.
     */
    private void addFileSelectorIfNeeded(final UnArchiver unarchiver) {
        if (this.includes.length != 0 || this.excludes.length != 0) {
            final IncludeExcludeFileSelector fileSelector = new IncludeExcludeFileSelector();
            if (this.includes.length != 0) {
                fileSelector.setIncludes(this.includes);
            }
            if (this.excludes.length != 0) {
                fileSelector.setExcludes(this.excludes);
            }
            unarchiver.setFileSelectors(new FileSelector[]{fileSelector});
        }
    }
}
