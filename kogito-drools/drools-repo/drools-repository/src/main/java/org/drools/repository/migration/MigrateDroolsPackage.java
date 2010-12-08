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

package org.drools.repository.migration;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.PackageIterator;
import org.drools.repository.RulesRepository;

/**
 * This is a one time class which will be used to migrate package header info for drools.
 * Only needed for drools version 4 repositories.
 * @author Michael Neale
 */
public class MigrateDroolsPackage {

	public boolean needsMigration(RulesRepository repo) throws RepositoryException {
		Node root = repo.getSession().getRootNode().getNode(RulesRepository.RULES_REPOSITORY_NAME);
		return !root.hasNode("drools.package.migrated");
	}

	public void migrate(RulesRepository repo) throws RepositoryException {
		System.out.println("AUTO MIGRATION: Performing drools.package migration...");
    	PackageIterator pkgs = repo.listPackages();
    	boolean performed = false;
    	while(pkgs.hasNext()) {
    		performed = true;
    		PackageItem pkg = (PackageItem) pkgs.next();
    		migratePackage(pkg);

    		String[] snaps = repo.listPackageSnapshots(pkg.getName());
    		if (snaps != null) {
	    		for (int i = 0; i < snaps.length; i++) {
	    			PackageItem snap = repo.loadPackageSnapshot(pkg.getName(), snaps[i]);
	    			migratePackage(snap);
				}
    		}
    	}



    	if (performed) {
	    	repo.getSession().getRootNode().getNode(RulesRepository.RULES_REPOSITORY_NAME).addNode("drools.package.migrated", "nt:folder");
	    	repo.save();
	    	System.out.println("AUTO MIGRATION: drools.package migration completed.");
    	}
	}

	private void migratePackage(PackageItem pkg) {
		if (!pkg.containsAsset("drools")) {
			AssetItem asset = pkg.addAsset("drools", "");
			asset.updateFormat("package");
			asset.updateContent(pkg.getStringProperty(PackageItem.HEADER_PROPERTY_NAME));
			asset.checkin("");
		}
	}


}
