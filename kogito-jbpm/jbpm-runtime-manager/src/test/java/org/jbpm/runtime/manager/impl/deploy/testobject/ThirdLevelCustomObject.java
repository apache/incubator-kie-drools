package org.jbpm.runtime.manager.impl.deploy.testobject;

public class ThirdLevelCustomObject {

	private EmbedingCustomObject embeddedObject;

	public ThirdLevelCustomObject(EmbedingCustomObject embeddedObject) {
		this.setEmbeddedObject(embeddedObject);
	}

	public EmbedingCustomObject getEmbeddedObject() {
		return embeddedObject;
	}

	public void setEmbeddedObject(EmbedingCustomObject embeddedObject) {
		this.embeddedObject = embeddedObject;
	}
}
