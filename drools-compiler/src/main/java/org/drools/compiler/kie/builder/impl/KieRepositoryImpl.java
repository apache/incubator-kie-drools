package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
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
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.TreeMap;
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

    public KieModule getKieModule(ReleaseId releaseId, byte[] pomXml) {
        KieModule kieModule = kieModuleRepo.load(releaseId);
        if (kieModule == null) {
            log.debug("KieModule Lookup. ReleaseId {} was not in cache, checking classpath",
                      releaseId.toExternalForm());
            kieModule = checkClasspathForKieModule(releaseId);
        }

        if (kieModule == null) {
            log.debug("KieModule Lookup. ReleaseId {} was not in cache, checking maven repository",
                      releaseId.toExternalForm());
            kieModule = loadKieModuleFromMavenRepo(releaseId, pomXml);
        }

        return kieModule;
    }

    private KieModule checkClasspathForKieModule(ReleaseId releaseId) {
        // TODO
        // ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // URL url = classLoader.getResource( ((ReleaseIdImpl)releaseId).getPomPropertiesPath() );
        return null;
    }

    private KieModule loadKieModuleFromMavenRepo(ReleaseId releaseId, byte[] pomXml) {
        return pomXml != null ?
               getInternalKieScanner().loadArtifact(releaseId, new ByteArrayInputStream(pomXml)) :
               getInternalKieScanner().loadArtifact(releaseId);
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

    private static class KieModuleRepo {

        private final InternalKieScanner kieScanner;
        private final Map<String, TreeMap<ComparableVersion, KieModule>> kieModules = new HashMap<String, TreeMap<ComparableVersion, KieModule>>();
        private final Map<ReleaseId, KieModule> oldKieModules = new HashMap<ReleaseId, KieModule>();

        private KieModuleRepo(InternalKieScanner kieScanner) {
            this.kieScanner = kieScanner;
        }

        KieModule remove(ReleaseId releaseId) {
            KieModule removedKieModule = null;
            String ga = releaseId.getGroupId() + ":" + releaseId.getArtifactId();
            TreeMap<ComparableVersion, KieModule> artifactMap = kieModules.get(ga);
            if (artifactMap != null) {
                ComparableVersion comparableVersion = new ComparableVersion(releaseId.getVersion());
                removedKieModule = artifactMap.remove(comparableVersion);
                if (artifactMap.isEmpty()) {
                    kieModules.remove(ga);
                }
            }
            oldKieModules.remove(releaseId);
            return removedKieModule;
        }

        void store(KieModule kieModule) {
            ReleaseId releaseId = kieModule.getReleaseId();
            String ga = releaseId.getGroupId() + ":" + releaseId.getArtifactId();

            TreeMap<ComparableVersion, KieModule> artifactMap = kieModules.get(ga);
            if (artifactMap == null) {
                artifactMap = new TreeMap<ComparableVersion, KieModule>();
                kieModules.put(ga, artifactMap);
            }
            ComparableVersion comparableVersion = new ComparableVersion(releaseId.getVersion());
            if (oldKieModules.get(releaseId) == null) {
                oldKieModules.put(releaseId, artifactMap.get(comparableVersion));
            }
            artifactMap.put(comparableVersion, kieModule);
        }

        private KieModule loadOldAndRemove(ReleaseId releaseId) {
            return oldKieModules.remove(releaseId);
        }

        KieModule load(ReleaseId releaseId) {
            return load(releaseId, new VersionRange(releaseId.getVersion()));
        }

        KieModule load(ReleaseId releaseId, VersionRange versionRange) {
            String ga = releaseId.getGroupId() + ":" + releaseId.getArtifactId();
            TreeMap<ComparableVersion, KieModule> artifactMap = kieModules.get(ga);
            if ( artifactMap == null ) {
                return null;
            }

            if (versionRange.fixed) {
                KieModule kieModule = artifactMap.get(new ComparableVersion(releaseId.getVersion()));
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

            final int INTEGER_ITEM = 0;
            final int STRING_ITEM = 1;
            final int LIST_ITEM = 2;

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
             * @param qualifier
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
