/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workflow.core.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.jbpm.process.instance.KogitoProcessContextImpl;
import org.jbpm.process.instance.impl.AssignmentAction;
import org.jbpm.process.instance.impl.AssignmentProducer;
import org.jbpm.workflow.core.node.Assignment;
import org.jbpm.workflow.core.node.Transformation;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.kie.api.runtime.process.DataTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * this class allows to simplify input output processing as a cross cutting concern (actions or node themselves)
 *
 */
public class NodeIoHelper {
    protected static final Logger logger = LoggerFactory.getLogger(NodeIoHelper.class);

    private NodeInstanceImpl nodeInstance;

    public NodeIoHelper(NodeInstanceImpl nodeInstance) {
        this.nodeInstance = nodeInstance;
    }

    public void processInputs(Collection<DataAssociation> dataAssociation, Function<String, Object> sourceResolver, Function<String, Object> targetResolver, AssignmentProducer producer) {
        this.processDataAssociations(dataAssociation, sourceResolver, targetResolver, producer);
    }

    public void processOutputs(Collection<DataAssociation> dataAssociation, Function<String, Object> sourceResolver, Function<String, Object> targetResolver, AssignmentProducer producer) {
        this.processDataAssociations(dataAssociation, sourceResolver, targetResolver, producer);
    }

    private void processDataAssociations(
            Collection<DataAssociation> dataAssociation,
            Function<String, Object> sourceResolver,
            Function<String, Object> targetResolver,
            AssignmentProducer producer) {

        for (Iterator<DataAssociation> iterator = dataAssociation.iterator(); iterator.hasNext();) {
            DataAssociation mapping = iterator.next();
            Map<String, Object> sources = new HashMap<>();
            // this mapping it is only useful so we use the label name instead of id
            mapping.getSources().forEach(source -> {
                Object value = sourceResolver.apply(source.getLabel());
                sources.put(source.getLabel(), value);
            });
            processDataAssociation(mapping, sources, sourceResolver, targetResolver, producer);
        }
    }

    private void processDataAssociation(
            DataAssociation mapping,
            Map<String, Object> dataSet,
            Function<String, Object> sourceResolver,
            Function<String, Object> targetResolver,
            AssignmentProducer producer) {
        try {
            if (mapping.getTransformation() != null) {
                Transformation transformation = mapping.getTransformation();
                DataTransformer transformer = DataTransformerRegistry.get().find(transformation.getLanguage());
                Object parameterValue = null;
                if (transformer != null) {
                    parameterValue = transformer.transform(transformation.getCompiledExpression(), dataSet);
                }
                if (parameterValue != null) {
                    producer.accept(mapping.getTarget().getLabel(), parameterValue);
                }
            } else if (mapping.getAssignments() == null || mapping.getAssignments().isEmpty()) {
                // if no assignments copy source to target
                producer.accept(mapping.getTarget().getLabel(), dataSet.get(mapping.getSources().get(0).getLabel()));
            } else {

                mapping.getAssignments().forEach(a -> {
                    this.handleAssignment(a, sourceResolver, targetResolver, producer);
                });

            }
        } catch (Throwable th) {
            logger.debug("there was an error during data association processing", th);
            throw th;
        }
    }

    private void handleAssignment(Assignment assignment, Function<String, Object> sourceResolver, Function<String, Object> targetResolver, AssignmentProducer producer) {
        AssignmentAction action = (AssignmentAction) assignment.getMetaData("Action");
        try {
            KogitoProcessContextImpl context = nodeInstance != null ? new KogitoProcessContextImpl(nodeInstance.getProcessInstance().getKnowledgeRuntime()) : new KogitoProcessContextImpl(null);
            context.setNodeInstance(nodeInstance);
            action.execute(sourceResolver, targetResolver, producer);
        } catch (Exception e) {
            throw new RuntimeException("Unable to execute Assignment", e);
        }
    }

    public static Map<String, Object> processInputs(NodeInstanceImpl nodeInstanceImpl,
            Function<String, Object> sourceResolver,
            Function<String, Object> targetResolver) {

        Function<String, Object> varResolverWrapper = (varRef) -> {
            switch (varRef) {
                case "nodeInstance":
                    return nodeInstanceImpl;
                case "processInstance":
                    return nodeInstanceImpl.getProcessInstance();
                case "processInstanceId":
                    return nodeInstanceImpl.getKogitoProcessInstance().getStringId();
                case "parentProcessInstanceId":
                    return nodeInstanceImpl.getKogitoProcessInstance().getParentProcessInstanceStringId();
                default:
                    return sourceResolver.apply(varRef);
            }
        };

        // for inputs resolve it is supposed to create object by default constructor (that is the reason is null
        return processInputs(nodeInstanceImpl, ((NodeImpl) nodeInstanceImpl.getNode()).getInAssociations(), varResolverWrapper, targetResolver);
    }

    public static Map<String, Object> processInputs(NodeInstanceImpl nodeInstanceImpl,
            List<DataAssociation> dataInputAssociation,
            Function<String, Object> sourceResolver,
            Function<String, Object> targetResolver) {

        NodeIoHelper ioHelper = new NodeIoHelper(nodeInstanceImpl);
        Map<String, Object> inputSet = new HashMap<>();
        // for inputs resolve it is supposed to create object by default constructor (that is the reason is null
        ioHelper.processInputs(dataInputAssociation, sourceResolver, targetResolver, (target, value) -> inputSet.put(target, value));
        return inputSet;
    }

    public static Map<String, Object> processInputs(NodeInstanceImpl nodeInstanceImpl, Function<String, Object> soureResolver) {
        return processInputs(nodeInstanceImpl, soureResolver, key -> null);
    }

    public static void processOutputs(NodeInstanceImpl nodeInstanceImpl, Function<String, Object> sourceResolver, Function<String, Object> targetResolver) {
        processOutputs(nodeInstanceImpl, ((NodeImpl) nodeInstanceImpl.getNode()).getOutAssociations(), sourceResolver, targetResolver);
    }

    public static void processOutputs(NodeInstanceImpl nodeInstanceImpl,
            List<DataAssociation> dataOutputAssociations,
            Function<String, Object> sourceResolver,
            Function<String, Object> targetResolver) {

        NodeIoHelper ioHelper = new NodeIoHelper(nodeInstanceImpl);
        ioHelper.processOutputs(dataOutputAssociations, sourceResolver, targetResolver, new DefaultAssignmentProducer(nodeInstanceImpl));
    }

    public static Map<String, Object> processOutputs(List<DataAssociation> dataOutputAssociations, Function<String, Object> sourceResolver) {
        NodeIoHelper ioHelper = new NodeIoHelper(null);
        Map<String, Object> outputSet = new HashMap<>();
        ioHelper.processOutputs(dataOutputAssociations, sourceResolver, key -> null, (key, value) -> outputSet.put(key, value));
        return outputSet;
    }

}
