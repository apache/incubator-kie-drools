/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.workflow.instance.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.drools.definition.process.Node;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.XPATHExpressionModifier;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.Assignment;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.ForEachNode.ForEachJoinNode;
import org.jbpm.workflow.core.node.ForEachNode.ForEachSplitNode;
import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.mvel2.MVEL;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Runtime counterpart of a for each node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class ForEachNodeInstance extends CompositeContextNodeInstance {

    private static final long serialVersionUID = 510l;
    
    public ForEachNode getForEachNode() {
        return (ForEachNode) getNode();
    }

    public NodeInstance getNodeInstance(final Node node) {
        // TODO do this cleaner for split / join of for each?
        if (node instanceof ForEachSplitNode) {
            ForEachSplitNodeInstance nodeInstance = new ForEachSplitNodeInstance();
            nodeInstance.setNodeId(node.getId());
            nodeInstance.setNodeInstanceContainer(this);
            nodeInstance.setProcessInstance(getProcessInstance());
            return nodeInstance;
        } else if (node instanceof ForEachJoinNode) {
            ForEachJoinNodeInstance nodeInstance = (ForEachJoinNodeInstance)
                getFirstNodeInstance(node.getId());
            if (nodeInstance == null) {
                nodeInstance = new ForEachJoinNodeInstance();
                nodeInstance.setNodeId(node.getId());
                nodeInstance.setNodeInstanceContainer(this);
                nodeInstance.setProcessInstance(getProcessInstance());
            }
            return nodeInstance;
        }
        return super.getNodeInstance(node);
    }
    
    public class ForEachSplitNodeInstance extends NodeInstanceImpl {

        private static final long serialVersionUID = 510l;
        
        public ForEachSplitNode getForEachSplitNode() {
            return (ForEachSplitNode) getNode();
        }

        public void internalTrigger(org.drools.runtime.process.NodeInstance fromm, String type) {
        	Map<String, Object> m = new HashMap<String, Object>();
            for (Iterator<DataAssociation> iterator = getForEachNode().getInMapping().iterator(); iterator.hasNext(); ) {
            	DataAssociation association = iterator.next();
            	String source = association.getSources().get(0);
            	String target = association.getTarget();
            	
    			if(source.equals(getForEachNode().getVariableName())) {
    				continue;
    			}

            	try {
            		for(Iterator<Assignment> it = association.getAssignments().iterator(); it.hasNext(); ) {
            			Assignment assignment = it.next();
            			String from = assignment.getFrom();
            			String to = assignment.getTo();

            			XPathFactory factory = XPathFactory.newInstance();
            			XPath xpathFrom = factory.newXPath();

            			XPathExpression exprFrom 
            			= xpathFrom.compile(from);

            			XPath xpathTo = factory.newXPath();

            			XPathExpression exprTo 
            			= xpathTo.compile(to);

            			VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
            			resolveContextInstance(VariableScope.VARIABLE_SCOPE, source);
            			
            			Element targetElem =  null;

            			if( m.get(target) == null) {
            				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            				Document doc = builder.newDocument();
            				targetElem = doc.createElement(target);
            				m.put(target, targetElem);
            			}

            			targetElem = (Element) m.get(target);
            			XPATHExpressionModifier modifier = new XPATHExpressionModifier();
            			modifier.insertMissingData(to, targetElem);

            			targetElem = ((Element)  exprTo.evaluate(m.get(target), XPathConstants.NODE));

            			NodeList nl = (NodeList)  exprFrom.evaluate(variableScopeInstance.getVariable(source), XPathConstants.NODESET);

            			for( int i =0 ; i<nl.getLength(); i++) {
            				org.w3c.dom.Node n  = targetElem.getOwnerDocument().importNode(nl.item(i), true);
            				if(n instanceof Attr) {
            					targetElem.setAttributeNode((Attr) n);
            				}
            				else {
            					targetElem.appendChild(n);
            				}
            			}

            		}
            	}
            	catch(Exception e) {
            		e.printStackTrace();
            		throw new RuntimeException(e);
            	}
            }

            String collectionExpression = getForEachNode().getCollectionExpression();
            Collection<?> collection = null;
            if(m.containsKey(collectionExpression)) {
            	collection = evaluateCollectionExpression((Element) m.get(collectionExpression));
            }
            else {
            	collection = evaluateCollectionExpression(collectionExpression);
            }
            ((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
            if (collection.isEmpty()) {
            	ForEachNodeInstance.this.triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
            } else {
            	for (Object o: collection) {
            		String variableName = getForEachNode().getVariableName();
            		NodeInstance nodeInstance = (NodeInstance)
            		((NodeInstanceContainer) getNodeInstanceContainer()).getNodeInstance(getForEachSplitNode().getTo().getTo());
            		VariableScopeInstance variableScopeInstance = null;
            		variableScopeInstance = (VariableScopeInstance)
            		nodeInstance.resolveContextInstance(VariableScope.VARIABLE_SCOPE, variableName);
            		variableScopeInstance.setVariable(variableName, o);
            		((org.jbpm.workflow.instance.NodeInstance) nodeInstance).trigger(this, getForEachSplitNode().getTo().getToType());
            	}
	            if (!getForEachNode().isWaitForCompletion()) {
	            	ForEachNodeInstance.this.triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, false);
	            }
            }
        }

        private Collection<?> evaluateCollectionExpression(String collectionExpression) {
            // TODO: should evaluate this expression using MVEL
        	Object collection = null;
            VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                resolveContextInstance(VariableScope.VARIABLE_SCOPE, collectionExpression);
            if (variableScopeInstance != null) {
            	collection = variableScopeInstance.getVariable(collectionExpression);
            } else {
            	try {
            		collection = MVEL.eval(collectionExpression, new NodeInstanceResolverFactory(this));
            	} catch (Throwable t) {
            		throw new IllegalArgumentException(
                        "Could not find collection " + collectionExpression);
            	}
                
            }
            if (collection == null) {
            	return Collections.EMPTY_LIST;
            }
            if (collection instanceof Collection<?>) {
            	return (Collection<?>) collection;
            }
            if (collection.getClass().isArray() ) {
            	List<Object> list = new ArrayList<Object>();
            	for (Object o: (Object[]) collection) {
            		list.add(o);
            	}
                return list;
            }
            throw new IllegalArgumentException(
        		"Unexpected collection type: " + collection.getClass());
        }
        
        private Collection<?> evaluateCollectionExpression(Element element) {
        	NodeList nl = element.getChildNodes();
        	List<Object> list = new ArrayList<Object>();
        	for (int i =0; i< nl.getLength(); i++) {
        		list.add(nl.item(i));
        	}
        	return list;    
        }
    }
    
    public class ForEachJoinNodeInstance extends NodeInstanceImpl {

        private static final long serialVersionUID = 510l;
        
        public ForEachJoinNode getForEachJoinNode() {
            return (ForEachJoinNode) getNode();
        }

        public void internalTrigger(org.drools.runtime.process.NodeInstance from, String type) {
            if (getNodeInstanceContainer().getNodeInstances().size() == 1) {
            	((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
                if (getForEachNode().isWaitForCompletion()) {
                	triggerConnection(getForEachJoinNode().getTo());
                }
            }
        }
        
    }
    
}
