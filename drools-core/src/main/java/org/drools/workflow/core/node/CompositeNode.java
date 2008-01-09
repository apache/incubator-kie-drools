package org.drools.workflow.core.node;

import java.util.HashMap;
import java.util.Iterator;
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
public class CompositeNode extends NodeImpl implements NodeContainer {

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

    public Node[] getNodes() {
        return nodeContainer.getNodes();
    }

    public void addNode(Node node) {
        nodeContainer.addNode(node);
        node.setNodeContainer(this);
    }

    public void removeNode(Node node) {
        nodeContainer.removeNode(node);
        node.setNodeContainer(null);
    }
    
    public void linkIncomingConnections(String inType, CompositeNode.NodeAndType inNode) {
        inConnectionMap.put(inType, inNode);
    }
    
    public void linkOutgoingConnections(CompositeNode.NodeAndType outNode, String outType) {
        outConnectionMap.put(outType, outNode);
    }

    public CompositeNode.NodeAndType getLinkedIncomingNode(String inType) {
        return inConnectionMap.get(inType);
    }

    public CompositeNode.NodeAndType getLinkedOutgoingNode(String outType) {
        return outConnectionMap.get(outType);
    }
    
    public void validateAddIncomingConnection(final String type, final Connection connection) {
        CompositeNode.NodeAndType nodeAndType = getLinkedIncomingNode(type);
        if (nodeAndType == null) {
            throw new IllegalArgumentException(
                "Cannot add incoming connections to a composite node until these connections can be mapped correctly to a subnode!");
        }
        ((NodeImpl) nodeAndType.getNode()).validateAddIncomingConnection(nodeAndType.getType(), connection);
    }
    
    public void addIncomingConnection(String type, Connection connection) {
        super.addIncomingConnection(type, connection);
        CompositeNodeStart start = new CompositeNodeStart(connection.getFrom(), type);
        addNode(start);
        CompositeNode.NodeAndType inNode = getLinkedIncomingNode(type);
        new ConnectionImpl(
            start, Node.CONNECTION_DEFAULT_TYPE, 
            inNode.getNode(), inNode.getType());
    }
    
    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        CompositeNode.NodeAndType nodeAndType = getLinkedOutgoingNode(type);
        if (nodeAndType == null) {
            throw new IllegalArgumentException(
                "Cannot add outgoing connections to a composite node until these connections can be mapped correctly to a subnode!");
        }
        ((NodeImpl) nodeAndType.getNode()).validateAddOutgoingConnection(nodeAndType.getType(), connection);
    }
    
    public void addOutgoingConnection(String type, Connection connection) {
        super.addOutgoingConnection(type, connection);
        CompositeNodeEnd end = new CompositeNodeEnd(connection.getTo(), type);
        addNode(end);
        CompositeNode.NodeAndType outNode = getLinkedOutgoingNode(type);
        new ConnectionImpl(
            outNode.getNode(), outNode.getType(), 
            end, Node.CONNECTION_DEFAULT_TYPE);
    }
    
    public void validateRemoveIncomingConnection(final String type, final Connection connection) {
        CompositeNode.NodeAndType nodeAndType = getLinkedIncomingNode(type);
        ((NodeImpl) nodeAndType.getNode()).validateRemoveIncomingConnection(nodeAndType.getType(), connection);
    }
    
    public void removeIncomingConnection(String type, Connection connection) {
        super.removeIncomingConnection(type, connection);
        CompositeNode.NodeAndType inNode = getLinkedIncomingNode(type);
        List<Connection> connections = inNode.getNode().getIncomingConnections(inNode.getType());
        for (Iterator<Connection> iterator = connections.iterator(); iterator.hasNext(); ) {
            Connection internalConnection = iterator.next();
            if (((CompositeNodeStart) internalConnection.getFrom()).getInNode().equals(connection.getFrom())) {
                ((ConnectionImpl) internalConnection).terminate();
                removeNode(internalConnection.getFrom());
            }
        }
    }
    
    public void validateRemoveOutgoingConnection(final String type, final Connection connection) {
        CompositeNode.NodeAndType nodeAndType = getLinkedOutgoingNode(type);
        ((NodeImpl) nodeAndType.getNode()).validateRemoveOutgoingConnection(nodeAndType.getType(), connection);
    }
    
    public void removeOutgoingConnection(String type, Connection connection) {
        super.removeOutgoingConnection(type, connection);
        CompositeNode.NodeAndType outNode = getLinkedOutgoingNode(type);
        List<Connection> connections = outNode.getNode().getOutgoingConnections(outNode.getType());
        for (Iterator<Connection> iterator = connections.iterator(); iterator.hasNext(); ) {
            Connection internalConnection = iterator.next();
            if (((CompositeNodeEnd) internalConnection.getTo()).getOutNode().equals(connection.getTo())) {
                ((ConnectionImpl) internalConnection).terminate();
                removeNode(internalConnection.getTo());
            }
        }
    }
    
    public static class NodeAndType {

        private Node node;
        private String type;
        
        public NodeAndType(Node node, String type) {
            if (node == null || type == null) {
                throw new IllegalArgumentException(
                    "Node or type may not be null!");
            }
            this.node = node;
            this.type = type;
        }
        
        public Node getNode() {
            return node;
        }

        public String getType() {
            return type;
        }
        
        public boolean equals(Object o) {
            if (o instanceof NodeAndType) {
                return node.equals(((NodeAndType) o).node)
                    && type.equals(((NodeAndType) o).type); 
            }
            return false;
        }
        
        public int hashCode() {
            return node.hashCode() + 13*type.hashCode();
        }
        
    }
    
    public class CompositeNodeStart extends NodeImpl {

        private static final long serialVersionUID = 400L;
        
        private Node outNode;
        private String outType;
        
        public CompositeNodeStart(Node outNode, String outType) {
            setName("Composite node start");
            this.outNode = outNode;
            this.outType = outType;
        }
        
        public Node getInNode() {
            return outNode;
        }
        
        public String getInType() {
            return outType;
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
        
        private Node outNode;
        private String outType;
        
        public CompositeNodeEnd(Node outNode, String outType) {
            setName("Composite node end");
            this.outNode = outNode;
            this.outType = outType;
        }
        
        public Node getOutNode() {
            return outNode;
        }
        
        public String getOutType() {
            return outType;
        }
        
    }
    
}
