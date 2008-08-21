package org.drools.workflow.core.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.impl.NodeContainerImpl;
import org.drools.workflow.core.impl.NodeImpl;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class CompositeNode extends NodeImpl implements NodeContainer, EventNodeInterface {

    private static final long serialVersionUID = 400L;
    
    private NodeContainer nodeContainer;
    private Map<String, CompositeNode.NodeAndType> inConnectionMap = new HashMap<String, CompositeNode.NodeAndType>();
    private Map<String, CompositeNode.NodeAndType> outConnectionMap = new HashMap<String, CompositeNode.NodeAndType>();
    
    public CompositeNode() {
        this.nodeContainer = new NodeContainerImpl();
    }
    
    public Node getNode(long id) {
        return nodeContainer.getNode(id);
    }
    
    public Node internalGetNode(long id) {
    	return getNode(id);
    }

    public Node[] getNodes() {
    	List<Node> subNodes = new ArrayList<Node>();
    	for (Node node: nodeContainer.getNodes()) {
    		if (!(node instanceof CompositeNode.CompositeNodeStart) &&
    				!(node instanceof CompositeNode.CompositeNodeEnd)) {
    			subNodes.add(node);
    		}
    	}
    	return subNodes.toArray(new Node[subNodes.size()]);
    }

    public void addNode(Node node) {
    	// TODO find a more elegant solution for this
    	// preferrable remove id setting from this class
    	// and delegate to GUI command that drops node
    	if (node.getId() <= 0) {
	    	long id = 0;
	        for (Node n: nodeContainer.getNodes()) {
	            if (n.getId() > id) {
	                id = n.getId();
	            }
	        }
	        node.setId(++id);
    	}
    	nodeContainer.addNode(node);
        node.setNodeContainer(this);
    }
    
    protected void internalAddNode(Node node) {
    	addNode(node);
    }
    
    public void removeNode(Node node) {
        nodeContainer.removeNode(node);
        node.setNodeContainer(null);
    }
    
    protected void internalRemoveNode(Node node) {
    	removeNode(node);
    }
    
	public boolean acceptsEvent(String type, Object event) {
		for (Node node: getNodes()) {
			if (node instanceof EventNodeInterface) {
				if (((EventNodeInterface) node).acceptsEvent(type, event)) {
					return true;
				}
			}
		}
		return false;
	}
    
    public void linkIncomingConnections(String inType, long inNodeId, String inNodeType) {
        linkIncomingConnections(inType, new NodeAndType(inNodeId, inNodeType));
    }
    
    public void linkIncomingConnections(String inType, CompositeNode.NodeAndType inNode) {
        CompositeNode.NodeAndType oldNodeAndType = inConnectionMap.get(inType);
        if (oldNodeAndType != null) {
        	if (oldNodeAndType.equals(inNode)) {
        		return;
        	} else {
        		// TODO remove old composite start nodes and connections 
        	}
        }
        inConnectionMap.put(inType, inNode);
        if (inNode != null) {
	        List<Connection> connections = getIncomingConnections(inType);
	        for (Connection connection: connections) {
	        	CompositeNodeStart start = new CompositeNodeStart(connection.getFrom(), inType);
		        internalAddNode(start);
		        new ConnectionImpl(
		            start, Node.CONNECTION_DEFAULT_TYPE, 
		            inNode.getNode(), inNode.getType());
	        }
        }
    }
    
    public void linkOutgoingConnections(long outNodeId, String outNodeType, String outType) {
        linkOutgoingConnections(new NodeAndType(outNodeId, outNodeType), outType);
    }
    
    public void linkOutgoingConnections(CompositeNode.NodeAndType outNode, String outType) {
        CompositeNode.NodeAndType oldNodeAndType = outConnectionMap.get(outType);
        if (oldNodeAndType != null) {
        	if (oldNodeAndType.equals(outNode)) {
        		return;
        	} else {
        		// TODO remove old composite start nodes and connections 
        	}
        }
        outConnectionMap.put(outType, outNode);
        if (outNode != null) {
	        List<Connection> connections = getOutgoingConnections(outType);
	        for (Connection connection: connections) {
		        CompositeNodeEnd end = new CompositeNodeEnd(connection.getTo(), outType);
		        internalAddNode(end);
		        new ConnectionImpl(
		            outNode.getNode(), outNode.getType(), 
		            end, Node.CONNECTION_DEFAULT_TYPE);
	        }
        }
    }

    public CompositeNode.NodeAndType getLinkedIncomingNode(String inType) {
        return inConnectionMap.get(inType);
    }

    public CompositeNode.NodeAndType internalGetLinkedIncomingNode(String inType) {
        return inConnectionMap.get(inType);
    }

    public CompositeNode.NodeAndType getLinkedOutgoingNode(String outType) {
        return outConnectionMap.get(outType);
    }
    
    public CompositeNode.NodeAndType internalGetLinkedOutgoingNode(String outType) {
        return outConnectionMap.get(outType);
    }

    public Map<String, CompositeNode.NodeAndType> getLinkedIncomingNodes() {
        return inConnectionMap;
    }
    
    public Map<String, CompositeNode.NodeAndType> getLinkedOutgoingNodes() {
        return outConnectionMap;
    }
    
    public void validateAddIncomingConnection(final String type, final Connection connection) {
    	CompositeNode.NodeAndType nodeAndType = internalGetLinkedIncomingNode(type);
    	if (connection.getFrom().getNodeContainer() == this) {
    		if (nodeAndType != null) {
    			throw new IllegalArgumentException("Cannot link incoming connection type more than once: " + type);
    		}
    	} else {
	        if (nodeAndType != null) {
	        	((NodeImpl) nodeAndType.getNode()).validateAddIncomingConnection(nodeAndType.getType(), connection);
	        }
    	}
    }
    
    public void addIncomingConnection(String type, Connection connection) {
    	if (connection.getFrom().getNodeContainer() == this) {
    		linkOutgoingConnections(connection.getFrom().getId(), connection.getFromType(), Node.CONNECTION_DEFAULT_TYPE);
    	} else {
	        super.addIncomingConnection(type, connection);
	        CompositeNode.NodeAndType inNode = internalGetLinkedIncomingNode(type);
	        if (inNode != null) {
		        CompositeNodeStart start = new CompositeNodeStart(connection.getFrom(), type);
		        internalAddNode(start);
		        new ConnectionImpl(
		            start, Node.CONNECTION_DEFAULT_TYPE, 
		            inNode.getNode(), inNode.getType());
	        }
    	}
    }
    
    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        CompositeNode.NodeAndType nodeAndType = internalGetLinkedOutgoingNode(type);
        if (connection.getTo().getNodeContainer() == this) {
    		if (nodeAndType != null) {
    			throw new IllegalArgumentException("Cannot link outgoing connection type more than once: " + type);
    		}
    	} else {
    		if (nodeAndType != null) {
	        	((NodeImpl) nodeAndType.getNode()).validateAddOutgoingConnection(nodeAndType.getType(), connection);
	        }
    	}
	}
    
    public void addOutgoingConnection(String type, Connection connection) {
    	if (connection.getTo().getNodeContainer() == this) {
    		linkIncomingConnections(Node.CONNECTION_DEFAULT_TYPE, connection.getTo().getId(), connection.getToType());    		
    	} else {
	        super.addOutgoingConnection(type, connection);
	        CompositeNode.NodeAndType outNode = internalGetLinkedOutgoingNode(type);
	        if (outNode != null) {
		        CompositeNodeEnd end = new CompositeNodeEnd(connection.getTo(), type);
		        internalAddNode(end);
		        new ConnectionImpl(
		            outNode.getNode(), outNode.getType(), 
		            end, Node.CONNECTION_DEFAULT_TYPE);
	        }
    	}
    }
    
    public void validateRemoveIncomingConnection(final String type, final Connection connection) {
        CompositeNode.NodeAndType nodeAndType = internalGetLinkedIncomingNode(type);
        for (Connection inConnection: nodeAndType.getNode().getIncomingConnections(nodeAndType.getType())) {
            if (((CompositeNodeStart) inConnection.getFrom()).getInNodeId() == connection.getFrom().getId()) {
                ((NodeImpl) nodeAndType.getNode()).validateRemoveIncomingConnection(nodeAndType.getType(), inConnection);
                return;
            }
        }
        throw new IllegalArgumentException(
            "Could not find internal incoming connection for node");
    }
    
    public void removeIncomingConnection(String type, Connection connection) {
        super.removeIncomingConnection(type, connection);
        CompositeNode.NodeAndType nodeAndType = internalGetLinkedIncomingNode(type);
        for (Connection inConnection: nodeAndType.getNode().getIncomingConnections(nodeAndType.getType())) {
            if (((CompositeNodeStart) inConnection.getFrom()).getInNodeId() == connection.getFrom().getId()) {
                Node compositeNodeStart = inConnection.getFrom();
                ((ConnectionImpl) inConnection).terminate();
                internalRemoveNode(compositeNodeStart);
                return;
            }
        }
        throw new IllegalArgumentException(
            "Could not find internal incoming connection for node");
    }
    
    public void validateRemoveOutgoingConnection(final String type, final Connection connection) {
        CompositeNode.NodeAndType nodeAndType = internalGetLinkedOutgoingNode(type);
        for (Connection outConnection: nodeAndType.getNode().getOutgoingConnections(nodeAndType.getType())) {
            if (((CompositeNodeEnd) outConnection.getTo()).getOutNodeId() == connection.getTo().getId()) {
                ((NodeImpl) nodeAndType.getNode()).validateRemoveOutgoingConnection(nodeAndType.getType(), outConnection);
                return;
            }
        }
        throw new IllegalArgumentException(
            "Could not find internal outgoing connection for node");
    }
    
    public void removeOutgoingConnection(String type, Connection connection) {
        super.removeOutgoingConnection(type, connection);
        CompositeNode.NodeAndType nodeAndType = internalGetLinkedOutgoingNode(type);
        for (Connection outConnection: nodeAndType.getNode().getOutgoingConnections(nodeAndType.getType())) {
            if (((CompositeNodeEnd) outConnection.getTo()).getOutNodeId() == connection.getTo().getId()) {
                Node compositeNodeEnd = outConnection.getTo();
                ((ConnectionImpl) outConnection).terminate();
                internalRemoveNode(compositeNodeEnd);
                return;
            }
        }
        throw new IllegalArgumentException(
            "Could not find internal outgoing connection for node");
    }
    
    public class NodeAndType {

        private long nodeId;
        private String type;
        private transient Node node;
        
        public NodeAndType(long nodeId, String type) {
            if (type == null) {
                throw new IllegalArgumentException(
                    "Node or type may not be null!");
            }
            this.nodeId = nodeId;
            this.type = type;
        }
        
        public NodeAndType(Node node, String type) {
            if (node == null || type == null) {
                throw new IllegalArgumentException(
                    "Node or type may not be null!");
            }
            this.nodeId = node.getId();
            this.node = node;
            this.type = type;
        }
        
        public Node getNode() {
            if (node == null) {
                node = nodeContainer.getNode(nodeId);
            }
            return node;
        }
        
        public long getNodeId() {
            return nodeId;
        }

        public String getType() {
            return type;
        }
        
        public boolean equals(Object o) {
            if (o instanceof NodeAndType) {
                return nodeId == ((NodeAndType) o).nodeId
                    && type.equals(((NodeAndType) o).type); 
            }
            return false;
        }
        
        public int hashCode() {
            return 7*(int)nodeId + 13*type.hashCode();
        }
        
    }
    
    public class CompositeNodeStart extends NodeImpl {

        private static final long serialVersionUID = 400L;
        
        private long inNodeId;
        private transient Node inNode;
        private String inType;
        
        public CompositeNodeStart(Node outNode, String outType) {
            setName("Composite node start");
            this.inNodeId = outNode.getId();
            this.inNode = outNode;
            this.inType = outType;
            setMetaData("hidden", true);
       }
        
        public Node getInNode() {
            if (inNode == null) {
                inNode = getNodeContainer().getNode(inNodeId);
            }
            return inNode;
        }
        
        public long getInNodeId() {
            return inNodeId;
        }
        
        public String getInType() {
            return inType;
        }
        
        public Connection getTo() {
            final List<Connection> list =
                getOutgoingConnections(Node.CONNECTION_DEFAULT_TYPE);
            if (list.size() > 0) {
                return (Connection) list.get(0);
            }
            return null;
        }
        
    }
    
    public class CompositeNodeEnd extends NodeImpl {

        private static final long serialVersionUID = 400L;
        
        private long outNodeId;
        private transient Node outNode;
        private String outType;
        
        public CompositeNodeEnd(Node outNode, String outType) {
            setName("Composite node end");
            this.outNodeId = outNode.getId();
            this.outNode = outNode;
            this.outType = outType;
            setMetaData("hidden", true);
        }
        
        public Node getOutNode() {
            if (outNode == null) {
                outNode = getNodeContainer().getNode(outNodeId);
            }
            return outNode;
        }
        
        public long getOutNodeId() {
            return outNodeId;
        }
        
        public String getOutType() {
            return outType;
        }
        
    }

}
