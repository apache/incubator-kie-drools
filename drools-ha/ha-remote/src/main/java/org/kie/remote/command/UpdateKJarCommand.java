/*
 * Copyright 2019 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.remote.command;

import java.io.Serializable;
import java.util.UUID;

public class UpdateKJarCommand extends AbstractCommand implements VisitableCommand, RemoteCommand, Serializable {

    private String kJarGAV;
    private String groupID;
    private String artifactID;
    private String version;

    public UpdateKJarCommand() {/*For serialization*/}

    public UpdateKJarCommand(String kjarGAV){
        super(UUID.randomUUID().toString());
        this.kJarGAV = kjarGAV;
        String[] parts= this.kJarGAV.split(":");
        groupID = parts[0];
        artifactID = parts[1];
        version = parts[2];
    }

    public String getKJarGAV(){
        return kJarGAV;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getArtifactID() {
        return artifactID;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public void accept(VisitorCommand visitor) { visitor.visit(this); }

    @Override
    public boolean isPermittedForReplicas() { return true; }

    @Override
    public String toString() {
        return "UpdateKJarCommand with KJar GAV:"+ kJarGAV ;
    }
}
