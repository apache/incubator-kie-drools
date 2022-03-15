package to.not.instrument;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;

public class UsingADependencyClass {

    private final String name;
    private Cluster<Clusterable> aCluster;

    public UsingADependencyClass(String name) {
        super();
        this.name = name;
        this.aCluster = new Cluster<Clusterable>();
    }

    public String getName() {
        return name;
    }

    public Cluster<Clusterable> getaCluster() {
        return aCluster;
    }

    public void setACluster(Cluster<Clusterable> aCluster) {
        this.aCluster = aCluster;
    }
}
