package org.drools.verifier.api.reporting;

import java.util.Set;

public class Issues {

    private String webWorkerUUID;

    private Set<Issue> set;

    public Issues() {

    }

    public Issues(final String webWorkerUUID,
                  final Set<Issue> set) {
        this.webWorkerUUID = webWorkerUUID;
        this.set = set;
    }

    public void setWebWorkerUUID(final String webWorkerUUID) {
        this.webWorkerUUID = webWorkerUUID;
    }

    public void setSet(final Set<Issue> set) {
        this.set = set;
    }

    public String getWebWorkerUUID() {
        return webWorkerUUID;
    }

    public Set<Issue> getSet() {
        return set;
    }
}
