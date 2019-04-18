/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.submarine.rest.quarkus;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;

public class DMNModelInfoList implements Serializable {

    private DMNModelInfo[] models;

    public DMNModelInfoList() {
    }

    public DMNModelInfoList(List<DMNModelInfo> models) {
        this.models = models.toArray(new DMNModelInfo[]{});
    }

    @JsonbProperty("models")
    public List<DMNModelInfo> getModels() {
        return Arrays.asList(models);
    }

    public void setModels(List<DMNModelInfo> models) {
        this.models = models.toArray(new DMNModelInfo[]{});
    }
}
