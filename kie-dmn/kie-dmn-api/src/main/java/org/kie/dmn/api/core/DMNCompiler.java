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
package org.kie.dmn.api.core;

import java.io.Reader;
import java.util.Collection;
import java.util.Collections;

import org.kie.api.io.Resource;
import org.kie.dmn.model.api.Definitions;

public interface DMNCompiler {

    default DMNModel compile(Resource resource) {
        return compile(resource, Collections.emptyList());
    }

    DMNModel compile(Resource resource, Collection<DMNModel> dmnModels);

    default DMNModel compile(Reader source) {
        return compile(source, Collections.emptyList());
    }

    DMNModel compile(Reader source, Collection<DMNModel> dmnModels);

    default DMNModel compile(Definitions dmndefs) {
        return compile(dmndefs, Collections.emptyList());
    }

    DMNModel compile(Definitions dmndefs, Collection<DMNModel> dmnModels);

    /**
     * As {@link #compile(Definitions, Collection)}, but links {@link Resource} to the manually provided {@link Definitions} while compiling the {@link DMNModel}.
     */
    DMNModel compile(Definitions dmndefs, Resource resource, Collection<DMNModel> dmnModels);

}
