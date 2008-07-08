package org.drools.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.drools.rule.Package;

/**
 * Contains the changes to packages.
 * 
 * @author Toni Rikkola
 * 
 */
class PackageChangeInfo {

	private Collection<Package> changedPackages;
	private Collection<String> removedPackages;

	void addPackage(Package p) {

		if (changedPackages == null) {
			changedPackages = new ArrayList<Package>();
		}

		changedPackages.add(p);
	}

	void addRemovedPackages(Collection<String> removedPackages) {

		for (String name : removedPackages) {
			addRemovedPackage(name);
		}

	}

	Collection<Package> getChangedPackages() {
		if (changedPackages != null) {
			return changedPackages;
		} else {
			return Collections.emptyList();
		}
	}

	Collection<String> getRemovedPackages() {
		if (removedPackages != null) {
			return removedPackages;
		} else {
			return Collections.emptyList();
		}
	}

	void addRemovedPackage(String name) {

		if (removedPackages == null) {
			removedPackages = new ArrayList<String>();
		}

		removedPackages.add(name);
	}
}
