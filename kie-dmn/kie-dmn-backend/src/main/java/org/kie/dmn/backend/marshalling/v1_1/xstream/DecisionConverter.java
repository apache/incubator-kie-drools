/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.backend.marshalling.v1_1.xstream;

import org.kie.dmn.model.v1_1.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class DecisionConverter extends DRGElementConverter {
    public static final String QUESTION = "question";
    public static final String ALLOWED_ANSWERS = "allowedAnswers";
    public static final String VARIABLE = "variable";
    public static final String INFORMATION_REQUIREMENT = "informationRequirement";
    public static final String KNOWLEDGE_REQUIREMENT = "knowledgeRequirement";
    public static final String AUTHORITY_REQUIREMENT = "authorityRequirement";
    public static final String SUPPORTED_OBJECTIVE = "supportedObjective";
    public static final String IMPACTED_PERFORMANCE_INDICATOR = "impactedPerformanceIndicator";
    public static final String DECISION_MAKER = "decisionMaker";
    public static final String DECISION_OWNER = "decisionOwner";
    public static final String USING_PROCESS = "usingProcess";
    public static final String USING_TASK = "usingTask";
    public static final String EXPRESSION = "expression";

    public DecisionConverter(XStream xstream) {
        super( xstream );
    }

    public boolean canConvert(Class clazz) {
        return clazz.equals( Decision.class );
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Decision dec = (Decision) parent;
        
        if (QUESTION.equals(nodeName)) {
            dec.setQuestion((String) child);
        } else if (ALLOWED_ANSWERS.equals(nodeName)) {
            dec.setAllowedAnswers((String) child);
        } else if (VARIABLE.equals(nodeName) ) {
            dec.setVariable( (InformationItem) child );
        } else if (INFORMATION_REQUIREMENT.equals(nodeName) ) {
            dec.getInformationRequirement().add( (InformationRequirement) child );
        } else if (KNOWLEDGE_REQUIREMENT.equals(nodeName) ) {
            dec.getKnowledgeRequirement().add((KnowledgeRequirement) child);
        } else if (AUTHORITY_REQUIREMENT.equals(nodeName) ) {
            dec.getAuthorityRequirement().add((AuthorityRequirement) child);
        } else if (SUPPORTED_OBJECTIVE.equals(nodeName) ) {
            dec.getSupportedObjective().add((DMNElementReference) child);
        } else if (IMPACTED_PERFORMANCE_INDICATOR.equals(nodeName) ) {
            dec.getImpactedPerformanceIndicator().add((DMNElementReference) child);
        } else if (DECISION_MAKER.equals(nodeName) ) {
            dec.getDecisionMaker().add((DMNElementReference) child);
        } else if (DECISION_OWNER.equals(nodeName) ) {
            dec.getDecisionOwner().add((DMNElementReference) child);
        } else if (USING_PROCESS.equals(nodeName) ) {
            dec.getUsingProcess().add((DMNElementReference) child);
        } else if (USING_TASK.equals(nodeName) ) {
            dec.getUsingTask().add((DMNElementReference) child);
        } else if ( child instanceof Expression ) {
            dec.setExpression( (Expression) child );
        } else {
            super.assignChildElement( dec, nodeName, child );
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
        
        // no attributes.
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new Decision();
    }
    
    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        Decision dec = (Decision) parent;
        
        if (dec.getQuestion() != null) writeChildrenNodeAsValue(writer, context, dec.getQuestion(), QUESTION);
        if (dec.getAllowedAnswers() != null) writeChildrenNodeAsValue(writer, context, dec.getAllowedAnswers(), ALLOWED_ANSWERS);
        if (dec.getVariable() != null) writeChildrenNode(writer, context, dec.getVariable(), VARIABLE);
        for ( InformationRequirement ir : dec.getInformationRequirement() ) {
            writeChildrenNode(writer, context, ir, INFORMATION_REQUIREMENT);
        }
        for ( KnowledgeRequirement kr : dec.getKnowledgeRequirement() ) {
            writeChildrenNode(writer, context, kr, KNOWLEDGE_REQUIREMENT);
        }
        for ( AuthorityRequirement ar : dec.getAuthorityRequirement() ) {
            writeChildrenNode(writer, context, ar, AUTHORITY_REQUIREMENT);
        }
        for ( DMNElementReference so : dec.getSupportedObjective() ) {
            writeChildrenNode(writer, context, so, SUPPORTED_OBJECTIVE);
        }
        for ( DMNElementReference ipi : dec.getImpactedPerformanceIndicator() ) {
            writeChildrenNode(writer, context, ipi, IMPACTED_PERFORMANCE_INDICATOR);
        }
        for ( DMNElementReference dm : dec.getDecisionMaker() ) {
            writeChildrenNode(writer, context, dm, DECISION_MAKER);
        }
        for ( DMNElementReference downer : dec.getDecisionOwner() ) {
            writeChildrenNode(writer, context, downer, DECISION_OWNER);
        }
        for ( DMNElementReference up : dec.getUsingProcess() ) {
            writeChildrenNode(writer, context, up, USING_PROCESS);
        }
        for ( DMNElementReference ut : dec.getUsingTask() ) {
            writeChildrenNode(writer, context, ut, USING_TASK);
        }
        if (dec.getExpression() != null) {
            Expression e = dec.getExpression();
            String nodeName = MarshallingUtils.defineExpressionNodeName(e);
            writeChildrenNode(writer, context, e, nodeName);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes 
    }
}
