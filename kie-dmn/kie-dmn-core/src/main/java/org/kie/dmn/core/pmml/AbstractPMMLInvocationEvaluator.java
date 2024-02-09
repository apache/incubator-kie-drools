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
package org.kie.dmn.core.pmml;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator.FormalParameter;
import org.kie.dmn.core.ast.EvaluatorResultImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.api.DMNElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPMMLInvocationEvaluator implements DMNExpressionEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPMMLInvocationEvaluator.class);

    protected final String dmnNS;
    protected final DMNElement node;
    protected final List<FormalParameter> parameters = new ArrayList<>();
    protected final Resource documentResource;
    protected final String model;


    public AbstractPMMLInvocationEvaluator(String dmnNS, DMNElement node, Resource resource, String model) {
        this.dmnNS = dmnNS;
        this.node = node;
        this.documentResource = resource;
        this.model = model;
    }

    protected static Object getValueForPMMLInput(DMNResult r, String name) {
        Object pValue = r.getContext().get(name);
        if (pValue instanceof BigDecimal) {
            return ((BigDecimal) pValue).doubleValue();
        }
        return pValue;
    }

    public DMNType getParameterType(String name) {
        for (FormalParameter fp : parameters) {
            if (fp.name.equals(name)) {
                return fp.type;
            }
        }
        return null;
    }

    public List<List<String>> getParameterNames() {
        return Collections.singletonList(parameters.stream().map(p -> p.name).collect(Collectors.toList()));
    }

    public List<List<DMNType>> getParameterTypes() {
        return Collections.singletonList(parameters.stream().map(p -> p.type).collect(Collectors.toList()));
    }

    public void addParameter(String name, DMNType dmnType) {
        this.parameters.add(new FormalParameter(name, dmnType));
    }

    public static class DummyPMMLInvocationEvaluator extends AbstractPMMLInvocationEvaluator {

        public DummyPMMLInvocationEvaluator(String dmnNS, DMNElement node, Resource url, String model) {
            super(dmnNS, node, url, model);
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
            return new EvaluatorResultImpl(null, ResultType.FAILURE);
        }
    }

    public static class PMMLInvocationEvaluatorFactory {

        private PMMLInvocationEvaluatorFactory() {
            // Constructing instances is not allowed for this Factory
        }

        public static AbstractPMMLInvocationEvaluator newInstance(DMNModelImpl model, ClassLoader classLoader, DMNElement funcDef, Resource pmmlResource, String pmmlModel, PMMLInfo<?> pmmlInfo) {
            try {
                @SuppressWarnings("unchecked")
                Class<AbstractPMMLInvocationEvaluator> cl = (Class<AbstractPMMLInvocationEvaluator>) classLoader.loadClass("org.kie.dmn.jpmml.DMNjPMMLInvocationEvaluator");
                return cl.getDeclaredConstructor(String.class,
                                                 DMNElement.class,
                                                 Resource.class,
                                                 String.class)
                        .newInstance(model.getNamespace(),
                                     funcDef,
                                     pmmlResource,
                                     pmmlModel);
            } catch (NoClassDefFoundError | ClassNotFoundException e) {
                LOG.warn("Tried binding org.kie:kie-dmn-jpmml, failed.");
            } catch (Throwable e) {
                LOG.warn("Binding org.kie:kie-dmn-jpmml succeded but initialization failed, with:", e);
            }
            DMNKiePMMLTrustyInvocationEvaluator toReturn =  getDMNKiePMMLTrustyInvocationEvaluator(model.getNamespace(), funcDef, pmmlResource, pmmlModel, pmmlInfo);
            if (toReturn != null) {
                return toReturn;
            } else {
                MsgUtil.reportMessage(LOG,
                                      DMNMessage.Severity.WARN,
                                      funcDef,
                                      model,
                                      null,
                                      null,
                                      Msg.FUNC_DEF_PMML_NOT_SUPPORTED,
                                      funcDef.getIdentifierString());
            }
            return new AbstractPMMLInvocationEvaluator.DummyPMMLInvocationEvaluator(model.getNamespace(), funcDef, pmmlResource, pmmlModel);
        }
    }

    /**
     * Retrieve the required <code>DMNKiePMMLTrustyInvocationEvaluator</code>. It may return <code>null</code>
     * if <code>org.drools:kie-pmml-trusty</code> is not in the classpath
     *
     * @param nameSpace
     * @param funcDef
     * @param pmmlResource
     * @param pmmlModel
     * @param pmmlInfo
     * @return
     */
    private static DMNKiePMMLTrustyInvocationEvaluator getDMNKiePMMLTrustyInvocationEvaluator(String nameSpace, DMNElement funcDef, Resource pmmlResource, String pmmlModel, PMMLInfo<?> pmmlInfo) {
        try {
            return new DMNKiePMMLTrustyInvocationEvaluator(nameSpace, funcDef, pmmlResource, pmmlModel, pmmlInfo);
        } catch (NoClassDefFoundError e) {
            LOG.warn("Tried binding org.drools:kie-pmml-trusty, failed.");
        } catch (Throwable e) {
            throw new RuntimeException("Binding org.drools:kie-pmml-trusty succeeded but initialization failed, with:", e);
        }
        return null;
    }

}
