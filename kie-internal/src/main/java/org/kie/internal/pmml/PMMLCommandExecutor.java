/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.internal.pmml;

import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.Context;

public interface PMMLCommandExecutor {

    /**
     * Evaluate the given <code>PMMLRequestData<code>
     * @param pmmlRequestData : it must contain the pmml file name (in the <i>source</i> property)
     * and the model name
     * @return
     */
    PMML4Result execute(final PMMLRequestData pmmlRequestData, final Context context);
}
