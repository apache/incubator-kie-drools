/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
