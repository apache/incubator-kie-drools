/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.remote.message;

import java.io.Serializable;

public class GetKJarGAVMessage extends AbstractMessage implements Serializable, ResultMessage<String> {

    private String kJarGAV;

    /* Empty constructor for serialization */
    public GetKJarGAVMessage(){}

    public GetKJarGAVMessage(String id, String kJarGAV){
        super(id);
        this.kJarGAV = kJarGAV;
    }

    @Override
    public String getResult() {
        return getkJarGAV();
    }

    public String getkJarGAV(){
        return kJarGAV;
    }

    @Override
    public String toString() {
        return "KJarGAVMessage{" +
                " kJarGAV=" + kJarGAV +
                ", id='" + id + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
