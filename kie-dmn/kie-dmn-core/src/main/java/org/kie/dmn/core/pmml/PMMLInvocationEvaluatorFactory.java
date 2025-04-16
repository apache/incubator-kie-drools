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

import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.api.DMNElement;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PMMLInvocationEvaluatorFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PMMLInvocationEvaluatorFactory.class);

    private PMMLInvocationEvaluatorFactory() {
        // Constructing instances is not allowed for this Factory
    }

    public static AbstractPMMLInvocationEvaluator newInstance(DMNModelImpl model, ClassLoader classLoader, DMNElement funcDef, ModelLocalUriId pmmlModelLocalUriID, String pmmlModelName, PMMLInfo<?> pmmlInfo) {
        AbstractPMMLInvocationEvaluator toReturn = getDMNjPMMLInvocationEvaluator(classLoader, funcDef, pmmlModelLocalUriID, model, pmmlModelName);
        if (toReturn == null) {
            toReturn = getDMNKiePMMLTrustyInvocationEvaluator(model.getNamespace(), funcDef, pmmlModelLocalUriID, pmmlModelName, pmmlInfo);
        }
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
        return new DummyPMMLInvocationEvaluator(model.getNamespace(), funcDef, pmmlModelLocalUriID, pmmlModelName);
    }

    /**
     * Retrieve the required <code>DMNjPMMLInvocationEvaluator</code>. It may return <code>null</code>
     * if <code>org.kie.dmn.jpmml.DMNjPMMLInvocationEvaluator</code> is not in the classpath
     *
     * @param classLoader
     * @param funcDef
     * @param pmmlModelLocalUriID
     * @param model
     * @param pmmlModelName
     * @return
     */
    private static AbstractPMMLInvocationEvaluator getDMNjPMMLInvocationEvaluator(ClassLoader classLoader, DMNElement funcDef, ModelLocalUriId pmmlModelLocalUriID, DMNModelImpl model, String pmmlModelName) {
        try {
            @SuppressWarnings("unchecked")
            Class<AbstractPMMLInvocationEvaluator> cl = (Class<AbstractPMMLInvocationEvaluator>) classLoader.loadClass("org.kie.dmn.jpmml.DMNjPMMLInvocationEvaluator");
            return cl.getDeclaredConstructor(String.class,
                                             DMNElement.class,
                                             Resource.class,
                                             String.class)
                    .newInstance(model.getNamespace(),
                                 funcDef,
                                 EfestoPMMLUtils.getPmmlResourceFromContextStorage(pmmlModelLocalUriID),
                                 pmmlModelName);
        } catch (NoClassDefFoundError | ClassNotFoundException e) {
            LOG.warn("Tried binding org.kie:kie-dmn-jpmml, failed.");
        } catch (Throwable e) {
            LOG.warn("Binding org.kie:kie-dmn-jpmml succeded but initialization failed, with:", e);
        }
        return null;
    }

    /**
     * Retrieve the required <code>DMNKiePMMLTrustyInvocationEvaluator</code>. It may return <code>null</code>
     * if <code>org.drools:kie-pmml-trusty</code> is not in the classpath
     *
     * @param nameSpace
     * @param funcDef
     * @param pmmlModelLocalUriID
     * @param pmmlModel
     * @param pmmlInfo
     * @return
     */
    private static DMNKiePMMLTrustyInvocationEvaluator getDMNKiePMMLTrustyInvocationEvaluator(String nameSpace, DMNElement funcDef, ModelLocalUriId pmmlModelLocalUriID, String pmmlModel, PMMLInfo<?> pmmlInfo) {
        try {
            return new DMNKiePMMLTrustyInvocationEvaluator(nameSpace, funcDef, pmmlModelLocalUriID, pmmlModel, pmmlInfo);
        } catch (NoClassDefFoundError e) {
            LOG.warn("Tried binding org.drools:kie-pmml-trusty, failed.");
        } catch (Throwable e) {
            throw new RuntimeException("Binding org.drools:kie-pmml-trusty succeeded but initialization failed, with:", e);
        }
        return null;
    }
}