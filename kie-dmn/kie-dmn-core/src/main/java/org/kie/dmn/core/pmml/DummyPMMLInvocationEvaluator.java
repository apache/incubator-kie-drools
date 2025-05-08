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
package org.kie.dmn.core.pmml;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.EvaluatorResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.ast.EvaluatorResultImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.api.DMNElement;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyPMMLInvocationEvaluator extends AbstractPMMLInvocationEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(DummyPMMLInvocationEvaluator.class);

    public DummyPMMLInvocationEvaluator(String dmnNS, DMNElement node, ModelLocalUriId pmmlModelLocalUriID, String model) {
        super(dmnNS, node, pmmlModelLocalUriID, model);
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult result) {
        MsgUtil.reportMessage(LOG,
                              DMNMessage.Severity.ERROR,
                              node,
                              ((DMNResultImpl) result),
                              null,
                              null,
                              Msg.FUNC_DEF_PMML_NOT_SUPPORTED,
                              node.getIdentifierString());
        return new EvaluatorResultImpl(null, EvaluatorResult.ResultType.FAILURE);
    }

}