package org.mvel;


public class Node {
    private NodeType nodeType = NodeType.PROPERTY_EX;

    private int startPos;
    private int length;
//    private char[] expression;

    private int node;
    private int endNode;

    private String alias;

    private Object register;

    private String name;

    Node() {
    }


    Node(int startPos) {
        this.startPos = startPos;
    }

    Node(int node, NodeType nodeType) {
        this.node = node;
        this.nodeType = nodeType;
    }

    Node(int node, NodeType nodeType, int endNode) {
        this.node = node;
        this.nodeType = nodeType;
        this.endNode = endNode;
    }

    Node(int startPos, int length) {
        this.startPos = startPos;
        this.length = length;
    }

    Node(int node, NodeType nodeType, int startPos, int length, int endNode) {
        this.nodeType = nodeType;
        this.startPos = startPos;
        this.length = length;
        this.node = node;
        this.endNode = endNode;
    }

    public NodeType getToken() {
        return nodeType;
    }

    public void setToken(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return startPos + length;
    }

    public int getNode() {
        return node;
    }

    public void setEndPos(int position) {
        this.length = position - startPos;
    }


    public Node setNode(int node) {
        this.node = node;
        this.endNode = node + 1;

        return this;
    }

    public int getEndNode() {
        return endNode;
    }

    public void setEndNode(int endNode) {
        this.endNode = endNode;
    }

    public Object getRegister() {
        return register;
    }

    public void setRegister(Object register) {
        this.register = register;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
