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

package org.drools.repository;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.Workspace;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CategoryItemTest extends RepositoryTestCase {

	
	
	@Test
    public void testTagItem() throws Exception {

        final CategoryItem root = getRepo().loadCategory("/");

        root.addCategory("TestTag", "nothing to see");

        CategoryItem tagItem1 = getRepo().loadCategory("TestTag");
        assertNotNull(tagItem1);
        assertEquals("TestTag", tagItem1.getName());

        CategoryItem tagItem2 = getRepo().loadCategory("TestTag");
        assertNotNull(tagItem2);
        assertEquals("TestTag", tagItem2.getName());
        assertEquals(tagItem1, tagItem2);

        List originalCats = getRepo().loadCategory("/").getChildTags(); // listCategoryNames();
        assertTrue(originalCats.size() > 0);

        CategoryItem rootCat = (CategoryItem)originalCats.get(0);
        assertNotNull(rootCat.getName());
        assertNotNull(rootCat.getFullPath());

        root.addCategory("FootestTagItem", "nothing");

        List cats = root.getChildTags();
        assertEquals(originalCats.size() + 1, cats.size());

        boolean found = false;
        for (Iterator iter = cats.iterator(); iter.hasNext();) {
            CategoryItem element = (CategoryItem)iter.next();
            if (element.getName().equals("FootestTagItem")) {
                found = true;
                break;
            }
        }

        assertTrue(found);

    }

	@Test
    public void testCreateCateories() throws Exception {
        RulesRepository repo = getRepo();

        // load the root
        CategoryItem root = repo.loadCategory("/");

        CategoryItem item = root.addCategory("testCreateCategories", "this is a top level one");
        assertEquals("testCreateCategories", item.getName());
        assertEquals("testCreateCategories", item.getFullPath());

        item = repo.loadCategory("testCreateCategories");
        assertEquals("testCreateCategories", item.getName());

        item.remove();
        repo.save();

        try {
            repo.loadCategory("testCreateCategories");
            fail("this should not exist");
        } catch (RulesRepositoryException e) {
            assertNotNull(e.getCause());
        }
    }

	@Test
    public void testGetChildTags() {
		
		final CategoryItem root = getRepo().loadCategory("/");

        root.addCategory("TestTag", "nothing to see");
        
        CategoryItem tagItem1 = getRepo().loadCategory("TestTag");
        assertNotNull(tagItem1);
        assertEquals("TestTag", tagItem1.getName());

        List childTags = tagItem1.getChildTags();
        assertNotNull(childTags);
        assertEquals(0, childTags.size());

        tagItem1.addCategory("TestChildTag1", "description");

        childTags = tagItem1.getChildTags();
        assertNotNull(childTags);
        assertEquals(1, childTags.size());
        assertEquals("TestChildTag1", ((CategoryItem)childTags.get(0)).getName());

        tagItem1.addCategory("AnotherChild", "ignore me");

        childTags = tagItem1.getChildTags();
        assertNotNull(childTags);
        assertEquals(2, childTags.size());
    }

	@Test
    public void testGetChildTag() {
        CategoryItem root = getRepo().loadCategory("/");
        CategoryItem tagItem1 = root.addCategory("testGetChildTag", "yeah");
        assertNotNull(tagItem1);
        assertEquals("testGetChildTag", tagItem1.getName());

        // test that child is added if not already in existence
        List childTags = tagItem1.getChildTags();
        assertNotNull(childTags);
        assertEquals(0, childTags.size());

        CategoryItem childTagItem1 = tagItem1.addCategory("TestChildTag1", "woo");
        assertNotNull(childTagItem1);
        assertEquals("TestChildTag1", childTagItem1.getName());

        // test that if already there, it is returned
        CategoryItem childTagItem2 = getRepo().loadCategory("testGetChildTag/TestChildTag1");
        assertNotNull(childTagItem2);
        assertEquals("TestChildTag1", childTagItem2.getName());
        assertEquals(childTagItem1, childTagItem2);
    }

	@Test
    public void testGetFullPath() {

        CategoryItem root = getRepo().loadCategory("/");

        CategoryItem tagItem1 = root.addCategory("testGetFullPath", "foo");
        assertNotNull(tagItem1);
        assertEquals("testGetFullPath", tagItem1.getFullPath());

        CategoryItem childTagItem1 = tagItem1.addCategory("TestChildTag1", "foo");
        assertNotNull(childTagItem1);
        assertEquals("testGetFullPath/TestChildTag1", childTagItem1.getFullPath());

        CategoryItem childTagItem2 = childTagItem1.addCategory("TestChildTag2", "wee");
        assertNotNull(childTagItem2);
        assertEquals("testGetFullPath/TestChildTag1/TestChildTag2", childTagItem2.getFullPath());

    }

	@Test
    public void testRemoveCategoryUneeded() {
        RulesRepository repo = getRepo();
        repo.loadCategory("/").addCategory("testRemoveCat", "a");
        AssetItem as = repo.loadDefaultPackage().addAsset("testRemoveCategory", "a", "testRemoveCat", "drl");
        as.checkin("a");
        as.updateCategoryList(new String[] {});

        as.checkin("a");

        as = repo.loadDefaultPackage().loadAsset("testRemoveCategory");
        assertEquals(0, as.getCategories().size());

        repo.loadCategory("testRemoveCat").remove();
        repo.save();

    }

	@Test
    public void testRemoveCategoryLinkedWithArchived() {
        RulesRepository repo = getRepo();
        repo.loadCategory("/").addCategory("testRemoveCategoryWithArchivedCat", "a");
        AssetItem as = repo.loadDefaultPackage().addAsset("testRemoveCategoryWithArchivedAsset",
                                                          "a",
                                                          "testRemoveCategoryWithArchivedCat",
                                                          "drl");
        as.checkin("a");

        as.archiveItem(true);

        repo.loadCategory("testRemoveCategoryWithArchivedCat").remove();
        
        repo.save();

        as.remove();
    }
	/**
	 * This removed the complexity of testRemoveCategoryLinkedWithArchived, and
	 * was added to show a problem in ModeShape: https://jira.jboss.org/browse/MODE-877
	 */
	@Test 
	public void simpleRemoveCategoryLinkedWithArchived() {
		try {
			RulesRepository repo = getRepo();	
			Session session = repo.getSession();

			Node rn = session.getRootNode();
			Node asset = rn.addNode("asset","drools:assetNodeType");
			//Adding some required properties
			asset.setProperty("drools:packageName", "one");
			asset.setProperty("drools:title", "title");
			asset.setProperty("drools:format", "format");
			asset.setProperty("drools:description", "description");
			Calendar lastModified = Calendar.getInstance();
			asset.setProperty("drools:lastModified", lastModified);
            //Adding a category
			Node category = rn.addNode("category","drools:categoryNodeType");
			//adding the category to the asset
			Value[] newTagValues = new Value[1];
            newTagValues[0] = asset.getSession().getValueFactory().createValue( category );
            asset.setProperty( "drools:categoryReference",
                    newTagValues );
            //save the session
			session.save();
			//checking that is there.
			PropertyIterator pi = category.getReferences();
			while (pi.hasNext()) {
				Property property = pi.nextProperty();
				String name = property.getName();
				System.out.println("Name=" + name);
				assertEquals("drools:categoryReference", name);
			}
			//removing the category from the asset
			Value[] updatedTagValues = new Value[1];
			updatedTagValues[0] = null;
			asset.setProperty( "drools:categoryReference",
					updatedTagValues );
			//session.save();
			//removing the category itself
			category.remove();
	        //saving the session, leads to a Referential Integrity Exception on ModeShape: 
			//https://jira.jboss.org/browse/MODE-877
			session.save();

		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
}
