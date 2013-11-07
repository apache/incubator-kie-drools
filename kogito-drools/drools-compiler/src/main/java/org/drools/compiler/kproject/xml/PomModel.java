package org.drools.compiler.kproject.xml;

import org.kie.api.builder.ReleaseId;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PomModel {

    private ReleaseId releaseId;
    private ReleaseId parentReleaseId;
    private Set<ReleaseId> dependencies = new HashSet<ReleaseId>();


    public ReleaseId getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(ReleaseId releaseId) {
        this.releaseId = releaseId;
    }

    public ReleaseId getParentReleaseId() {
        return parentReleaseId;
    }

    public void setParentReleaseId(ReleaseId parentReleaseId) {
        this.parentReleaseId = parentReleaseId;
    }

    public Collection<ReleaseId> getDependencies() {
        return dependencies;
    }

    public void addDependency(ReleaseId dependency) {
        this.dependencies.add(dependency);
    }

    public static class Parser {

        private static class PomModelGeneratorHolder {
            private static PomModelGenerator pomModelGenerator;

            static {
                try {
                    pomModelGenerator = (PomModelGenerator) Class.forName("org.kie.scanner.MavenPomModelGenerator").newInstance();
                } catch (Exception e) {
                    pomModelGenerator = new DefaultPomModelGenerator();
                }
            }
        }

        public static PomModel parse(String path, InputStream is) {
            try {
                return PomModelGeneratorHolder.pomModelGenerator.parse(path, is);
            } catch (Exception e) {
                return MinimalPomParser.parse(path, is);
            }
        }
    }

    private static class DefaultPomModelGenerator implements PomModelGenerator {
        @Override
        public PomModel parse(String path, InputStream is) {
            return MinimalPomParser.parse(path, is);
        }
    }
}
