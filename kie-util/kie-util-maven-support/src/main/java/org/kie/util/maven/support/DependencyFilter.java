package org.kie.util.maven.support;

import org.kie.api.builder.ReleaseId;

public interface DependencyFilter {
    boolean accept(ReleaseId releaseId, String scope );

    DependencyFilter TAKE_ALL_FILTER = new DependencyFilter() {
        @Override
        public boolean accept(ReleaseId releaseId, String scope ) {
            return true;
        }
    };

    DependencyFilter COMPILE_FILTER = new ExcludeScopeFilter("test", "provided");

    class ExcludeScopeFilter implements DependencyFilter {
        private final String[] excludedScopes;

        public ExcludeScopeFilter( String... excludedScopes ) {
            this.excludedScopes = excludedScopes;
        }

        @Override
        public boolean accept(ReleaseId releaseId, String scope ) {
            for (String excludedScope : excludedScopes) {
                if (excludedScope.equals( scope )) {
                    return false;
                }
            }
            return true;
        }
    }
}