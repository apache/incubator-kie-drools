/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.dmn;

import java.io.InputStream;

import org.drools.core.util.IoUtils;
import org.kie.api.management.GAV;
import org.kie.kogito.decision.DecisionModelType;

public class DecisionModelJarResource extends BaseDecisionModelResource {

    public DecisionModelJarResource(GAV gav,
                                    String path,
                                    String namespace,
                                    String modelName,
                                    DecisionModelType type) {
        super(gav,
              path,
              namespace,
              modelName,
              type);
    }

    @Override
    public InputStream getInputStream() {
        return IoUtils.class.getClassLoader().getResourceAsStream(path);
    }
}
