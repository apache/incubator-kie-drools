/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workflow.core.node;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Supplier;

import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.AbstractContext;
import org.jbpm.process.core.impl.ContextContainerImpl;
import org.kie.api.definition.process.Connection;
import org.kie.api.runtime.KieRuntime;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.rules.RuleUnitMemory;

/**
 * Default implementation of a RuleSet node.
 */
public class RuleSetNode extends StateBasedNode implements ContextContainer {


    public static abstract class RuleType implements Serializable {

        private static final String UNIT_RULEFLOW_PREFIX = "unit:";

        public static RuleType of(String name, String language) {
            if (language.equals(DRL_LANG)) {
                return parseRuleFlowGroup(name);
            } else if (language.equals(RULE_UNIT_LANG)){
                return ruleUnit(name);
            } else {
                throw new IllegalArgumentException("Unsupported language " + language);
            }
        }

        private static RuleType parseRuleFlowGroup(String name) {
            if (name.startsWith(UNIT_RULEFLOW_PREFIX)) {
                String unitId = name.substring(UNIT_RULEFLOW_PREFIX.length());
                return ruleUnit(unitId);
            }
            return ruleFlowGroup(name);
        }

        public static RuleFlowGroup ruleFlowGroup(String name) {
            return new RuleFlowGroup(name);
        }

        public static RuleUnit ruleUnit(String name) {
            return new RuleUnit(name);
        }

        public static Decision decision(String namespace, String model, String decision) {
            return new Decision(namespace, model, decision);
        }


        protected String name;

        private RuleType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean isRuleFlowGroup() {
            return false;
        }

        public boolean isRuleUnit() {
            return false;
        }

        public boolean isDecision() {
            return false;
        }

        public static class RuleFlowGroup extends RuleType {

            private RuleFlowGroup(String name) {
                super(name);
            }

            @Override
            public boolean isRuleFlowGroup() {
                return true;
            }

            @Override
            public String toString() {
                return new StringJoiner(", ", RuleFlowGroup.class.getSimpleName() + "[", "]")
                        .add("name='" + name + "'")
                        .toString();
            }
        }

        public static class RuleUnit extends RuleType {

            private RuleUnit(String name) {
                super(name);
            }

            @Override
            public boolean isRuleUnit() {
                return true;
            }

            @Override
            public String toString() {
                return new StringJoiner(", ", RuleUnit.class.getSimpleName() + "[", "]")
                        .add("name='" + name + "'")
                        .toString();
            }
        }

        public static class Decision extends RuleType {

            private String namespace;
            private String decision;

            private Decision(String namespace, String model, String decision) {
                super(model);
                this.namespace = namespace;
                this.decision = decision;
            }

            @Override
            public boolean isDecision() {
                return true;
            }

            public String getNamespace() {
                return namespace;
            }

            public String getModel() {
                return getName();
            }

            public String getDecision() {
                return decision;
            }

            @Override
            public String toString() {
                return new StringJoiner(", ", Decision.class.getSimpleName() + "[", "]")
                        .add("namespace='" + namespace + "'")
                        .add("model='" + name + "'")
                        .add("decision='" + decision + "'")
                        .toString();
            }
        }
    }

    private static final long serialVersionUID = 510l;

    public static final String DRL_LANG = "http://www.jboss.org/drools/rule";
    public static final String RULE_UNIT_LANG = "http://www.jboss.org/drools/rule-unit";
    public static final String DMN_LANG = "http://www.jboss.org/drools/dmn";

    private String language = DRL_LANG;

    // NOTE: ContetxInstances are not persisted as current functionality (exception scope) does not require it
    private ContextContainer contextContainer = new ContextContainerImpl();
    
    private RuleType ruleType;

    private List<DataAssociation> inMapping = new LinkedList<DataAssociation>();
    private List<DataAssociation> outMapping = new LinkedList<DataAssociation>();
    
    private Map<String, Object> parameters = new HashMap<String, Object>();
    
    private Supplier<DMNRuntime> dmnRuntime;
    private Supplier<KieRuntime> kieRuntime;
    private RuleUnitFactory<RuleUnitMemory> ruleUnitFactory;

    public void setRuleType(RuleType ruleType) {
        this.ruleType = ruleType;
    }

    public RuleType getRuleType() {
        return ruleType;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }

    public Supplier<DMNRuntime> getDmnRuntime() {
        return dmnRuntime;
    }
    
    public void setDmnRuntime(Supplier<DMNRuntime> dmnRuntime) {
        this.dmnRuntime = dmnRuntime;
    }
    
    public Supplier<KieRuntime> getKieRuntime() {
        return kieRuntime;
    }

    public RuleUnitFactory<RuleUnitMemory> getRuleUnitFactory() {
        return ruleUnitFactory;
    }

    public void setRuleUnitFactory(RuleUnitFactory<?> ruleUnitFactory) {
        this.ruleUnitFactory = (RuleUnitFactory<RuleUnitMemory>) ruleUnitFactory;
    }

    public void setKieRuntime(Supplier<KieRuntime> kieRuntime) {
        this.kieRuntime = kieRuntime;
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
        	throw new IllegalArgumentException(
                    "This type of node [" + connection.getTo().getMetaData().get("UniqueId") + ", " + connection.getTo().getName() 
                    + "] only accepts default incoming connection type!");
        }
        if (getFrom() != null && !"true".equals(System.getProperty("jbpm.enable.multi.con"))) {
        	throw new IllegalArgumentException(
                    "This type of node [" + connection.getTo().getMetaData().get("UniqueId") + ", " + connection.getTo().getName() 
                    + "] cannot have more than one incoming connection!");
        }
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
        	throw new IllegalArgumentException(
                    "This type of node [" + connection.getFrom().getMetaData().get("UniqueId") + ", " + connection.getFrom().getName() 
                    + "] only accepts default outgoing connection type!");
        }
        if (getTo() != null && !"true".equals(System.getProperty("jbpm.enable.multi.con"))) {
        	throw new IllegalArgumentException(
                    "This type of node [" + connection.getFrom().getMetaData().get("UniqueId") + ", " + connection.getFrom().getName() 
                    + "] cannot have more than one outgoing connection!");
        }
    }
    
    public void addInMapping(String parameterName, String variableName) {
        inMapping.add(new DataAssociation(variableName, parameterName, null, null));
    }

    public void setInMappings(Map<String, String> inMapping) {
        this.inMapping = new LinkedList<DataAssociation>();
        for(Map.Entry<String, String> entry : inMapping.entrySet()) {
            addInMapping(entry.getKey(), entry.getValue());
        }
    }

    public String getInMapping(String parameterName) {
        return getInMappings().get(parameterName);
    }
    
    public Map<String, String> getInMappings() {
        Map<String,String> in = new HashMap<String, String>(); 
        for(DataAssociation a : inMapping) {
            if(a.getSources().size() ==1 && (a.getAssignments() == null || a.getAssignments().size()==0) && a.getTransformation() == null) {
                in.put(a.getTarget(), a.getSources().get(0));
            }
        }
        return in;
    }

    public void addInAssociation(DataAssociation dataAssociation) {
        inMapping.add(dataAssociation);
    }

    public List<DataAssociation> getInAssociations() {
        return Collections.unmodifiableList(inMapping);
    }
    
    public void addOutMapping(String parameterName, String variableName) {
        outMapping.add(new DataAssociation(parameterName, variableName, null, null));
    }

    public void setOutMappings(Map<String, String> outMapping) {
        this.outMapping = new LinkedList<DataAssociation>();
        for(Map.Entry<String, String> entry : outMapping.entrySet()) {
            addOutMapping(entry.getKey(), entry.getValue());
        }
    }

    public String getOutMapping(String parameterName) {
        return getOutMappings().get(parameterName);
    }
    
    public Map<String, String> getOutMappings() {
        Map<String,String> out = new HashMap<String, String>(); 
        for(DataAssociation a : outMapping) {
            if(a.getSources().size() ==1 && (a.getAssignments() == null || a.getAssignments().size()==0) && a.getTransformation() == null) {
                out.put(a.getSources().get(0), a.getTarget());
            }
        }
        return out;
    }
    
    public void addOutAssociation(DataAssociation dataAssociation) {
        outMapping.add(dataAssociation);
    }

    public List<DataAssociation> getOutAssociations() {
        return Collections.unmodifiableList(outMapping);
    }

    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public void setParameter(String param, Object value) {
        this.parameters.put(param, value);
    }
    
    public Object getParameter(String param) {
        return this.parameters.get(param);
    }
    
    public Object removeParameter(String param) {
        return this.parameters.remove(param);
    }
    
    public boolean isDMN() {
        return DMN_LANG.equals(language);
    }

    public List<Context> getContexts(String contextType) {
        return contextContainer.getContexts(contextType);
    }

    public void addContext(Context context) {
        ((AbstractContext) context).setContextContainer(this);
        contextContainer.addContext(context);
    }

    public Context getContext(String contextType, long id) {
        return contextContainer.getContext(contextType, id);
    }

    public void setDefaultContext(Context context) {
        ((AbstractContext) context).setContextContainer(this);
        contextContainer.setDefaultContext(context);
    }

    public Context getDefaultContext(String contextType) {
        return contextContainer.getDefaultContext(contextType);
    }

    @Override
    public Context getContext(String contextId) {
        Context context = getDefaultContext(contextId);
        if (context != null) {
            return context;
        }
        return super.getContext(contextId);
    }
}
