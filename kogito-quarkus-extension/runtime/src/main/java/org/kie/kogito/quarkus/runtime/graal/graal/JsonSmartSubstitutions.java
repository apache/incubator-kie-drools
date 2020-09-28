/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.kie.kogito.quarkus.runtime.graal.graal;

import java.io.IOException;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.mapper.JsonSmartMappingProvider;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import net.minidev.json.JSONStyle;
import net.minidev.json.reader.BeansWriterASM;


@TargetClass(JsonSmartMappingProvider.class)
final class JsonSmartMappingProviderTarget {

    @Substitute
    public <T> T map(Object source, Class<T> targetType, Configuration configuration) {
        throw new UnsupportedOperationException("this path is never taken");
    }
}

@TargetClass(BeansWriterASM.class)
final class BeansWriterASMTarget {
    @Substitute
    public <E> void writeJSONString(E value, Appendable out, JSONStyle compression) throws IOException {
        out.append(value.toString());
    }
}
