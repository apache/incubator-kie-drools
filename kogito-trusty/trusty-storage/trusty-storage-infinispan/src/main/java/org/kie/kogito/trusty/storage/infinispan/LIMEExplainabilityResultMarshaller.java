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
package org.kie.kogito.trusty.storage.infinispan;

import java.io.IOException;
import java.util.ArrayList;

import org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityStatus;
import org.kie.kogito.trusty.storage.api.model.LIMEExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.SaliencyModel;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LIMEExplainabilityResultMarshaller extends AbstractModelMarshaller<LIMEExplainabilityResult> {

    public LIMEExplainabilityResultMarshaller(ObjectMapper mapper) {
        super(mapper, LIMEExplainabilityResult.class);
    }

    @Override
    public LIMEExplainabilityResult readFrom(ProtoStreamReader reader) throws IOException {
        return new LIMEExplainabilityResult(
                reader.readString(BaseExplainabilityResult.EXECUTION_ID_FIELD),
                enumFromString(reader.readString(BaseExplainabilityResult.STATUS_FIELD), ExplainabilityStatus.class),
                reader.readString(BaseExplainabilityResult.STATUS_DETAILS_FIELD),
                reader.readCollection(LIMEExplainabilityResult.SALIENCIES_FIELD, new ArrayList<>(), SaliencyModel.class));
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, LIMEExplainabilityResult input) throws IOException {
        writer.writeString(BaseExplainabilityResult.EXECUTION_ID_FIELD, input.getExecutionId());
        writer.writeString(BaseExplainabilityResult.STATUS_FIELD, stringFromEnum(input.getStatus()));
        writer.writeString(BaseExplainabilityResult.STATUS_DETAILS_FIELD, input.getStatusDetails());
        writer.writeCollection(LIMEExplainabilityResult.SALIENCIES_FIELD, input.getSaliencies(), SaliencyModel.class);
    }
}
