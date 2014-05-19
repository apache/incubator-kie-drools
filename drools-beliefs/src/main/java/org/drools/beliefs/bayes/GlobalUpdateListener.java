package org.drools.beliefs.bayes;

public interface GlobalUpdateListener {
    public void beforeGlobalUpdate(CliqueState clique);
    public void afterGlobalUpdate(CliqueState clique);
}
