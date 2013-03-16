package org.jbpm.process.workitem;

public class WorkDefinitionImpl extends org.drools.core.process.core.impl.WorkDefinitionExtensionImpl {

	private static final long serialVersionUID = 5L;
	
	private String[] dependencies;
	private String defaultHandler;
	private String category;
	private String path;
	private String file;
	private String documentation;
	private String iconEncoded;
	
	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String[] getDependencies() {
		return dependencies;
	}
	
	public void setDependencies(String[] dependencies) {
		this.dependencies = dependencies;
	}
	
	public String getDefaultHandler() {
		return defaultHandler;
	}
	
	public void setDefaultHandler(String defaultHandler) {
		this.defaultHandler = defaultHandler;
	}

	public String getIconEncoded() {
		return iconEncoded;
	}

	public void setIconEncoded(String iconEncoded) {
		this.iconEncoded = iconEncoded;
	}
	
}
