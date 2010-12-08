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

import javax.jcr.Session;

import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RepositoryTestCase;
import org.drools.repository.RulesRepository;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class MigrateDroolsPackageTest extends RepositoryTestCase {

	@Test
	public void testMigrate() throws Exception {
		RulesRepository repo = getRepo();
		Session sess = repo.getSession();
		sess.getRootNode().getNode(RulesRepository.RULES_REPOSITORY_NAME).getNode("drools.package.migrated").remove();
		sess.save();

		MigrateDroolsPackage mig = new MigrateDroolsPackage();

		PackageItem pkg = repo.createPackage("testMigratePackage", "");
		pkg.updateStringProperty("some header", PackageItem.HEADER_PROPERTY_NAME);
		sess.save();

		repo.createPackageSnapshot("testMigratePackage", "SNAP1");
		repo.createPackageSnapshot("testMigratePackage", "SNAP2");



		assertTrue(mig.needsMigration(repo));
		mig.migrate(repo);
		assertFalse(repo.getSession().hasPendingChanges());
		assertFalse(mig.needsMigration(repo));
		pkg = repo.loadPackage("testMigratePackage");
		assertTrue(pkg.containsAsset("drools"));
		AssetItem as = pkg.loadAsset("drools");
		assertEquals("some header", as.getContent());


		pkg = repo.loadPackageSnapshot("testMigratePackage", "SNAP1");
		assertTrue(pkg.containsAsset("drools"));
		as = pkg.loadAsset("drools");
		assertEquals("some header", as.getContent());

		pkg = repo.loadPackageSnapshot("testMigratePackage", "SNAP2");
		assertTrue(pkg.containsAsset("drools"));
		as = pkg.loadAsset("drools");
		assertEquals("some header", as.getContent());




	}

}
