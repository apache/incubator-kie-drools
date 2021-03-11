/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.command.runtime.pmml;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.core.command.IdentifiableResult;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLConstants;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;
import org.kie.internal.pmml.PMMLCommandExecutorFactory;
import org.kie.internal.ruleunit.RuleUnitComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.internal.pmml.PMMLImplementationsUtil.toEnable;

@XmlRootElement(name = "apply-pmml-model-command")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplyPmmlModelCommand implements ExecutableCommand<PMML4Result>,
                                              IdentifiableResult {

    private static Logger logger = LoggerFactory.getLogger(ApplyPmmlModelCommand.class);

    private static final long serialVersionUID = 19630331;
    @XmlAttribute(name = "outIdentifier")
    private String outIdentifier;
    @XmlAttribute(name = "packageName")
    private String packageName;
    @XmlAttribute(name = "hasMining")
    private Boolean hasMining;
    @XmlElement(name = "requestData")
    private PMMLRequestData requestData;
    @XmlElements(
            @XmlElement(name = "complexInputObject", type = Object.class)
    )
    private List<Object> complexInputObjects;

    public ApplyPmmlModelCommand() {
        // Necessary for JAXB
        super();
    }

    public ApplyPmmlModelCommand(PMMLRequestData requestData) {
        initialize(requestData, null, null);
    }

    public ApplyPmmlModelCommand(PMMLRequestData requestData, List<Object> complexInputList) {
        initialize(requestData, complexInputList, null);
    }

    public ApplyPmmlModelCommand(PMMLRequestData requestData, List<Object> complexInputList, Boolean hasMining) {
        initialize(requestData, complexInputList, hasMining);
    }

    private void initialize(PMMLRequestData requestData, List<Object> complexInputList, Boolean hasMining) {
        this.requestData = requestData;
        this.complexInputObjects = complexInputList != null ? new ArrayList(complexInputList) : new ArrayList<>();
        this.hasMining = hasMining != null ? hasMining : Boolean.FALSE;
    }

    public PMMLRequestData getRequestData() {
        return requestData;
    }

    public void setRequestData(PMMLRequestData requestData) {
        this.requestData = requestData;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Boolean getHasMining() {
        return hasMining;
    }

    public void setHasMining(Boolean hasMining) {
        this.hasMining = hasMining;
    }

    public boolean isMining() {
        return hasMining != null && hasMining;
    }

    public void addComplexInputObject(Object o) {
        if (o != null) {
            this.complexInputObjects.add(o);
        }
    }

    @Override
    public String getOutIdentifier() {
        return outIdentifier;
    }

    @Override
    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    @Override
    public PMML4Result execute(Context context) {
        if (requestData == null) {
            throw new IllegalStateException("ApplyPmmlModelCommand requires request data (PMMLRequestData) to execute");
        }
        final PMMLConstants toInvoke = getToInvoke(context);
        switch (toInvoke) {
            case NEW:
                return executePMMLTrusty(context);
            case LEGACY:
                return executePMMLLegacy(context);
            default:
                throw new IllegalArgumentException("Unmanaged PMMLConstants " + toInvoke);
        }
    }

    protected PMMLConstants getToInvoke(Context context) {
        ClassLoader classLoader = getClassLoader((RegistryContext) context);
        return toEnable(classLoader);
    }

    private ClassLoader getClassLoader(RegistryContext registryContext) {
        try {
            KieSession kieSession = registryContext.lookup(KieSession.class);
            InternalKnowledgeBase kieBase = (InternalKnowledgeBase) kieSession.getKieBase();
            return kieBase.getRootClassLoader();
        } catch (Exception e) {
            logger.warn("Impossible to retrieve RootClassLoader, using ContextClassLoader {}", e.getMessage(), e);
            return Thread.currentThread().getContextClassLoader();
        }
    }

    protected PMML4Result executePMMLLegacy(Context context) {
        return RuleUnitComponentFactory.get().newApplyPmmlModelCommandExecutor().execute(context, requestData,
                                                                                         complexInputObjects,
                                                                                         packageName, isMining());
    }

    protected PMML4Result executePMMLTrusty(Context context) {
        RegistryContext registryContext = (RegistryContext) context;
        PMML4Result toReturn = PMMLCommandExecutorFactory.get().newPMMLCommandExecutor().execute(requestData, context);
        // Needed to update the ExecutionResultImpl and the Registry context,
        // as done inside legacy implementation
        Optional<ExecutionResultImpl> execRes = Optional.ofNullable(registryContext.lookup(ExecutionResultImpl.class));
        registryContext.register(PMML4Result.class, toReturn);
        execRes.ifPresent(result -> result.setResult("results", toReturn));
        return toReturn;
    }
}
