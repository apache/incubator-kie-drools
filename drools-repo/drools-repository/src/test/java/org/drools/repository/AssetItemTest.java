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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;

import junit.framework.TestCase;

public class AssetItemTest extends TestCase {


    private RulesRepository getRepo() {
        return RepositorySessionUtil.getRepository();
    }

    private PackageItem getDefaultPackage() {
        return getRepo().loadDefaultPackage();
    }

    public void testAssetItemCreation() throws Exception {

            Calendar now = Calendar.getInstance();

            Thread.sleep(500); //MN: need this sleep to get the correct date

            AssetItem ruleItem1 = getDefaultPackage().addAsset("testRuleItem", "test content");


            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals("testRuleItem", ruleItem1.getName());

            assertNotNull(ruleItem1.getCreatedDate());

            assertTrue(now.before( ruleItem1.getCreatedDate() ));

            String packName = getDefaultPackage().getName();

            assertEquals(packName, ruleItem1.getPackageName());

            assertNotNull(ruleItem1.getUUID());

        //try constructing with node of wrong type
        try {

            PackageItem pitem = getRepo().loadDefaultPackage();
            new AssetItem(getRepo(), pitem.getNode());
            fail("Exception not thrown for node of wrong type");
        }
        catch(RulesRepositoryException e) {
            assertNotNull(e.getMessage());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testGetContentLength() throws Exception {
        RulesRepository repo = getRepo();
        PackageItem pkg = repo.loadDefaultPackage();
        AssetItem asset = pkg.addAsset("testGetContentLength", "");
        assertEquals(0, asset.getContentLength());
        asset.updateContent("boo");
        asset.checkin("");
        assertEquals("boo".getBytes().length, asset.getContentLength() );

        asset = pkg.addAsset("testGetContentLength2", "");
        assertEquals(0, asset.getContentLength());
        asset.updateBinaryContentAttachment(new ByteArrayInputStream("foobar".getBytes()));
        asset.checkin("");
        assertEquals("foobar".getBytes().length, asset.getContentLength());


    }

    public void testGetPackageItem() throws Exception {
        RulesRepository repo = getRepo();
        PackageItem def = repo.loadDefaultPackage();
        AssetItem asset = repo.loadDefaultPackage().addAsset("testPackageItem", "test content");
        PackageItem pkg = asset.getPackage();
        assertEquals(def.getName(), pkg.getName());
        assertEquals(def.getUUID(), pkg.getUUID());

    }


    public void testUpdateStringProperty() throws Exception {
        RulesRepository repo = getRepo();
        PackageItem def = repo.loadDefaultPackage();
        AssetItem asset = repo.loadDefaultPackage().addAsset("testUpdateStringProperty", "test content");
        asset.updateContent("new content");
        asset.checkin("");
        Calendar lm = asset.getLastModified();

        Thread.sleep(100);
        asset.updateStringProperty("Anything", "AField");

        assertEquals("Anything", asset.getStringProperty("AField"));
        Calendar lm_ = asset.getLastModified();

        assertTrue(lm_.getTimeInMillis() > lm.getTimeInMillis());

        Thread.sleep(100);

        asset.updateStringProperty("More", "AField", false);

        assertEquals(lm_.getTimeInMillis(), asset.getLastModified().getTimeInMillis());

        asset.updateContent("more content");
        asset.checkin("");

        asset = repo.loadAssetByUUID(asset.getUUID());
        assertEquals("More", asset.getStringProperty("AField"));

        
    }


    public void testGetPackageItemHistorical() throws Exception {
        RulesRepository repo = getRepo();
        PackageItem pkg = repo.createPackage("testGetPackageItemHistorical", "");
        AssetItem asset = pkg.addAsset("whee", "");
        asset.checkin("");
        assertNotNull(asset.getPackage());

        repo.createPackageSnapshot(pkg.getName(), "SNAP");

        PackageItem pkg_ = repo.loadPackageSnapshot(pkg.getName(), "SNAP");
        AssetItem asset_ = pkg_.loadAsset("whee");
        PackageItem pkg__ = asset_.getPackage();
        assertTrue(pkg__.isSnapshot());
        assertTrue(pkg_.isSnapshot());
        assertFalse(pkg.isSnapshot());
        assertEquals(pkg.getName(), pkg__.getName());

        asset.updateDescription("yeah !");
        asset.checkin("new");

        asset = pkg.loadAsset("whee");
        assertNotNull(asset.getPackage());

        AssetHistoryIterator it = asset.getHistory();
        while(it.hasNext()) {
        	AssetItem as = it.next();
        	if (as.getVersionNumber() > 0) {
	        	System.err.println(as.getVersionNumber());
	        	System.err.println(as.getPackageName());
	        	assertNotNull(as.getPackage());
	        	assertEquals(pkg.getName(), as.getPackage().getName());
        	}
        }
    }

    public void testGetAssetNameFromFileName() {
    	String[] asset = AssetItem.getAssetNameFromFileName("foo.bar");
    	assertEquals("foo", asset[0]);
    	assertEquals("bar", asset[1]);

    	asset = AssetItem.getAssetNameFromFileName("Rule 261.3 Something foo.drl");
    	assertEquals("Rule 261.3 Something foo", asset[0]);
    	assertEquals("drl", asset[1]);

    	asset = AssetItem.getAssetNameFromFileName("Rule_261.3_Something_foo.drl");
    	assertEquals("Rule_261.3_Something_foo", asset[0]);
    	assertEquals("drl", asset[1]);

    	asset = AssetItem.getAssetNameFromFileName("Rule 261.3 Something foo.model.drl");
    	assertEquals("Rule 261.3 Something foo", asset[0]);
    	assertEquals("model.drl", asset[1]);

    	asset = AssetItem.getAssetNameFromFileName("Rule_261.3_Something_foo.model.drl");
    	assertEquals("Rule_261.3_Something_foo", asset[0]);
    	assertEquals("model.drl", asset[1]);

    	asset = AssetItem.getAssetNameFromFileName("application-model-1.0.0.jar");
    	assertEquals("application-model-1.0.0", asset[0]);
    	assertEquals("jar", asset[1]);

    	asset = AssetItem.getAssetNameFromFileName("something-1.0.0.drl");
    	assertEquals("something-1.0.0", asset[0]);
    	assertEquals("drl", asset[1]);

        asset = AssetItem.getAssetNameFromFileName("foo.bpel.jar");
        assertEquals("foo", asset[0]);
        assertEquals("bpel.jar", asset[1]);
        
    	asset = AssetItem.getAssetNameFromFileName("SubmitApplication.rf");
    	assertEquals("SubmitApplication", asset[0]);
    	assertEquals("rf", asset[1]);

    	asset = AssetItem.getAssetNameFromFileName("Submit.rf");
    	assertEquals("Submit", asset[0]);
    	assertEquals("rf", asset[1]);


//    	System.err.println(asset[0]);
//    	System.err.println(asset[1]);



    }


    public void testGetContent() {

            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetContent", "test content");
            ruleItem1.updateContent( "test content" );
            ruleItem1.updateFormat("drl");

            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals("test content", ruleItem1.getContent());

            assertFalse(ruleItem1.isBinary());

            assertNotNull(ruleItem1.getBinaryContentAsBytes());
            assertNotNull(ruleItem1.getBinaryContentAttachment());
            String content = new String(ruleItem1.getBinaryContentAsBytes());

            assertNotNull(content);        
    }


    public void testUpdateContent() throws Exception {
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testUpdateContent", "test description");

            assertFalse(ruleItem1.getCreator().equals( "" ));
            ruleItem1.updateContent( "test content" );
            ruleItem1.checkin( "yeah" );

            assertFalse(ruleItem1.getLastContributor().equals( "" ));

            ruleItem1.updateContent( "new rule content");

            assertEquals("new rule content", ruleItem1.getContent());

            assertTrue(ruleItem1.getNode().getSession().hasPendingChanges());

            ruleItem1.checkin( "yeah !" );
            assertFalse(ruleItem1.getNode().getSession().hasPendingChanges());

            assertEquals("yeah !", ruleItem1.getCheckinComment());

            AssetItem prev = (AssetItem) ruleItem1.getPrecedingVersion();
            assertEquals("test content", prev.getContent());
            assertFalse("yeah !".equals(prev.getCheckinComment()));


            assertEquals(prev, ruleItem1.getPrecedingVersion());


            ruleItem1 = getDefaultPackage().loadAsset( "testUpdateContent" );
            VersionIterator it = VersionableItem.getVersionManager(ruleItem1.getNode()).getVersionHistory(ruleItem1.getNode().getPath()).getAllVersions();

            // and this shows using a version iterator.
            // perhaps migrate to using this rather then next/prev methods.
            //this way, we can skip.
            assertTrue(it.hasNext());
            while (it.hasNext()) {
                Version n = it.nextVersion();
                AssetItem item = new AssetItem(ruleItem1.getRulesRepository(), n);
                assertNotNull(item);

            }
    }


    public void testCategoriesPagination() {
    		PackageItem pkg = getRepo().createPackage("testPagination", "");
    		getRepo().loadCategory( "/" ).addCategory( "testPagedTag", "description" );

            AssetItem a = pkg.addAsset("testPage1", "test content");
            a.addCategory("testPagedTag");
            a.checkin("");

            a = pkg.addAsset("testPage2", "test content");
            a.addCategory("testPagedTag");
            a.checkin("");

            a = pkg.addAsset("testPage3", "test content");
            a.addCategory("testPagedTag");
            a.checkin("");

            a = pkg.addAsset("testPage4", "test content");
            a.addCategory("testPagedTag");
            a.checkin("");

            a = pkg.addAsset("testPage5", "test content");
            a.addCategory("testPagedTag");
            a.checkin("");

            AssetItemPageResult result = getRepo().findAssetsByCategory("testPagedTag", 0, -1);
            assertTrue(result.currentPosition > 0);
            assertEquals(5, result.assets.size());
            assertEquals(false, result.hasNext);



            result = getRepo().findAssetsByCategory("testPagedTag", 0, 2);
            assertTrue(result.currentPosition > 0);
            assertEquals(true, result.hasNext);
            assertEquals(2, result.assets.size());

            assertEquals("testPage1", ((AssetItem) result.assets.get(0)).getName());
            assertEquals("testPage2", ((AssetItem) result.assets.get(1)).getName());

            result = getRepo().findAssetsByCategory("testPagedTag", 2, 2);
            assertTrue(result.currentPosition > 0);
            assertEquals(true, result.hasNext);
            assertEquals(2, result.assets.size());

            assertEquals("testPage3", ((AssetItem) result.assets.get(0)).getName());
            assertEquals("testPage4", ((AssetItem) result.assets.get(1)).getName());

            result = getRepo().findAssetsByCategory("testPagedTag", 2, 3);
            assertTrue(result.currentPosition > 0);
            assertEquals(false, result.hasNext);
            assertEquals(3, result.assets.size());

            assertEquals("testPage3", ((AssetItem) result.assets.get(0)).getName());
            assertEquals("testPage4", ((AssetItem) result.assets.get(1)).getName());
            assertEquals("testPage5", ((AssetItem) result.assets.get(2)).getName());

    }

    public void testCategories() {
        AssetItem ruleItem1 = getDefaultPackage().addAsset("testAddTag", "test content");

        getRepo().loadCategory( "/" ).addCategory( "testAddTagTestTag", "description" );

        ruleItem1.addCategory("testAddTagTestTag");
        List tags = ruleItem1.getCategories();
        assertEquals(1, tags.size());
        assertEquals("testAddTagTestTag", ((CategoryItem)tags.get(0)).getName());

        getRepo().loadCategory( "/" ).addCategory( "testAddTagTestTag2", "description" );
        ruleItem1.addCategory("testAddTagTestTag2");
        tags = ruleItem1.getCategories();
        assertEquals(2, tags.size());

        ruleItem1.checkin( "woot" );

        //now test retrieve by tags
        List result = getRepo().findAssetsByCategory("testAddTagTestTag", 0, -1).assets;
        assertEquals(1, result.size());
        AssetItem retItem = (AssetItem) result.get( 0 );
        assertEquals("testAddTag", retItem.getName());

        ruleItem1.updateContent( "foo" );
        ruleItem1.checkin( "latest" );


        assertTrue(ruleItem1.getCategories().size() > 0);
        assertNotNull(ruleItem1.getCategorySummary());
        assertEquals("testAddTagTestTag testAddTagTestTag2 ", ruleItem1.getCategorySummary());


        result = getRepo().findAssetsByCategory( "testAddTagTestTag",0, -1 ).assets;

        assertEquals(1, result.size());

        ruleItem1 = (AssetItem) result.get( 0 );
        assertEquals(2, ruleItem1.getCategories().size());

        assertEquals("foo", ruleItem1.getContent());
        AssetItem prev = (AssetItem) ruleItem1.getPrecedingVersion();
        assertNotNull(prev);




    }


    public void testUpdateCategories() {
        getRepo().loadCategory( "/" ).addCategory( "testUpdateCategoriesOnAsset", "la" );
        getRepo().loadCategory( "/" ).addCategory( "testUpdateCategoriesOnAsset2", "la" );

        AssetItem item = getRepo().loadDefaultPackage().addAsset( "testUpdateCategoriesOnAsset", "huhuhu" );
        String[] cats = new String[] {"testUpdateCategoriesOnAsset", "testUpdateCategoriesOnAsset2"};
        item.updateCategoryList( cats );

        item.checkin( "aaa" );

        item = getRepo().loadDefaultPackage().loadAsset( "testUpdateCategoriesOnAsset" );
        assertEquals(2, item.getCategories().size());

        for ( Iterator iter = item.getCategories().iterator(); iter.hasNext(); ) {
            CategoryItem cat = (CategoryItem) iter.next();
            assertTrue(cat.getName().startsWith( "testUpdateCategoriesOnAsset" ));
        }

    }

    public void testFindRulesByCategory() throws Exception {

        getRepo().loadCategory( "/" ).addCategory( "testFindRulesByCat", "yeah" );
        AssetItem as1 = getDefaultPackage().addAsset( "testFindRulesByCategory1", "ya", "testFindRulesByCat", "drl" );
        getDefaultPackage().addAsset( "testFindRulesByCategory2", "ya", "testFindRulesByCat", AssetItem.DEFAULT_CONTENT_FORMAT ).checkin( "version0" );

        as1.checkin( "version0" );

        assertEquals("drl", as1.getFormat());

        List rules = getRepo().findAssetsByCategory( "testFindRulesByCat", 0, -1 ).assets;
        assertEquals(2, rules.size());

        for ( Iterator iter = rules.iterator(); iter.hasNext(); ) {
            AssetItem element = (AssetItem) iter.next();
            assertTrue(element.getName().startsWith( "testFindRulesByCategory" ));
        }

        try {
            getRepo().loadCategory( "testFindRulesByCat" ).remove();

            fail("should not be able to remove");
        } catch (RulesRepositoryException e) {
            //assertTrue(e.getCause() instanceof ReferentialIntegrityException);
            assertNotNull(e.getMessage());
        }


    }


    public void testRemoveTag() {
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testRemoveTag", "test content");

            getRepo().loadCategory( "/" ).addCategory( "TestRemoveCategory", "description" );

            ruleItem1.addCategory("TestRemoveCategory");
            List tags = ruleItem1.getCategories();
            assertEquals(1, tags.size());
            ruleItem1.removeCategory("TestRemoveCategory");
            tags = ruleItem1.getCategories();
            assertEquals(0, tags.size());

            getRepo().loadCategory( "/" ).addCategory( "TestRemoveCategory2", "description" );
            getRepo().loadCategory( "/" ).addCategory( "TestRemoveCategory3", "description" );
            ruleItem1.addCategory("TestRemoveCategory2");
            ruleItem1.addCategory("TestRemoveCategory3");
            ruleItem1.removeCategory("TestRemoveCategory2");
            tags = ruleItem1.getCategories();
            assertEquals(1, tags.size());
            assertEquals("TestRemoveCategory3", ((CategoryItem)tags.get(0)).getName());

    }

    public void testGetTags() {
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testGetTags", "test content");

            List tags = ruleItem1.getCategories();
            assertNotNull(tags);
            assertEquals(0, tags.size());

            getRepo().loadCategory( "/" ).addCategory( "testGetTagsTestTag", "description" );

            ruleItem1.addCategory("testGetTagsTestTag");
            tags = ruleItem1.getCategories();
            assertEquals(1, tags.size());
            assertEquals("testGetTagsTestTag", ((CategoryItem)tags.get(0)).getName());

    }

    public void testSetStateString() {
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testSetStateString", "test content");

            getRepo().createState( "TestState1" );

            ruleItem1.updateState("TestState1");
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState1", ruleItem1.getState().getName());

            getRepo().createState( "TestState2" );
            ruleItem1.updateState("TestState2");
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState2", ruleItem1.getState().getName());

            ruleItem1 = getDefaultPackage().addAsset("foobar", "test description");


            StateItem stateItem1 = getRepo().getState("TestState1");
            ruleItem1.updateState(stateItem1);
            assertNotNull(ruleItem1.getState());
            assertEquals(ruleItem1.getState().getName(), "TestState1");

            StateItem stateItem2 = getRepo().getState("TestState2");
            ruleItem1.updateState(stateItem2);
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState2", ruleItem1.getState().getName());

    }


    public void testStatusStuff() {
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testGetState", "test content");

            StateItem stateItem1 = ruleItem1.getState();
            assertEquals(StateItem.DRAFT_STATE_NAME, stateItem1.getName());



            ruleItem1.updateState("TestState1");
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState1", ruleItem1.getState().getName());

            ruleItem1 = getDefaultPackage().addAsset( "testGetState2", "wa" );
            assertEquals(StateItem.DRAFT_STATE_NAME, ruleItem1.getStateDescription());
            assertEquals(getRepo().getState( StateItem.DRAFT_STATE_NAME ), ruleItem1.getState());
    }

    public void testToString() {
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testToString", "test content");
            assertNotNull(ruleItem1.toString());
    }

    public void testGetLastModifiedOnCheckin() throws Exception  {
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testGetLastModified", "test content");

            Calendar cal = Calendar.getInstance();
            long before = cal.getTimeInMillis();

            Thread.sleep( 100 );
            ruleItem1.updateContent("new lhs");
            ruleItem1.checkin( "woot" );
            Calendar cal2 = ruleItem1.getLastModified();
            long lastMod = cal2.getTimeInMillis();

            cal = Calendar.getInstance();
            long after = cal.getTimeInMillis();



            assertTrue(before < lastMod);
            assertTrue(lastMod < after);

    }

    public void testGetDateEffective() {

            AssetItem ruleItem1 = getDefaultPackage().addAsset("testGetDateEffective", "test content");

            //it should be initialized to null
            assertTrue(ruleItem1.getDateEffective() == null);

            //now try setting it, then retrieving it
            Calendar cal = Calendar.getInstance();
            ruleItem1.updateDateEffective(cal);
            Calendar cal2 = ruleItem1.getDateEffective();

            assertEquals(cal, cal2);
    }

    public void testGetDateExpired() {
        try {
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetDateExpired", "test content");

            //it should be initialized to null
            assertTrue(ruleItem1.getDateExpired() == null);

            //now try setting it, then retrieving it
            Calendar cal = Calendar.getInstance();
            ruleItem1.updateDateExpired(cal);
            Calendar cal2 = ruleItem1.getDateExpired();

            assertEquals(cal, cal2);
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }


    public void testSaveAndCheckinDescriptionAndTitle() throws Exception {
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetDescription", "");
            ruleItem1.checkin( "version0" );

            //it should be "" to begin with
            assertEquals("", ruleItem1.getDescription());

            ruleItem1.updateDescription("test description");
            assertEquals("test description", ruleItem1.getDescription());




            assertTrue(getRepo().getSession().hasPendingChanges());

            ruleItem1.updateTitle( "This is a title" );
            assertTrue(getRepo().getSession().hasPendingChanges());
            ruleItem1.checkin( "ya" );


            //we can save without a checkin
            getRepo().getSession().save();

            assertFalse(getRepo().getSession().hasPendingChanges());


            try {
                ruleItem1.getPrecedingVersion().updateTitle( "baaad" );
                fail("should not be able to do this");
            } catch (RulesRepositoryException e) {
                assertNotNull(e.getMessage());
            }

    }

    public void testGetPrecedingVersionAndRestore() throws Exception {
            getRepo().loadCategory( "/" ).addCategory( "foo", "ka" );
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetPrecedingVersion", "descr");
            ruleItem1.checkin( "version0" );
            assertTrue(ruleItem1.getPrecedingVersion() == null);

            ruleItem1.addCategory( "foo" );
            ruleItem1.updateContent( "test content" );
            ruleItem1.updateDescription( "descr2" );
            Thread.sleep( 100 );
            ruleItem1.checkin( "boo" );

            AssetItem predecessorRuleItem = (AssetItem) ruleItem1.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);

            //check version handling
            assertNotNull(predecessorRuleItem.getVersionSnapshotUUID());
            assertFalse(predecessorRuleItem.getVersionSnapshotUUID().equals( ruleItem1.getUUID() ));


            //assertEquals(predecessorRuleItem.getCreatedDate().getTimeInMillis(), ruleItem1.getCreatedDate().getTimeInMillis());


            assertEquals(ruleItem1.getState().getName(), predecessorRuleItem.getState().getName());
            //assertEquals(ruleItem1.getName(), predecessorRuleItem.getName());



            AssetItem loadedHistorical = getRepo().loadAssetByUUID( predecessorRuleItem.getVersionSnapshotUUID() );
            assertTrue(loadedHistorical.isHistoricalVersion());
            assertFalse(ruleItem1.getVersionNumber() == loadedHistorical.getVersionNumber());

            ruleItem1.updateContent("new content");
            ruleItem1.checkin( "two changes" );

            predecessorRuleItem = (AssetItem) ruleItem1.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);
            assertEquals(1, predecessorRuleItem.getCategories().size());
            CategoryItem cat = (CategoryItem) predecessorRuleItem.getCategories().get( 0 );
            assertEquals("foo", cat.getName());

            assertEquals("test content", predecessorRuleItem.getContent());

            assertEquals(RulesRepository.DEFAULT_PACKAGE, predecessorRuleItem.getPackageName());

            ruleItem1.updateContent("newer lhs");
            ruleItem1.checkin( "another" );

            predecessorRuleItem = (AssetItem) ruleItem1.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);
            assertEquals("new content", predecessorRuleItem.getContent());
            predecessorRuleItem = (AssetItem) predecessorRuleItem.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);
            assertEquals("test content", predecessorRuleItem.getContent());

            //now try restoring
            long oldVersionNumber = ruleItem1.getVersionNumber();

            AssetItem toRestore = getRepo().loadAssetByUUID( predecessorRuleItem.getVersionSnapshotUUID() );

            getRepo().restoreHistoricalAsset( toRestore, ruleItem1, "cause I want to"  );


            AssetItem restored = getRepo().loadDefaultPackage().loadAsset( "testGetPrecedingVersion" );

            //assertEquals( predecessorRuleItem.getCheckinComment(), restored.getCheckinComment());
            assertEquals(predecessorRuleItem.getDescription(), restored.getDescription());
            assertEquals("cause I want to", restored.getCheckinComment());
            assertEquals(5, restored.getVersionNumber());
            assertFalse(oldVersionNumber ==  restored.getVersionNumber() );
    }

    public void testGetSucceedingVersion() {
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetSucceedingVersion", "test description");
            ruleItem1.checkin( "version0" );

            assertEquals(1, ruleItem1.getVersionNumber());

            AssetItem succeedingRuleItem = (AssetItem) ruleItem1.getSucceedingVersion();
            assertTrue(succeedingRuleItem == null);

            ruleItem1.updateContent("new content");
            ruleItem1.checkin( "la" );

            assertEquals(2, ruleItem1.getVersionNumber());

            AssetItem predecessorRuleItem = (AssetItem) ruleItem1.getPrecedingVersion();
            assertEquals("", predecessorRuleItem.getContent());
            succeedingRuleItem = (AssetItem) predecessorRuleItem.getSucceedingVersion();
            assertNotNull(succeedingRuleItem);
            assertEquals(ruleItem1.getContent(), succeedingRuleItem.getContent());
    }

    public void testGetSuccessorVersionsIterator() {
        try {
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetSuccessorVersionsIterator", "test content");
            ruleItem1.checkin( "version0" );

            Iterator iterator = ruleItem1.getSuccessorVersionsIterator();
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());

            ruleItem1.updateContent("new content").checkin( "ya" );


            iterator = ruleItem1.getSuccessorVersionsIterator();
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());

            AssetItem predecessorRuleItem = (AssetItem) ruleItem1.getPrecedingVersion();
            iterator = predecessorRuleItem.getSuccessorVersionsIterator();
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            AssetItem nextRuleItem = (AssetItem) iterator.next();
            assertEquals("new content", nextRuleItem.getContent());
            assertFalse(iterator.hasNext());

            ruleItem1.updateContent("newer content");
            ruleItem1.checkin( "boo" );

            iterator = predecessorRuleItem.getSuccessorVersionsIterator();
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            nextRuleItem = (AssetItem) iterator.next();
            assertEquals("new content", nextRuleItem.getContent());
            assertTrue(iterator.hasNext());
            nextRuleItem = (AssetItem)iterator.next();
            assertEquals("newer content", nextRuleItem.getContent());
            assertFalse(iterator.hasNext());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testGetPredecessorVersionsIterator() {
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetPredecessorVersionsIterator", "test description");
            ruleItem1.checkin( "version0" );

            Iterator iterator = ruleItem1.getPredecessorVersionsIterator();
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());

            ruleItem1.updateContent( "test content" );
            ruleItem1.checkin( "lalalalala" );

            iterator = ruleItem1.getPredecessorVersionsIterator();
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());

            ruleItem1.updateContent("new content");
            ruleItem1.checkin( "boo" );

            iterator = ruleItem1.getPredecessorVersionsIterator();
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            AssetItem nextRuleItem = (AssetItem) iterator.next();

            assertEquals("test content", nextRuleItem.getContent());

            ruleItem1.updateContent("newer content");
            ruleItem1.checkin( "wee" );


            iterator = ruleItem1.getPredecessorVersionsIterator();
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            nextRuleItem = (AssetItem) iterator.next();
            assertTrue(iterator.hasNext());
            assertEquals("new content", nextRuleItem.getContent());
            nextRuleItem = (AssetItem) iterator.next();

            assertEquals("test content", nextRuleItem.getContent());

            assertEquals("", ((AssetItem) iterator.next()).getContent());

    }

    public void testHistoryIterator() throws Exception {
        AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testHistoryIterator", "test description");
        ruleItem1.checkin( "version0" );

        ruleItem1 = getRepo().loadAssetByUUID( ruleItem1.getUUID() );
        ruleItem1.updateContent( "wo" );
        ruleItem1.checkin( "version1" );

        ruleItem1 = getRepo().loadAssetByUUID( ruleItem1.getUUID() );
        ruleItem1.updateContent( "ya" );
        ruleItem1.checkin( "version2" );

        Iterator it = ruleItem1.getHistory();
        for ( int i = 0; i < 2; i++ ) {
            assertTrue(it.hasNext());
            it.next();
        }

    }

    public void testGetTitle() {
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetTitle", "test content");

            assertEquals("testGetTitle", ruleItem1.getTitle());
    }

    public void testDublinCoreProperties() {
        PackageItem pkg = getRepo().createPackage( "testDublinCore", "wa" );

        AssetItem ruleItem = pkg.addAsset( "testDublinCoreProperties", "yeah yeah yeah" );
        ruleItem.updateCoverage( "b" );
        assertEquals("b",ruleItem.getCoverage());

        ruleItem.checkin( "woo" );

        pkg = getRepo().loadPackage( "testDublinCore" );
        ruleItem = (AssetItem) pkg.getAssets().next();

        assertEquals("b", ruleItem.getCoverage());

        assertEquals("", ruleItem.getExternalRelation());
        assertEquals("", ruleItem.getExternalSource());

    }

    public void testGetFormat() throws Exception {
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetFormat", "test content");
            ruleItem1.updateContent( "la" );
            assertEquals(AssetItem.DEFAULT_CONTENT_FORMAT, ruleItem1.getFormat());

            assertTrue(ruleItem1.getNode().hasProperty( AssetItem.CONTENT_PROPERTY_NAME ));
            assertFalse(ruleItem1.getNode().hasProperty( AssetItem.CONTENT_PROPERTY_BINARY_NAME ));

            ruleItem1.updateFormat( "blah" );
            assertEquals("blah", ruleItem1.getFormat());
    }

    public void testAnonymousProperties() {
        AssetItem item = getRepo().loadDefaultPackage().addAsset( "anonymousproperty", "lalalalala" );
        item.updateUserProperty( "fooBar", "value");
        assertEquals("value", item.getUserProperty("fooBar"));



        item.checkin( "lalalala" );
        try {
            item.updateUserProperty( "drools:content", "whee" );
            fail("should not be able to set built in properties this way.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }


    }

    public void testBinaryAsset() throws Exception {
        AssetItem item = getRepo().loadDefaultPackage().addAsset( "testBinaryAsset", "yeah" );
        String data = "abc 123";
        ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes());
        item.updateBinaryContentAttachment( in );
        item.updateBinaryContentAttachmentFileName( "x.x" );
        in.close();

        assertEquals(data, item.getContent());

        assertFalse(item.getNode().hasProperty( AssetItem.CONTENT_PROPERTY_NAME ));
        assertTrue(item.getNode().hasProperty( AssetItem.CONTENT_PROPERTY_BINARY_NAME ));
        item.checkin( "lalalala" );

        assertTrue(item.isBinary());



        item = getRepo().loadDefaultPackage().loadAsset( "testBinaryAsset" );
        InputStream in2 = item.getBinaryContentAttachment();
        assertNotNull(in2);

        byte[] data2 = item.getBinaryContentAsBytes();
        assertEquals(data, new String(data2));
        assertEquals("x.x", item.getBinaryContentAttachmentFileName());
        assertTrue(item.isBinary());


        item.updateContent("qed");
        item.checkin("");
        item = getRepo().loadAssetByUUID(item.getUUID());
        assertEquals("qed", item.getContent());


    }

}
