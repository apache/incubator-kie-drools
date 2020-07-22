/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.index.graphql.infinispan;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.kie.kogito.index.DataIndexInfinispanServerTestResource;
import org.kie.kogito.index.graphql.AbstractWebSocketSubscriptionIT;

import static org.kie.kogito.index.TestUtils.getDealsProtoBufferFile;
import static org.kie.kogito.index.TestUtils.getTravelsProtoBufferFile;

@QuarkusTest
@QuarkusTestResource(DataIndexInfinispanServerTestResource.class)
class InfinispanWebSocketSubscriptionIT extends AbstractWebSocketSubscriptionIT {

    @Override
    protected String getProcessProtobufFileContent() throws Exception {
        return getTravelsProtoBufferFile();
    }

    @Override
    protected String getUserTaskProtobufFileContent() throws Exception {
        return getDealsProtoBufferFile();
    }
}
