package org.drools.agent;

import java.io.Serializable;
import java.util.Map;

import org.drools.definition.process.Node;
import org.drools.definition.process.WorkflowProcess;
import org.drools.io.Resource;
import org.drools.io.ResourcedObject;

public class DummyProcess implements WorkflowProcess, Serializable, ResourcedObject {

	private String id;
	private String name;
	private Resource resource;
	public DummyProcess(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public void setResource(Resource resource) {
		
		this.resource = resource;
	}

	public Resource getResource() {
		
		return resource;
	}

	public String getId() {
		
		return this.id;
	}

	public String getName() {
		
		return null;
	}

	public String getVersion() {
		
		return null;
	}

	public String getPackageName() {
		
		return "dummy";
	}

	public String getType() {
		
		return null;
	}

	public Map<String, Object> getMetaData() {
		
		return null;
	}

	public Object getMetaData(String name) {
		
		return null;
	}

	public Node[] getNodes() {
		
		return null;
	}

	public Node getNode(long id) {
		
		return null;
	}

    public String getNamespace() {
        return null;
    }

    public KnowledgeType getKnowledgeType() {
        return null;
    }

}
