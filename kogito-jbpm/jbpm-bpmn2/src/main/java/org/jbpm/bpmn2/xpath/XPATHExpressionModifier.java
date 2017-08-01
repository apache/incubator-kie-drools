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

package org.jbpm.bpmn2.xpath;

/**
 * A helper utility that modifies XPath Expression in-place.
 */
public class XPATHExpressionModifier {

//	/**
//	 * Creates a new XPATHExpressionModifier object.
//	 *
//	 * @param contextUris
//	 * @param namePool
//	 */
//	public XPATHExpressionModifier() {
//	}
//
//	/**
//	 * Insert nodes into the specified XPath expression wherever
//	 * required 
//	 * <p>
//	 * To be precise, a node is added to its parent if:
//	 * a) the node is an element...
//	 * b) that corresponds to an step...
//	 * c) that has a child axis...
//	 * d) whose parent had no children with its name...
//	 * e) and all preceding steps are element name tests.
//	 * </p>
//	 * @param xpathExpr
//	 * @param namePool
//	 *
//	 * @throws DOMException
//	 * @throws TransformerException
//	 * @throws XPathExpressionException 
//	 */
//	public Node insertMissingData(String xpath, Node contextNode)
//	throws DOMException, TransformerException, XPathExpressionException {
//	    if (xpath.startsWith("/") && contextNode == null) {
//	        xpath = xpath.substring(1);
//	    }
//	    Node rootNode = contextNode;
//	    if (contextNode != null) {
//	        contextNode = contextNode.getOwnerDocument();
//	    }
//	    
//		XPathFactory xpf = new XPathFactoryImpl();
//		XPath xpe = xpf.newXPath();    	
//		XPathExpression xpathExpr = xpe.compile(xpath);
//
////		if (contextNode == null || !(contextNode instanceof Element) ||
////				!(xpathExpr instanceof XPathExpressionImpl)) {
////			return;
////		}
//
//		Expression expression = ((XPathExpressionImpl) xpathExpr).getInternalExpression();
//
//		Document document = toDOMDocument(contextNode);
//
//		PathExpression pathExpr = null;
//		Expression step = null;
//
//		if (expression instanceof PathExpression) {
//			pathExpr = (PathExpression) expression;
//			step = pathExpr.getFirstStep();
//		} else if (expression instanceof AxisExpression) {
//			pathExpr = null;
//			step = (AxisExpression) expression;
//		} else {
//			return contextNode;
//		}
//
//		while (step != null) {
//			if (step instanceof AxisExpression) {
//				AxisExpression axisExpr = (AxisExpression) step;
//
//				NodeTest nodeTest = axisExpr.getNodeTest();
//
//				if (!(nodeTest instanceof NameTest)) {
//					break;
//				}
//
//				NameTest nameTest = (NameTest) nodeTest;
//
//				QName childName = getQualifiedName(nameTest.getFingerprint(),
//						((XPathFactoryImpl) xpf).getConfiguration().getNamePool()/*, contextUris*/);
//
//				if (Axis.CHILD == axisExpr.getAxis()) {
//					if (NodeKindTest.ELEMENT.getNodeKindMask() != nameTest.getNodeKindMask()) {
//						break;
//					}
//					if (contextNode == null) {
//					    contextNode = document.createElementNS(childName.getNamespaceURI(), childName.getLocalPart());
//					    document.appendChild(contextNode);
//					    rootNode = contextNode;
//					} else {
//					    
//					    NodeList children = null;
//					    if (contextNode instanceof Element) {
//					        children = ((Element) contextNode).getElementsByTagNameNS(childName.getNamespaceURI(),
//					            childName.getLocalPart());
//					    } else if (contextNode instanceof Document) {
//					        children = ((Document) contextNode).getElementsByTagNameNS(childName.getNamespaceURI(),
//	                                childName.getLocalPart());
//					    } else {
//					        throw new IllegalArgumentException(contextNode + " is of unsupported type");
//					    }
//					    if ((children == null) || (children.getLength() == 0)) {
//					        Node child = document.createElementNS(childName.getNamespaceURI(),
//					                getQualifiedName(childName));
//					        Document currentDoc = (Document) (contextNode instanceof Document ? contextNode : contextNode.getOwnerDocument());
//					        
//					        contextNode.appendChild(currentDoc.importNode(child, true));
//					        contextNode = child;
//					    } else if (children.getLength() == 1) {
//					        contextNode = children.item(0);
//					    } else {
//					        break;
//					    }
//					}
//				} else if (Axis.ATTRIBUTE == axisExpr.getAxis()) {
//					if (NodeKindTest.ATTRIBUTE.getNodeKindMask() != nameTest.getNodeKindMask()) {
//						break;
//					}
//
//					Attr attribute = ((Element) contextNode).getAttributeNodeNS(childName.getNamespaceURI(), childName.getLocalPart());
//					if (attribute == null) {
//						attribute = document.createAttributeNS(childName.getNamespaceURI(), childName.getLocalPart());
//						((Element) contextNode).setAttributeNode(attribute);
//						contextNode = attribute;
//					} else {
//						break;
//					}
//
//				} else {
//					break;
//				}
//
//
//			} else if (step instanceof ItemChecker) {
//				ItemChecker itemChecker = (ItemChecker) step;
//				Expression baseExpr = itemChecker.getBaseExpression();
//
//				if (!(baseExpr instanceof VariableReference)) {
//					break;
//				}
//			} else {
//				break;
//			}
//
//			if (pathExpr != null) {
//				Expression remainingSteps = pathExpr.getRemainingSteps();
//
//				if (remainingSteps instanceof PathExpression) {
//					pathExpr = (PathExpression) remainingSteps;
//					step = pathExpr.getFirstStep();
//				} else if (remainingSteps instanceof AxisExpression) {
//					pathExpr = null;
//					step = (AxisExpression) remainingSteps;
//				} else {
//					throw new RuntimeException("Not supported step " + remainingSteps + " in expression " + expression);
//				}
//			} else {
//				break;
//			}
//		}
//		return rootNode;
//	}
//
//	/**
//	 * Create the QName by running the given finger print against the
//	 * given context
//	 *
//	 * @param fingerprint
//	 * @param namePool
//	 * @param nsContext
//	 *
//	 * @return The QName corresponding to the finger print
//	 */
//	private QName getQualifiedName(int fingerprint, NamePool namePool) {
//		String localName = namePool.getLocalName(fingerprint);
//		String prefix = namePool.getPrefix(fingerprint);
//		String uri = namePool.getURI(fingerprint);
//
//		// Unfortunately, NSContext.getPrefix(String URI) doesn't always work
//		// So, we need to find the prefix for the URI the hard way
//		//        if ((prefix == null) || "".equals(prefix)) {
//		//            for (String nsPrefix : nsContext.getPrefixes()) {
//		//                String nsUri = nsContext.getNamespaceURI(nsPrefix);
//		//
//		//                if (nsUri.equals(uri)) {
//		//                    prefix = nsPrefix;
//		//                }
//		//            }
//		//        }
//
//		return new QName(uri, localName, prefix);
//	}
//
//	public static Document toDOMDocument(Node node) throws TransformerException {
//		// If the node is the document, just cast it
//		if (node instanceof Document) {
//		    return newDocument();
//			// If the node is an element
//		} else if (node instanceof Element) {
//			Element elem = (Element) node;
//			// If this is the root element, return its owner document
//			//            if (elem.getOwnerDocument().getDocumentElement() == elem) {
//			return elem.getOwnerDocument();
//			// else, create a new doc and copy the element inside it
//			//            } else {
//			//                Document doc = newDocument();
//			//                doc.appendChild(doc.importNode(node, true));
//			//                return doc;
//			//            }
//			// other element types are not handled
//		} else if (node == null) {
//		    return newDocument();
//		} else {
//			throw new TransformerException("Unable to convert DOM node to a Document");
//		}
//	}
//
//	public static Document newDocument() {
//		try {
//			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//			return db.newDocument();
//		} catch (ParserConfigurationException e) {
//			//            __log.error(e);
//			throw new RuntimeException(e);
//		}
//	}
//
//	public static String getQualifiedName(QName qName) {
//		String prefix = qName.getPrefix(), localPart = qName.getLocalPart();
//		return (prefix == null || "".equals(prefix)) ? localPart : (prefix + ":" + localPart);
//	}

}