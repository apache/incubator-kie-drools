package org.drools.testing.plugin.model;

import java.util.ArrayList;
import java.util.List;

import org.drools.lang.descr.PackageDescr;

public class RtlModel {

	private String fileName;
	private String containerName;
	private PackageDescr packageDescr;
	private List ruleDescrs = new ArrayList();
	
	public RtlModel () {
		
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public PackageDescr getPackageDescr() {
		return packageDescr;
	}

	public void setPackageDescr(PackageDescr packageDescr) {
		this.packageDescr = packageDescr;
	}

	public List getRuleDescrs() {
		return ruleDescrs;
	}

	public void setRuleDescrs(List ruleDescrs) {
		this.ruleDescrs = ruleDescrs;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
}
