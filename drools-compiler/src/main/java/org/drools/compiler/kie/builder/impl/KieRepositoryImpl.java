/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.compiler.kproject.xml.PomModel;
import org.drools.core.io.internal.InternalResource;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.KieScannerFactoryService;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.utils.ServiceRegistryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicReference;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;

public class KieRepositoryImpl
        implements
        KieRepository {

    private static final Logger log = LoggerFactory.getLogger(KieRepositoryImpl.class);

    private static final String DEFAULT_VERSION = "1.0.0-SNAPSHOT";
    private static final String DEFAULT_ARTIFACT = "artifact";
    private static final String DEFAULT_GROUP = "org.default";

    public static final KieRepository INSTANCE = new KieRepositoryImpl();

    private final KieModuleRepo kieModuleRepo;

    private InternalKieScanner internalKieScanner;

    public KieRepositoryImpl() {
        internalKieScanner = getInternalKieScanner();
        kieModuleRepo = new KieModuleRepo(internalKieScanner);
    }

    private final AtomicReference<ReleaseId> defaultGAV = new AtomicReference(new ReleaseIdImpl(DEFAULT_GROUP,
                                                                                                DEFAULT_ARTIFACT,
                                                                                                DEFAULT_VERSION));

    public void setDefaultGAV(ReleaseId releaseId) {
        this.defaultGAV.set(releaseId);
    }

    public ReleaseId getDefaultReleaseId() {
        return this.defaultGAV.get();
    }

    public void addKieModule(KieModule kieModule) {
        kieModuleRepo.store(kieModule);
        log.info("KieModule was added: " + kieModule);
    }

    public KieModule getKieModule(ReleaseId releaseId) {
        return getKieModule(releaseId, null);
    }

    public KieModule removeKieModule(ReleaseId releaseId) {
        return kieModuleRepo.remove(releaseId);
    }

    KieModule getOldKieModule(ReleaseId releaseId) {
        KieModule kieModule = kieModuleRepo.loadOldAndRemove(releaseId);
        return kieModule != null ? kieModule : getKieModule(releaseId);
    }

    public KieModule getKieModule(ReleaseId releaseId, PomModel pomModel) {
        KieModule kieModule = kieModuleRepo.load(releaseId);
        if (kieModule == null) {
            log.debug("KieModule Lookup. ReleaseId {} was not in cache, checking classpath",
                      releaseId.toExternalForm());
            kieModule = checkClasspathForKieModule(releaseId);
        }

        if (kieModule == null) {
            log.debug("KieModule Lookup. ReleaseId {} was not in cache, checking maven repository",
                      releaseId.toExternalForm());
            kieModule = loadKieModuleFromMavenRepo(releaseId, pomModel);
        }

        return kieModule;
    }

    private KieModule checkClasspathForKieModule(ReleaseId releaseId) {
        // TODO
        // ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // URL url = classLoader.getResource( ((ReleaseIdImpl)releaseId).getPomPropertiesPath() );
        return null;
    }

    private KieModule loadKieModuleFromMavenRepo(ReleaseId releaseId, PomModel pomModel) {
        return getInternalKieScanner().loadArtifact(releaseId, pomModel);
    }

    private InternalKieScanner getInternalKieScanner() {
        if (internalKieScanner == null) {
            try {
                KieScannerFactoryService scannerFactoryService = ServiceRegistryImpl.getInstance().get( KieScannerFactoryService.class );
                internalKieScanner = (InternalKieScanner)scannerFactoryService.newKieScanner();
            } catch (Exception e) {
                // kie-ci is not on the classpath
                internalKieScanner = new DummyKieScanner();
            }
        }
        return internalKieScanner;
    }

    private static class DummyKieScanner
            implements
            InternalKieScanner {

        public void start(long pollingInterval) { }

        public void stop() { }

        public void shutdown() { }

        public void scanNow() { }

        public void setKieContainer(KieContainer kieContainer) { }

        public KieModule loadArtifact(ReleaseId releaseId) {
            return null;
        }

        public KieModule loadArtifact(ReleaseId releaseId, InputStream pomXML) {
            return null;
        }

        public KieModule loadArtifact(ReleaseId releaseId, PomModel pomModel) {
            return null;
        }

        public String getArtifactVersion(ReleaseId releaseId) {
            return null;
        }

        public ReleaseId getScannerReleaseId() {
            return null;
        }

        public ReleaseId getCurrentReleaseId() {
            return null;
        }

        public Status getStatus() {
            return Status.STOPPED;
        }

        public long getPollingInterval() { return 0; }
    }

    public KieModule addKieModule(Resource resource, Resource... dependencies) {
        log.info("Adding KieModule from resource: " + resource);
        KieModule kModule = getKieModule(resource);
        if (dependencies != null && dependencies.length > 0) {
            for (Resource depRes : dependencies) {
                InternalKieModule depKModule = (InternalKieModule) getKieModule(depRes);
                ((InternalKieModule) kModule).addKieDependency(depKModule);
                log.info("Adding KieModule dependency from resource: " + resource);
            }
        }

        addKieModule(kModule);
        return kModule;
    }

    public KieModule getKieModule(Resource resource) {
        InternalResource res = (InternalResource) resource;
        try {
            KieModule kModule;
            // find kmodule.xml
            if (res.hasURL()) {
                String urlPath = res.getURL().toExternalForm();
                if (res.isDirectory()) {
                    if (!urlPath.endsWith("/")) {
                        urlPath = urlPath + "/";
                    }
                    urlPath = urlPath + KieModuleModelImpl.KMODULE_JAR_PATH;

                } else {
                    urlPath = "jar:" + urlPath + "!/" + KieModuleModelImpl.KMODULE_JAR_PATH;
                }
                kModule = ClasspathKieProject.fetchKModule(new URL(urlPath));
                log.debug("Fetched KieModule from resource: " + resource);
            } else {
                // might be a byte[] resource
                MemoryFileSystem mfs = MemoryFileSystem.readFromJar(res.getInputStream());
                byte[] bytes = mfs.getBytes(KieModuleModelImpl.KMODULE_JAR_PATH);
                KieModuleModel kieProject = KieModuleModelImpl.fromXML(new ByteArrayInputStream(bytes));
                setDefaultsforEmptyKieModule(kieProject);

                String pomProperties = mfs.findPomProperties();
                ReleaseId releaseId = ReleaseIdImpl.fromPropertiesString(pomProperties);
                kModule = new MemoryKieModule(releaseId, kieProject, mfs);
            }
            return kModule;
        } catch (Exception e) {
            throw new RuntimeException("Unable to fetch module from resource: " + res, e);
        }
    }

    // package scope so that we can test it
    static class KieModuleRepo {

        private final InternalKieScanner kieScanner;

        private final ConcurrentHashMap<String, ConcurrentSkipListMap<ComparableVersion, KieModule>> kieModules
            = new ConcurrentHashMap<String, ConcurrentSkipListMap<ComparableVersion, KieModule>>();
        private final ConcurrentHashMap<ReleaseId, KieModule> oldKieModules
            = new ConcurrentHashMap<ReleaseId, KieModule>();

        /**
         * The gaLocks map:
         * - A LRU cache (based on *access* order, not insertion order) for locks per GA
         *
         * We lock on the basis of these objects (instead of on the artifactMap) because
         * the artifactMap can be removed and then readded,
         * whereas these lock objects will only be removed
         * when there MAX_UNIQUE_GAs objects that have been more recently accessed than the lock to be removed
         *
         * These objects are used to safeguard:
         * - modifications to and reads of a GA artifactMap,
         *   especially when the logic depends on multiple reads
         * - modifications to the oldKieModules map
         */
        private static final String UNIQUE_PROJECT_GA_MAX_PROPERTY = "org.kie.repository.project.ga.unique.max";
        private static final int MAX_UNIQUE_GAs
            = Integer.parseInt(System.getProperty(UNIQUE_PROJECT_GA_MAX_PROPERTY, "100"));
        private final HashMap<String,Object> gaLocks = new LinkedHashMap<String, Object>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry( Entry<String, Object> eldest ) {
                return size() > MAX_UNIQUE_GAs;
            }
        };

        // This value *must* always be smaller than MAX_UNIQUE_GAs
        public static final String GA_ARTIFACT_MAP_REPLACEMENT_MAX_PROPERTY = "org.kie.repository.project.ga.replacement.max";
        private static final int EMPTY_ARTIFACT_MAP_REMOVAL_QUEUE_SIZE
            = Integer.parseInt(System.getProperty(GA_ARTIFACT_MAP_REPLACEMENT_MAX_PROPERTY, "20"));

        static {
            if( EMPTY_ARTIFACT_MAP_REMOVAL_QUEUE_SIZE > MAX_UNIQUE_GAs + 1 ) {
               throw new IllegalStateException("The " + GA_ARTIFACT_MAP_REPLACEMENT_MAX_PROPERTY + " property [" +
                       EMPTY_ARTIFACT_MAP_REMOVAL_QUEUE_SIZE + "] may not be larger than the  "
                       + UNIQUE_PROJECT_GA_MAX_PROPERTY + " [" + MAX_UNIQUE_GAs + "] property!");
            }
        }

        /**
         * The removeArtifactMapQueue:
         * - A LRU cache based on insertion order that triggers artifactMap removal when the object is discarded
         *
         * When the max size[1] for the removeArtifactMapQueue map has been reached,
         * the removeEldestEntry logic below checks to see if the artifactMap (value) from the last (inserted) entry is still empty.
         * If the artifactMap entry is empty, the empty artifactMap s removed from the kieModules map.
         * Regardless of whether the artifactMap is empty, the entry is then removed from the removeArtifactMapQueue map,
         * so that it's not checked twice.
         *
         * [1] The max size her is the KIE_MODULES_REMOVE_EMPTY_ARTIFACT_MAP_QUEUE_SIZE constant
         *
         * The idea here is that we need a mechanism that
         *   1. removes the artifactMap from the kieModules map field
         *   2. but delays it,
         * this is in order to avoid a race condition for situations in which operations with ga-single version deployments
         * (i.e. one thread removes while the next thread stores) would cause different threads to use different artifactMap instances
         * for the same GA
         *
         * Of couse, if the user is doing this for enough[2] ga-single versions modules, then there will be a race condition.
         * However, that situation is unlikely.
         *
         * [2] where "enough" > the max-size of the cache or KIE_MODULES_REMOVE_EMPTY_ARTIFACT_MAP_QUEUE_SIZE
         */
        private final Map<String, NavigableMap<ComparableVersion, KieModule>> removeArtifactMapQueue = new LinkedHashMap<String, NavigableMap<ComparableVersion, KieModule>>() {
            @Override
            protected boolean removeEldestEntry( Entry<String, NavigableMap<ComparableVersion, KieModule>> eldest ) {
                boolean remove = size() > EMPTY_ARTIFACT_MAP_REMOVAL_QUEUE_SIZE;
                if( remove ) {
                    String ga = eldest.getKey();
                    NavigableMap<ComparableVersion, KieModule> artifactMap = eldest.getValue();
                    Object gaLock = getGALockObject(ga);
                    synchronized(gaLock) {
                        // if the artifact map is empty (and it's the one still in the kieModules map!), then remove it
                        if( artifactMap.isEmpty() ) {
                            kieModules.remove(ga, artifactMap);
                            // the remove above can fail if the artifactMap value was somehow replaced,
                            // but that's what we want for that case
                        }
                    }
                }
                return remove;
            }
        };


        KieModuleRepo(InternalKieScanner kieScanner) {
            this.kieScanner = kieScanner;
        }

        KieModule remove(ReleaseId releaseId) {
            KieModule removedKieModule = null;
            String ga = releaseId.getGroupId() + ":" + releaseId.getArtifactId();
            NavigableMap<ComparableVersion, KieModule> artifactMap = kieModules.get(ga);
            ComparableVersion comparableVersion = new ComparableVersion(releaseId.getVersion());

            // synchronize on the GA, otherwise we might be removing what another thread is adding
            Object gaLock = getGALockObject(ga);
            synchronized(gaLock) {
                if (artifactMap != null) {
                    removedKieModule = artifactMap.remove(comparableVersion);
                    if (artifactMap.isEmpty()) {
                        // add the KieModule artifactMap to the queue to be deleted
                        // (otherwise, if we remove it immediately, there's a possible race condition
                        //  for users that remove and add ga-single version (as opposed to ga-multiple versions)
                        //  kie modules)
                        removeArtifactMapQueue.put(ga, artifactMap);
                    }
                }
                oldKieModules.remove(releaseId);
            }

            return removedKieModule;
        }

        void store(KieModule kieModule) {
            ReleaseId releaseId = kieModule.getReleaseId();
            String ga = releaseId.getGroupId() + ":" + releaseId.getArtifactId();

            // doing a kieModules.get(ga) first is more performant than always creating a possibly new map for the putIfAbsent operation
            ConcurrentSkipListMap<ComparableVersion, KieModule> artifactMap = kieModules.get(ga);
            if( artifactMap == null ) {
                artifactMap = new ConcurrentSkipListMap<ComparableVersion, KieModule>();
                ConcurrentSkipListMap<ComparableVersion, KieModule> existingArtifactMap = kieModules.putIfAbsent(ga, artifactMap);
                if( existingArtifactMap != null ) {
                    artifactMap = existingArtifactMap;
                }
            }

            ComparableVersion comparableVersion = new ComparableVersion(releaseId.getVersion());

            // synchronize on the GA, otherwise we might be adding what another thread is removing
            // Also, without a synchronize, Thread A's oldKieModule value can be Thread B's kieModule value
            Object gaLock = getGALockObject(ga);
            synchronized(gaLock) {
                KieModule oldReleaseIdKieModule = oldKieModules.get(releaseId);
                // variable used in order to test race condition
                if (oldReleaseIdKieModule == null) {
                    KieModule oldKieModule = artifactMap.get(comparableVersion);
                    if (oldKieModule != null) {
                        oldKieModules.put( releaseId, oldKieModule );
                    }
                }
                artifactMap.put(comparableVersion, kieModule);
            }
        }

        private Object getGALockObject(String ga) {
            Object lock = new Object();
            synchronized(gaLocks) {
                Object existingLock = gaLocks.get(ga);
                if( existingLock == null ) {
                    gaLocks.put(ga, lock);
                } else {
                    lock = existingLock;
                }
            }
            return lock;
        }

        KieModule loadOldAndRemove(ReleaseId releaseId) {
            String ga = releaseId.getGroupId() + ":" + releaseId.getArtifactId();
            Object gaLock = getGALockObject(ga);
            synchronized(gaLock) {
                return oldKieModules.remove(releaseId);
            }
        }

        KieModule load(ReleaseId releaseId) {
            return load(releaseId, new VersionRange(releaseId.getVersion()));
        }

        KieModule load(ReleaseId releaseId, VersionRange versionRange) {
            String ga = releaseId.getGroupId() + ":" + releaseId.getArtifactId();
            Object gaLock = getGALockObject(ga);

            KieModule kieModule = null;
            NavigableMap<ComparableVersion, KieModule> artifactMap;
            synchronized(gaLock) {
                artifactMap = kieModules.get(ga);
                if ( artifactMap == null || artifactMap.isEmpty() ) {
                    return null;
                }
                kieModule = artifactMap.get(new ComparableVersion(releaseId.getVersion()));
            }

            if (versionRange.fixed) {
                if ( kieModule != null && releaseId.isSnapshot() ) {
                    String oldSnapshotVersion = ((ReleaseIdImpl)kieModule.getReleaseId()).getSnapshotVersion();
                    if ( oldSnapshotVersion != null ) {
                        String currentSnapshotVersion = kieScanner.getArtifactVersion(releaseId);
                        if (currentSnapshotVersion != null &&
                            new ComparableVersion(currentSnapshotVersion).compareTo(new ComparableVersion(oldSnapshotVersion)) > 0) {
                            // if the snapshot currently available on the maven repo is newer than the cached one
                            // return null to enforce the building of this newer version
                            return null;
                        }
                    }
                }
                return kieModule;
            }

            // no need for a ga lock here, since this is one-time read
            Map.Entry<ComparableVersion, KieModule> entry =
                    versionRange.upperBound == null ?
                    artifactMap.lastEntry() :
                    versionRange.upperInclusive ?
                        artifactMap.floorEntry(new ComparableVersion(versionRange.upperBound)) :
                        artifactMap.lowerEntry(new ComparableVersion(versionRange.upperBound));

            if ( entry == null ) {
                return null;
            }

            if ( versionRange.lowerBound == null ) {
                return entry.getValue();
            }

            int comparison = entry.getKey().compareTo(new ComparableVersion(versionRange.lowerBound));
            return comparison > 0 || (comparison == 0 && versionRange.lowerInclusive) ? entry.getValue() : null;
        }
    }

    private static class VersionRange {

        private String lowerBound;
        private String upperBound;
        private boolean lowerInclusive;
        private boolean upperInclusive;
        private boolean fixed;

        private VersionRange(String version) {
            parse(version);
        }

        private void parse(String version) {
            if ("LATEST".equals(version) || "RELEASE".equals(version)) {
                fixed = false;
                lowerBound = "1.0";
                upperBound = null;
                lowerInclusive = true;
                upperInclusive = false;
                return;
            }

            if (version.charAt(0) != '(' && version.charAt(0) != '[') {
                fixed = true;
                lowerBound = version;
                upperBound = version;
                lowerInclusive = true;
                upperInclusive = true;
                return;
            }

            lowerInclusive = version.charAt(0) == '[';
            upperInclusive = version.charAt(version.length() - 1) == ']';

            int commaPos = version.indexOf(',');
            if (commaPos < 0) {
                fixed = true;
                lowerBound = version.substring(1, version.length() - 1);
                upperBound = lowerBound;
            } else {
                if (commaPos > 1) {
                    lowerBound = version.substring(1, commaPos);
                }
                if (commaPos < version.length() - 2) {
                    upperBound = version.substring(commaPos + 1, version.length() - 1);
                }
            }
        }
    }

    public static class ComparableVersion implements Comparable<ComparableVersion> {

        private String value;

        private String canonical;

        private ListItem items;

        private interface Item {

            int INTEGER_ITEM = 0;
            int STRING_ITEM = 1;
            int LIST_ITEM = 2;

            int compareTo(Item item);

            int getType();

            boolean isNull();
        }

        private static class IntegerItem implements Item {

            private static final BigInteger BigInteger_ZERO = new BigInteger("0");

            private final BigInteger value;

            public static final IntegerItem ZERO = new IntegerItem();

            private IntegerItem() {
                this.value = BigInteger_ZERO;
            }

            public IntegerItem(String str) {
                this.value = new BigInteger(str);
            }

            public int getType() {
                return INTEGER_ITEM;
            }

            public boolean isNull() {
                return BigInteger_ZERO.equals(value);
            }

            public int compareTo(Item item) {
                if (item == null)
                {
                    return BigInteger_ZERO.equals(value) ? 0 : 1; // 1.0 == 1, 1.1 > 1
                }

                switch (item.getType())
                {
                    case INTEGER_ITEM:
                        return value.compareTo(((IntegerItem) item).value);

                    case STRING_ITEM:
                        return 1; // 1.1 > 1-sp

                    case LIST_ITEM:
                        return 1; // 1.1 > 1-1

                    default:
                        throw new RuntimeException("invalid item: " + item.getClass());
                }
            }

            public String toString() {
                return value.toString();
            }
        }

        /**
         * Represents a string in the version item list, usually a qualifier.
         */
        private static class StringItem implements Item {

            private static final String[] QUALIFIERS = {"alpha", "beta", "milestone", "rc", "snapshot", "", "sp"};

            private static final List<String> _QUALIFIERS = Arrays.asList(QUALIFIERS);

            private static final Properties ALIASES = new Properties();

            static {
                ALIASES.put("ga", "");
                ALIASES.put("final", "");
                ALIASES.put("cr", "rc");
            }

            /**
             * A comparable value for the empty-string qualifier. This one is used to determine if a given qualifier makes
             * the version older than one without a qualifier, or more recent.
             */
            private static final String RELEASE_VERSION_INDEX = String.valueOf(_QUALIFIERS.indexOf(""));

            private String value;

            public StringItem(String value, boolean followedByDigit) {
                if (followedByDigit && value.length() == 1) {
                    // a1 = alpha-1, b1 = beta-1, m1 = milestone-1
                    switch (value.charAt(0)) {
                        case 'a':
                            value = "alpha";
                            break;
                        case 'b':
                            value = "beta";
                            break;
                        case 'm':
                            value = "milestone";
                            break;
                    }
                }
                this.value = ALIASES.getProperty(value, value);
            }

            public int getType() {
                return STRING_ITEM;
            }

            public boolean isNull() {
                return (comparableQualifier(value).compareTo(RELEASE_VERSION_INDEX) == 0);
            }

            /**
             * Returns a comparable value for a qualifier.
             *
             * This method both takes into account the ordering of known qualifiers as well as lexical ordering for unknown
             * qualifiers.
             *
             * just returning an Integer with the index here is faster, but requires a lot of if/then/else to check for -1
             * or QUALIFIERS.size and then resort to lexical ordering. Most comparisons are decided by the first character,
             * so this is still fast. If more characters are needed then it requires a lexical sort anyway.
             *
             * @return an equivalent value that can be used with lexical comparison
             */
            public static String comparableQualifier(String qualifier) {
                int i = _QUALIFIERS.indexOf(qualifier);

                return i == -1 ? _QUALIFIERS.size() + "-" + qualifier : String.valueOf(i);
            }

            public int compareTo(Item item) {
                if (item == null) {
                    // 1-rc < 1, 1-ga > 1
                    return comparableQualifier(value).compareTo(RELEASE_VERSION_INDEX);
                }
                switch (item.getType()) {
                    case INTEGER_ITEM:
                        return -1; // 1.any < 1.1 ?

                    case STRING_ITEM:
                        return comparableQualifier(value).compareTo(comparableQualifier(((StringItem) item).value));

                    case LIST_ITEM:
                        return -1; // 1.any < 1-1

                    default:
                        throw new RuntimeException("invalid item: " + item.getClass());
                }
            }

            public String toString() {
                return value;
            }
        }

        /**
         * Represents a version list item. This class is used both for the global item list and for sub-lists (which start
         * with '-(number)' in the version specification).
         */
        private static class ListItem extends ArrayList<Item> implements Item {

            public int getType() {
                return LIST_ITEM;
            }

            public boolean isNull() {
                return (size() == 0);
            }

            void normalize() {
                for (ListIterator<Item> iterator = listIterator(size()); iterator.hasPrevious();) {
                    Item item = iterator.previous();
                    if (item.isNull()) {
                        iterator.remove(); // remove null trailing items: 0, "", empty list
                    } else {
                        break;
                    }
                }
            }

            public int compareTo(Item item) {
                if (item == null) {
                    if (size() == 0) {
                        return 0; // 1-0 = 1- (normalize) = 1
                    }
                    Item first = get(0);
                    return first.compareTo(null);
                }
                switch (item.getType()) {
                    case INTEGER_ITEM:
                        return -1; // 1-1 < 1.0.x

                    case STRING_ITEM:
                        return 1; // 1-1 > 1-sp

                    case LIST_ITEM:
                        Iterator<Item> left = iterator();
                        Iterator<Item> right = ((ListItem) item).iterator();

                        while (left.hasNext() || right.hasNext()) {
                            Item l = left.hasNext() ? left.next() : null;
                            Item r = right.hasNext() ? right.next() : null;

                            // if this is shorter, then invert the compare and mul with -1
                            int result = l == null ? -1 * r.compareTo(l) : l.compareTo(r);

                            if (result != 0) {
                                return result;
                            }
                        }

                        return 0;

                    default:
                        throw new RuntimeException("invalid item: " + item.getClass());
                }
            }

            public String toString() {
                StringBuilder buffer = new StringBuilder("(");
                for (Iterator<Item> iter = iterator(); iter.hasNext();)
                {
                    buffer.append(iter.next());
                    if (iter.hasNext())
                    {
                        buffer.append(',');
                    }
                }
                buffer.append(')');
                return buffer.toString();
            }
        }

        public ComparableVersion(String version) {
            parseVersion(version);
        }

        public final void parseVersion(String version) {
            this.value = version;

            items = new ListItem();

            version = version.toLowerCase(Locale.ENGLISH);

            ListItem list = items;

            Stack<Item> stack = new Stack<Item>();
            stack.push(list);

            boolean isDigit = false;

            int startIndex = 0;

            for (int i = 0; i < version.length(); i++) {
                char c = version.charAt(i);

                if (c == '.') {
                    if (i == startIndex) {
                        list.add(IntegerItem.ZERO);
                    } else {
                        list.add(parseItem(isDigit, version.substring(startIndex, i)));
                    }
                    startIndex = i + 1;
                } else if (c == '-') {
                    if (i == startIndex) {
                        list.add(IntegerItem.ZERO);
                    } else {
                        list.add(parseItem(isDigit, version.substring(startIndex, i)));
                    }
                    startIndex = i + 1;

                    if (isDigit) {
                        list.normalize(); // 1.0-* = 1-*

                        if ((i + 1 < version.length()) && Character.isDigit(version.charAt(i + 1))) {
                            // new ListItem only if previous were digits and new char is a digit,
                            // ie need to differentiate only 1.1 from 1-1
                            list.add(list = new ListItem());

                            stack.push(list);
                        }
                    }
                }
                else if (Character.isDigit(c)) {
                    if (!isDigit && i > startIndex) {
                        list.add(new StringItem(version.substring(startIndex, i), true));
                        startIndex = i;
                    }

                    isDigit = true;
                } else {
                    if (isDigit && i > startIndex) {
                        list.add(parseItem(true, version.substring(startIndex, i)));
                        startIndex = i;
                    }

                    isDigit = false;
                }
            }

            if (version.length() > startIndex) {
                list.add(parseItem(isDigit, version.substring(startIndex)));
            }

            while (!stack.isEmpty()) {
                list = (ListItem) stack.pop();
                list.normalize();
            }

            canonical = items.toString();
        }

        private static Item parseItem(boolean isDigit, String buf) {
            return isDigit ? new IntegerItem(buf) : new StringItem(buf, false);
        }

        public int compareTo(ComparableVersion o) {
            return items.compareTo(o.items);
        }

        public String toString() {
            return value;
        }

        public boolean equals(Object o) {
            return (o instanceof ComparableVersion) && canonical.equals(((ComparableVersion) o).canonical);
        }

        public int hashCode() {
            return canonical.hashCode();
        }
    }
}
