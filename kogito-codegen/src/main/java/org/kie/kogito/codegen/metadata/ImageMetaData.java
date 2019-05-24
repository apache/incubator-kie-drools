/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.kogito.codegen.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImageMetaData {

    private List<Map<String, String>> labels;

    public ImageMetaData() {}

    public ImageMetaData(List<Map<String, String>> labels) {
        super();
        this.labels = labels;
    }

    public List<Map<String, String>> getLabels() {
        return labels;
    }

    public void setLabels(List<Map<String, String>> labels) {
        this.labels = labels;
    }

    public void add(Map<String, String> labels2) {
        if (this.labels == null) {
            this.labels = new ArrayList<>();
            this.labels.add(labels2);
        } else {
            this.labels.get(0).putAll(labels2);
        }        
    }
    
    @Override
    public String toString() {
        return "ImageMetaData [labels=" + labels + "]";
    }


}
