package org.kie.api.cluster;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public final class ClusterNode implements Externalizable {

    private String location;

    private String serverId;

    
    public ClusterNode() {
        // for serializing
    }

    public ClusterNode(String serverId, String location) {
        this.serverId = serverId;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public String getServerId() {
        return serverId;
    }
    
    public String toKey() {
        return serverId + "-" + location;
    }

    @Override
    public String toString() {
        return "Cluster Node [" + toKey() + "]";
    }
    
    @Override
    public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
        input.read(); // skip version
        serverId = (String) input.readObject();
        location = (String) input.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput output) throws IOException {
        output.write(1); // read version
        output.writeObject(serverId);
        output.writeObject(location);
    }

}
