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

package org.drools.repository.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.repository.RepositorySessionUtil;

import junit.framework.TestCase;

public class PermissionManagerTest extends TestCase {

	public void testLoadSave() throws Exception {
		PermissionManager pm = new PermissionManager(RepositorySessionUtil.getRepository());
		Map<String, List<String>> perms = new HashMap<String, List<String>>() {{
			put("package.admin", new ArrayList<String>() {{add("1234567890");}});
			put("package.developer", new ArrayList<String>() {{add("1"); add("2");}});
			put("analyst", new ArrayList<String>() {{add("HR");}});
			put("admin", new ArrayList<String>());
		}};
		pm.updateUserPermissions("wankle", perms);

		Map<String, List<String>> perms_ = pm.retrieveUserPermissions("wankle");
		assertEquals(perms.size(), perms_.size());

		perms_ = pm.retrieveUserPermissions("wankle");
		assertEquals(perms.size(), perms_.size());

		List<String> padmin = perms_.get("package.admin");
		assertEquals(1, padmin.size());
		assertEquals("1234567890", padmin.get(0));

		List<String> pdev = perms_.get("package.developer");
		assertEquals(2, pdev.size());

		perms = new HashMap<String, List<String>>() {{
			put("admin", null);
		}};
		pm.updateUserPermissions("wankle2", perms);

		perms_ = pm.retrieveUserPermissions("wankle2");
		List<String> aperms = perms_.get("admin");
		assertEquals(0, aperms.size());

		perms_.remove("admin");
		assertEquals(0, perms_.size());
		pm.updateUserPermissions("wankle2", perms_);
		perms_ = pm.retrieveUserPermissions("wankle2");
		assertEquals(0, perms_.size());



		perms_ = pm.retrieveUserPermissions("wankle");

		padmin = perms_.get("package.admin");
		assertEquals(1, padmin.size());
		assertEquals("1234567890", padmin.get(0));

		assertTrue(pm.listUsers().containsKey("wankle"));
		pm.removeUserPermissions("wankle");

		assertFalse(pm.listUsers().containsKey("wankle"));
	}

	public void testUpdatePerms() throws Exception {
		PermissionManager pm = new PermissionManager(RepositorySessionUtil.getRepository());
		Map<String, List<String>> perms = new HashMap<String, List<String>>() {{
			put("package.admin", new ArrayList<String>() {{add("1234567890");}});
			put("package.developer", new ArrayList<String>() {{add("1"); add("2");}});
			put("analyst", new ArrayList<String>() {{add("HR");}});
			put("admin", new ArrayList<String>());
		}};
		pm.updateUserPermissions("testUpdatePermsWankle", perms);

		perms = pm.retrieveUserPermissions("testUpdatePermsWankle");
		assertEquals(4, perms.size());
		perms.remove("package.developer");
		pm.updateUserPermissions("testUpdatePermsWankle", perms);
		perms = pm.retrieveUserPermissions("testUpdatePermsWankle");
		assertEquals(3, perms.size());
	}

	public void testNilUser() throws Exception {
		PermissionManager pm = new PermissionManager(RepositorySessionUtil.getRepository());
		Map<String, List<String>> perms_ = pm.retrieveUserPermissions("nobody");
		assertEquals(0, perms_.size());

		perms_ = pm.retrieveUserPermissions("nobody");
		assertEquals(0, perms_.size());
	}

	public void testListingUsers() throws Exception {
		PermissionManager pm = new PermissionManager(RepositorySessionUtil.getRepository());
		pm.deleteAllUsers();

		Map<String, List<String>> perms = new HashMap<String, List<String>>() {{
			put("package.admin", new ArrayList<String>() {{add("1234567890");}});
			put("package.developer", new ArrayList<String>() {{add("1"); add("2");}});
			put("analyst", new ArrayList<String>() {{add("HR");}});
			put("admin", new ArrayList<String>());
		}};
		pm.updateUserPermissions("listingUser1", perms);

		perms = new HashMap<String, List<String>>() {{
			put("admin", new ArrayList<String>());
		}};
		pm.updateUserPermissions("listingUser2", perms);
		pm.updateUserPermissions("listingUser3", perms);

		perms = new HashMap<String, List<String>>() {{
			put("package.developer", new ArrayList<String>() {{add("1"); add("2");}});
		}};

		pm.updateUserPermissions("listingUser4", perms);
		perms = new HashMap<String, List<String>>() {{
			put("analyst", new ArrayList<String>() {{add("1"); add("2");}});
		}};
		pm.updateUserPermissions("listingUser5", perms);

		Map<String, List<String>> result = pm.listUsers();
		assertNotNull(result);
		assertEquals(5, result.size());
		assertTrue(result.containsKey("listingUser1"));
		assertTrue(result.containsKey("listingUser2"));
		assertTrue(result.containsKey("listingUser3"));
		assertTrue(result.containsKey("listingUser4"));
		assertTrue(result.containsKey("listingUser5"));

		List<String> permTypes = result.get("listingUser1");
		assertEquals(4, permTypes.size());
		assertTrue(permTypes.contains("package.developer"));


		permTypes = result.get("listingUser5");
		assertEquals(1, permTypes.size());
		assertEquals("analyst", permTypes.get(0));
	}

	public void testEmptyUserName() throws Exception {
		PermissionManager pm = new PermissionManager(RepositorySessionUtil.getRepository());
		Map<String, List<String>> perms_ = pm.retrieveUserPermissions("");
		assertEquals(0, perms_.size());
		
		perms_ = pm.retrieveUserPermissions("  ");
		assertEquals(0, perms_.size());
		
		Map<String, List<String>> perms = new HashMap<String, List<String>>() {{
			put("package.admin", new ArrayList<String>() {{add("1234567890");}});
			put("package.developer", new ArrayList<String>() {{add("1"); add("2");}});
			put("analyst", new ArrayList<String>() {{add("HR");}});
			put("admin", new ArrayList<String>());
		}};
		pm.updateUserPermissions(" ", perms);
		pm.updateUserPermissions("", perms);
		
		pm.removeUserPermissions("");
		pm.removeUserPermissions("  ");		
	}
}
