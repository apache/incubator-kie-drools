package org.drools.beliefs.bayes;

public interface PassMessageListener {
    public void beforeProjectAndAbsorb(JunctionTreeClique sourceNode, JunctionTreeSeparator sep,
                                       JunctionTreeClique targetNode, double[] oldSeparatorPotentials);

    public void afterProject(JunctionTreeClique sourceNode, JunctionTreeSeparator sep,
                             JunctionTreeClique targetNode, double[] oldSeparatorPotentials);

    public void afterAbsorb(JunctionTreeClique sourceNode, JunctionTreeSeparator sep,
                            JunctionTreeClique targetNode, double[] oldSeparatorPotentials);
}
