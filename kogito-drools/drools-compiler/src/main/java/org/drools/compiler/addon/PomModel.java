package org.drools.compiler.addon;


import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kie.api.builder.ReleaseId;

public interface PomModel {
   
    ReleaseId getReleaseId();

    ReleaseId getParentReleaseId();

    Collection<ReleaseId> getDependencies();
    Collection<ReleaseId> getDependencies(DependencyFilter filter);

    class InternalModel implements PomModel {
        private ReleaseId releaseId;
        private ReleaseId parentReleaseId;
        private final Map<String, Set<ReleaseId>> dependencies = new HashMap<String, Set<ReleaseId>>();

        @Override
        public ReleaseId getReleaseId() {
            return releaseId;
        }

        public void setReleaseId(ReleaseId releaseId) {
            this.releaseId = releaseId;
        }

        @Override
        public ReleaseId getParentReleaseId() {
            return parentReleaseId;
        }

        public void setParentReleaseId(ReleaseId parentReleaseId) {
            this.parentReleaseId = parentReleaseId;
        }

        @Override
        public Collection<ReleaseId> getDependencies() {
            return getDependencies(DependencyFilter.TAKE_ALL_FILTER);
        }

        @Override
        public Collection<ReleaseId> getDependencies(DependencyFilter filter ) {
            Set<ReleaseId> depSet = new HashSet<ReleaseId>();
            for (Map.Entry<String, Set<ReleaseId>> entry : dependencies.entrySet()) {
                for (ReleaseId releaseId : entry.getValue()) {
                    if (filter.accept( releaseId, entry.getKey() )) {
                        depSet.add(releaseId);
                    }
                }
            }
            return depSet;
        }

        protected void addDependency(ReleaseId dependency, String scope) {
            Set<ReleaseId> depsByScope = dependencies.get(scope);
            if (depsByScope == null) {
                depsByScope = new HashSet<ReleaseId>();
                dependencies.put( scope, depsByScope );
            }
            depsByScope.add( dependency );
        }
    }

    class Parser {


        public static PomModel parse(String path, InputStream is) {
            return MinimalPomParser.parse(path, is);
            
        }
    }
}
