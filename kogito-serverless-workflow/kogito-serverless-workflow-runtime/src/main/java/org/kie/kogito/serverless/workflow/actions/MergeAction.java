/*
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
package org.kie.kogito.serverless.workflow.actions;

import org.jbpm.process.instance.impl.Action;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.MergeUtils;

public class MergeAction implements Action {

    protected String inputName;
    protected String outputName;

    public MergeAction(String inputName, String outputName) {
        this.inputName = inputName;
        this.outputName = outputName;
    }

    @Override
    public void execute(KogitoProcessContext context) throws Exception {
        context.setVariable(outputName, MergeUtils.merge(ActionUtils.getJsonNode(context, inputName), ActionUtils.getJsonNode(context, outputName)));
    }
}
