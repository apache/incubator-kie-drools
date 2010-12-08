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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.Workspace;
import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class ShareableAssetItemTest extends RepositoryTestCase {

    private PackageItem loadGlobalArea() {
        return getRepo().loadGlobalArea();
    }

    @Test
    public void testCreateShareableAsset() throws Exception {
        Calendar now = Calendar.getInstance();
        Thread.sleep(500); // MN: need this sleep to get the correct date

        AssetItem ruleItem = loadGlobalArea().addAsset("testCreateShareableAssetAsset", "desc");
        ruleItem.updateContent("la");
        ruleItem.checkin("initial");

        AssetItem linkedRuleItem = getDefaultPackage().addAssetImportedFromGlobalArea(ruleItem.getName());
        linkedRuleItem.updateContent("laa");
        linkedRuleItem.checkin("second");

        // Test name
        assertEquals("testCreateShareableAssetAsset", linkedRuleItem.getName());
        assertEquals("testCreateShareableAssetAsset", ruleItem.getName());

        // Test Date
        assertNotNull(ruleItem.getCreatedDate());
        assertNotNull(linkedRuleItem.getCreatedDate());
        assertTrue(now.before(ruleItem.getCreatedDate()));
        assertTrue(now.before(linkedRuleItem.getCreatedDate()));

        // Test package name
        assertEquals("globalArea", ruleItem.getPackageName());
        // NOTE: For the asset that links to the shared asset, its package name is always "globalArea".
        assertEquals("globalArea", linkedRuleItem.getPackageName());
        assertEquals(loadGlobalArea().getUUID(), ruleItem.getPackage().getUUID());

        // REVISIT: getPackage mess.
        // assertEquals(loadGlobalArea().getUUID(), linkedRuleItem.getPackage().getUUID());
        assertEquals("laa", linkedRuleItem.getContent());
        assertEquals("laa", ruleItem.getContent());

        // Test UUID
        assertNotNull(ruleItem.getUUID());
        assertNotNull(linkedRuleItem.getUUID());
        // NOTE: They are same nodes. So same UUID!
        assertTrue(linkedRuleItem.getUUID().equals(linkedRuleItem.getUUID()));
    }

    @Test
    public void testRemoveShareableAsset() throws Exception {
        AssetItem asset = loadGlobalArea().addAsset("testRemoveShareableAssetAsset", "desc");
        asset.updateContent("la");
        asset.checkin("initial");

        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());
        linkedAsset.updateContent("laa");
        linkedAsset.checkin("second");

        // REVISIT: the shared asset can not be removed unless no asset refers to it.
        // asset.remove();

        linkedAsset.remove();

        try {
            AssetItem linkedAsset1 = getDefaultPackage().loadAsset("testRemoveShareableAssetAsset");
            fail("Did not get expected exception");
        } catch (RulesRepositoryException e) {

        }

        AssetItem asset1 = loadGlobalArea().loadAsset("testRemoveShareableAssetAsset");
        assertTrue(asset.getUUID().equals(asset1.getUUID()));
    }

    @Test
    public void testGetContentLengthForShareableAsset() throws Exception {
        AssetItem ruleItem = loadGlobalArea().addAsset("testGetContentLengthForShareableAsset", "desc");
        ruleItem.checkin("initial");
        AssetItem linkedRuleItem = getDefaultPackage().addAssetImportedFromGlobalArea(ruleItem.getName());

        assertEquals(0, ruleItem.getContentLength());
        assertEquals(0, linkedRuleItem.getContentLength());
        ruleItem.updateContent("boo");
        ruleItem.checkin("");
        assertEquals("boo".getBytes().length, ruleItem.getContentLength());
        assertEquals("boo".getBytes().length, linkedRuleItem.getContentLength());

        linkedRuleItem.updateContent("booo");
        linkedRuleItem.checkin("");
        assertEquals("booo".getBytes().length, ruleItem.getContentLength());
        assertEquals("booo".getBytes().length, linkedRuleItem.getContentLength());

        ruleItem = loadGlobalArea().addAsset("testGetContentLengthForShareableAsset2", "");
        ruleItem.checkin("initial");
        linkedRuleItem = getDefaultPackage().addAssetImportedFromGlobalArea(ruleItem.getName());

        assertEquals(0, ruleItem.getContentLength());
        assertEquals(0, linkedRuleItem.getContentLength());
        linkedRuleItem.updateBinaryContentAttachment(new ByteArrayInputStream("foobar".getBytes()));
        linkedRuleItem.checkin("");
        assertEquals("foobar".getBytes().length, ruleItem.getContentLength());
        assertEquals("foobar".getBytes().length, linkedRuleItem.getContentLength());

        ruleItem.updateBinaryContentAttachment(new ByteArrayInputStream("foobarr".getBytes()));
        ruleItem.checkin("");
        assertEquals("foobarr".getBytes().length, ruleItem.getContentLength());
        assertEquals("foobarr".getBytes().length, linkedRuleItem.getContentLength());
    }

    @Test
    public void testUpdateStringPropertyForShareableAsset() throws Exception {
        AssetItem asset = loadGlobalArea().addAsset("testUpdateStringPropertyForShareableAsset", "desc");
        asset.checkin("initial");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());
        linkedAsset.updateContent("new content");
        linkedAsset.checkin("");

        linkedAsset.updateStringProperty("Anything", "AField");
        assertEquals("Anything", linkedAsset.getStringProperty("AField"));
        assertEquals("Anything", asset.getStringProperty("AField"));

        asset.updateStringProperty("More", "AField", false);
        asset.updateContent("more content");
        asset.checkin("");

        asset = getRepo().loadAssetByUUID(asset.getUUID());
        assertEquals("More", asset.getStringProperty("AField"));
        assertEquals("more content", asset.getContent());
        linkedAsset = getRepo().loadAssetByUUID(linkedAsset.getUUID());
        assertEquals("More", linkedAsset.getStringProperty("AField"));
        assertEquals("more content", asset.getContent());
    }
    /*
     * https://jira.jboss.org/browse/MODE-879
     */
    @Test
    public void testSimpleGetPackageItemHistoricalForShareableAsset() throws Exception {
        
    	Node node = getRepo().getSession().getNode("/drools:repository/drools:package_area/globalArea/");
    	Node assetNode = node.getNode("assets").addNode("testKurt","drools:assetNodeType");
    	//Adding some required properties
    	assetNode.setProperty("drools:packageName", "one");
    	assetNode.setProperty("drools:title", "title");
    	assetNode.setProperty("drools:format", "format");
    	assetNode.setProperty("drools:description", "description");
		Calendar lastModified = Calendar.getInstance();
		assetNode.setProperty("drools:lastModified", lastModified);
    	getRepo().getSession().save();
    	assetNode.checkin();
    	findAndPrintNodeName(assetNode);
    	
    	//Creating a shared Node
		assetNode.checkout();
		assetNode.addMixin("mix:shareable");
		getRepo().getSession().save();
		assetNode.checkin();
    	Workspace workspace = getRepo().getSession().getWorkspace();
    	String srcPath   = "/drools:repository/drools:package_area/globalArea/assets/testKurt";
    	String path    = "/drools:repository/drools:package_area/defaultPackage/assets/testKurt";
    	workspace.clone(workspace.getName(), srcPath, path, false);	
    	
        findAndPrintNodeName(assetNode);
        
        // Test package snapshot
        String packageName = getDefaultPackage().getName();
        try {
            Node snaps = getRepo().getAreaNode( "drools:packagesnapshot_area" );
            if ( !snaps.hasNode( packageName ) ) {
                snaps.addNode( packageName,
                               "nt:folder" );
                getRepo().save();
            }
            
            String source = "/drools:repository/drools:package_area/defaultPackage";
            String newName = "/drools:repository/drools:packagesnapshot_area/defaultPackage/SNAP";
            getRepo().getSession().getWorkspace().copy( source, newName );
        } catch ( Exception e ) {
            fail();
        }
        findAndPrintNodeName(assetNode);
        //asset.updateDescription("yeah !");
    }
    
    private void findAndPrintNodeName(Node node) throws ValueFormatException, PathNotFoundException, RepositoryException {
    	String UUID = node.getProperty("jcr:baseVersion").getString();
        Node nodeFound = getRepo().getSession().getNodeByUUID(UUID);
        System.out.println("Node:" + nodeFound.getName());
    }

    @Test
    public void testGetPackageItemHistoricalForShareableAsset() throws Exception {
        AssetItem asset = loadGlobalArea().addAsset("testGetPackageItemHistoricalForShareableAsset", "test content");
        // Version 1, created by the original asset
        asset.checkin("initial");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        // Test package snapshot
        String name = getDefaultPackage().getName();
        getRepo().createPackageSnapshot(getDefaultPackage().getName(), "SNAP");

        PackageItem pkgSnap = getRepo().loadPackageSnapshot(getDefaultPackage().getName(), "SNAP");
        AssetItem assetSnap = pkgSnap.loadAsset("testGetPackageItemHistoricalForShareableAsset");
        PackageItem pkgSnap1 = assetSnap.getPackage();
        assertTrue(pkgSnap1.isSnapshot());
        assertTrue(pkgSnap.isSnapshot());
        assertFalse(getDefaultPackage().isSnapshot());
        assertEquals(getDefaultPackage().getName(), pkgSnap1.getName());

        AssetItem linkedAsset1 = getDefaultPackage().loadAsset("testGetPackageItemHistoricalForShareableAsset");
        PackageItem linkedPkg = linkedAsset1.getPackage();
        assertFalse(linkedPkg.isSnapshot());
        assertFalse(getDefaultPackage().isSnapshot());
        assertEquals(getDefaultPackage().getName(), linkedPkg.getName());

        linkedAsset.updateDescription("yeah !");

        // Version 3, created by LinkedAssetItem
        linkedAsset.checkin("new");

        linkedAsset = getDefaultPackage().loadAsset("testGetPackageItemHistoricalForShareableAsset");
        assertNotNull(linkedAsset.getPackage());

        AssetHistoryIterator linkedIt = linkedAsset.getHistory();
        assertEquals(4, iteratorToList(linkedIt).size());

        asset = getDefaultPackage().loadAsset("testGetPackageItemHistoricalForShareableAsset");
        AssetHistoryIterator it = asset.getHistory();
        assertEquals(4, iteratorToList(it).size());
    }

    List iteratorToList( Iterator it ) {
        List list = new ArrayList();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }

    @Test
    public void testGetContentForShareableAsset() {
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testGetContentForShareableAsset", "test content");
        AssetItem linkedAsset = getRepo().loadDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        linkedAsset.updateContent("test content");
        linkedAsset.updateFormat("drl");

        assertNotNull(linkedAsset.getNode());
        assertEquals("test content", linkedAsset.getContent());
        assertEquals("test content", asset.getContent());

        assertFalse(linkedAsset.isBinary());
        assertFalse(asset.isBinary());

        assertNotNull(linkedAsset.getBinaryContentAsBytes());
        assertNotNull(linkedAsset.getBinaryContentAttachment());
        String content = new String(linkedAsset.getBinaryContentAsBytes());
        assertNotNull(content);
        content = new String(asset.getBinaryContentAsBytes());
        assertNotNull(content);
    }

    @Test
    public void testUpdateContentForShareableAsset() throws Exception {
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testUpdateContentForShareableAsset", "test content");
        AssetItem linkedAsset = getRepo().loadDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        assertFalse(asset.getCreator().equals(""));
        assertFalse(linkedAsset.getCreator().equals(""));
        linkedAsset.updateContent("test content");
        linkedAsset.checkin("yeah");

        assertFalse(linkedAsset.getLastContributor().equals(""));
        assertFalse(asset.getLastContributor().equals(""));

        linkedAsset.updateContent("new rule content");

        assertEquals("new rule content", linkedAsset.getContent());

        assertTrue(linkedAsset.getNode().getSession().hasPendingChanges());
        assertTrue(asset.getNode().getSession().hasPendingChanges());

        asset.checkin("yeah !");
        assertFalse(asset.getNode().getSession().hasPendingChanges());
        assertEquals("yeah !", asset.getCheckinComment());

        try {
            linkedAsset.checkin("yeah linked !");
            fail("Did not get expected exception: Unable to checkin");
        } catch (RulesRepositoryException e) {

        }

        AssetItem prev = (AssetItem)asset.getPrecedingVersion();
        assertEquals("test content", prev.getContent());
        assertFalse("yeah !".equals(prev.getCheckinComment()));

        asset = getDefaultPackage().loadAsset("testUpdateContentForShareableAsset");
        VersionIterator it = asset.getNode().getVersionHistory().getAllVersions();

        // and this shows using a version iterator.
        // perhaps migrate to using this rather then next/prev methods.
        // this way, we can skip.
        assertTrue(it.hasNext());
        while (it.hasNext()) {
            Version n = it.nextVersion();
            AssetItem item = new AssetItem(asset.getRulesRepository(), n);
            assertNotNull(item);
        }
    }

    @Test
    public void testCategoriesForShareableAsset() {
        getRepo().loadCategory("/").addCategory("testCategoriesTag", "description");
        getRepo().loadCategory("/").addCategory("testCategoriesTag2", "description");
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testCategoriesForShareableAsset", "desc");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        linkedAsset.addCategory("testCategoriesTag");
        List tags = linkedAsset.getCategories();
        assertEquals(1, tags.size());
        assertEquals("testCategoriesTag", ((CategoryItem)tags.get(0)).getName());

        linkedAsset.addCategory("testCategoriesTag2");
        tags = linkedAsset.getCategories();
        assertEquals(2, tags.size());

        linkedAsset.checkin("woot");

        // now test retrieve by tags
        List result = getRepo().findAssetsByCategory("testCategoriesTag", 0, -1).assets;
        assertEquals(1, result.size());
        AssetItem retItem = (AssetItem)result.get(0);
        assertEquals("testCategoriesForShareableAsset", retItem.getName());

        asset.updateContent("foo");
        asset.checkin("latest");

        assertTrue(asset.getCategories().size() > 0);
        assertNotNull(asset.getCategorySummary());
        assertEquals("testCategoriesTag testCategoriesTag2 ", asset.getCategorySummary());

        result = getRepo().findAssetsByCategory("testCategoriesTag2", 0, -1).assets;

        assertEquals(1, result.size());
        asset = (AssetItem)result.get(0);
        assertEquals(2, asset.getCategories().size());
    }

    @Test
    public void testUpdateCategoriesForShareableAsset() {
        getRepo().loadCategory("/").addCategory("testUpdateCategoriesForShareableAssetTag1", "la");
        getRepo().loadCategory("/").addCategory("testUpdateCategoriesForShareableAssetTag2", "la");
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testUpdateCategoriesForShareableAsset", "desc");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        String[] cats = new String[] {"testUpdateCategoriesForShareableAssetTag1", "testUpdateCategoriesForShareableAssetTag2"};
        linkedAsset.updateCategoryList(cats);

        linkedAsset.checkin("aaa");

        asset = getRepo().loadGlobalArea().loadAsset("testUpdateCategoriesForShareableAsset");
        assertEquals(2, asset.getCategories().size());

        for (Iterator iter = asset.getCategories().iterator(); iter.hasNext();) {
            CategoryItem cat = (CategoryItem)iter.next();
            assertTrue(cat.getName().startsWith("testUpdateCategoriesForShareableAssetTag"));
        }
    }

    @Test
    public void testRemoveTagForShareableAsset() {
        getRepo().loadCategory("/").addCategory("testRemoveTagForShareableAssetTag1", "la");
        getRepo().loadCategory("/").addCategory("testRemoveTagForShareableAssetTag2", "description");
        getRepo().loadCategory("/").addCategory("testRemoveTagForShareableAssetTag3", "description");
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testRemoveTagForShareableAsset", "desc");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        linkedAsset.addCategory("testRemoveTagForShareableAssetTag1");
        List tags = linkedAsset.getCategories();
        assertEquals(1, tags.size());
        linkedAsset.removeCategory("testRemoveTagForShareableAssetTag1");
        tags = linkedAsset.getCategories();
        assertEquals(0, tags.size());

        linkedAsset.addCategory("testRemoveTagForShareableAssetTag2");
        linkedAsset.addCategory("testRemoveTagForShareableAssetTag3");
        linkedAsset.checkin("aaa");

        linkedAsset.removeCategory("testRemoveTagForShareableAssetTag3");
        linkedAsset.checkin("aaa");
        getRepo().save();

        tags = linkedAsset.getCategories();
        assertEquals(1, tags.size());
        assertEquals("testRemoveTagForShareableAssetTag2", ((CategoryItem)tags.get(0)).getName());

        try {
            getRepo().loadCategory("testRemoveTagForShareableAssetTag2").remove();
            fail("should not be able to remove");
        } catch (RulesRepositoryException e) {
            assertNotNull(e.getMessage());
        }

        // REVISIT:
        /*		try {
        			getRepo().loadCategory("testRemoveTagForShareableAssetTag3").remove();
        			fail("should not reach here. Should have been removed");
        		} catch (RulesRepositoryException e) {
        			assertNotNull(e.getMessage());
        		}*/
    }

    @Test
    public void testStatusStufftestRemoveTagForShareableAsset() {
        getRepo().createState("testStatusStufftestRemoveTagForShareableAssetStatus");

        AssetItem asset = getRepo().loadGlobalArea().addAsset("testStatusStufftestRemoveTagForShareableAsset", "desc");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        StateItem stateItem1 = linkedAsset.getState();
        assertEquals(StateItem.DRAFT_STATE_NAME, stateItem1.getName());
        assertEquals(getRepo().getState(StateItem.DRAFT_STATE_NAME), linkedAsset.getState());
        assertEquals(StateItem.DRAFT_STATE_NAME, linkedAsset.getStateDescription());

        linkedAsset.updateState("testStatusStufftestRemoveTagForShareableAssetStatus");
        assertEquals("testStatusStufftestRemoveTagForShareableAssetStatus", linkedAsset.getState().getName());
    }

    @Test
    public void testGetDateEffectiveForShareableAsset() {
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testGetDateEffectiveForShareableAsset", "desc");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        // it should be initialized to null
        assertTrue(linkedAsset.getDateEffective() == null);

        // now try setting it, then retrieving it
        Calendar cal = Calendar.getInstance();
        linkedAsset.updateDateEffective(cal);
        Calendar cal2 = linkedAsset.getDateEffective();

        assertEquals(cal, cal2);
    }

    @Test
    public void testGetDateExpiredForShareableAsset() {
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testGetDateExpiredForShareableAsset", "desc");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        // it should be initialized to null
        assertTrue(linkedAsset.getDateExpired() == null);

        // now try setting it, then retrieving it
        Calendar cal = Calendar.getInstance();
        linkedAsset.updateDateExpired(cal);
        Calendar cal2 = linkedAsset.getDateExpired();

        assertEquals(cal, cal2);
    }

    @Test
    public void testSaveAndCheckinDescriptionAndTitleForShareableAsset() throws Exception {
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testSaveAndCheckinDescriptionAndTitleForShareableAsset", "desc");
        asset.checkin("version0");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        // it should be "" to begin with
        assertEquals("desc", linkedAsset.getDescription());

        linkedAsset.updateDescription("test description");
        assertEquals("test description", linkedAsset.getDescription());

        assertTrue(getRepo().getSession().hasPendingChanges());

        linkedAsset.updateTitle("This is a title");
        assertTrue(getRepo().getSession().hasPendingChanges());
        linkedAsset.checkin("ya");

        // we can save without a checkin
        getRepo().getSession().save();

        assertFalse(getRepo().getSession().hasPendingChanges());

        try {
            linkedAsset.getPrecedingVersion().updateTitle("baaad");
            fail("should not be able to do this");
        } catch (RulesRepositoryException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testGetPrecedingVersionAndRestoreForShareableAsset() throws Exception {
        getRepo().loadCategory("/").addCategory("testGetPrecedingVersionAndRestoreCat", "ka");
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testGetPrecedingVersionAndRestoreForShareableAsset", "desc");
        asset.checkin("version0");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        // assertTrue(asset.getPrecedingVersion() == null);
        assertNotNull(asset.getPrecedingVersion());
        assertNotNull(linkedAsset.getPrecedingVersion());

        linkedAsset.addCategory("testGetPrecedingVersionAndRestoreCat");
        linkedAsset.updateContent("test content");
        linkedAsset.updateDescription("descr2");
        Thread.sleep(100);
        linkedAsset.checkin("boo");

        AssetItem predecessorRuleItem = (AssetItem)linkedAsset.getPrecedingVersion();
        assertNotNull(predecessorRuleItem);

        // check version handling
        assertNotNull(predecessorRuleItem.getVersionSnapshotUUID());
        assertFalse(predecessorRuleItem.getVersionSnapshotUUID().equals(asset.getUUID()));

        // assertEquals(predecessorRuleItem.getCreatedDate().getTimeInMillis(),
        // ruleItem1.getCreatedDate().getTimeInMillis());

        assertEquals(asset.getState().getName(), predecessorRuleItem.getState().getName());
        // assertEquals(ruleItem1.getName(), predecessorRuleItem.getName());

        AssetItem loadedHistorical = getRepo().loadAssetByUUID(predecessorRuleItem.getVersionSnapshotUUID());
        assertTrue(loadedHistorical.isHistoricalVersion());
        assertFalse(asset.getVersionNumber() == loadedHistorical.getVersionNumber());

        linkedAsset.updateContent("new content");
        linkedAsset.checkin("two changes");

        predecessorRuleItem = (AssetItem)linkedAsset.getPrecedingVersion();
        assertNotNull(predecessorRuleItem);
        assertEquals(1, predecessorRuleItem.getCategories().size());
        CategoryItem cat = (CategoryItem)predecessorRuleItem.getCategories().get(0);
        assertEquals("testGetPrecedingVersionAndRestoreCat", cat.getName());

        assertEquals("test content", predecessorRuleItem.getContent());

        assertEquals(getRepo().loadGlobalArea().getName(), predecessorRuleItem.getPackageName());

        linkedAsset.updateContent("newer lhs");
        linkedAsset.checkin("another");

        predecessorRuleItem = (AssetItem)linkedAsset.getPrecedingVersion();
        assertNotNull(predecessorRuleItem);
        assertEquals("new content", predecessorRuleItem.getContent());
        predecessorRuleItem = (AssetItem)predecessorRuleItem.getPrecedingVersion();
        assertNotNull(predecessorRuleItem);
        assertEquals("test content", predecessorRuleItem.getContent());

        // now try restoring
        long oldVersionNumber = asset.getVersionNumber();

        AssetItem toRestore = getRepo().loadAssetByUUID(predecessorRuleItem.getVersionSnapshotUUID());

        getRepo().restoreHistoricalAsset(toRestore, linkedAsset, "cause I want to");

        AssetItem restored = getRepo().loadDefaultPackage().loadAsset("testGetPrecedingVersionAndRestoreForShareableAsset");

        // assertEquals( predecessorRuleItem.getCheckinComment(),
        // restored.getCheckinComment());
        assertEquals(predecessorRuleItem.getDescription(), restored.getDescription());
        assertEquals("cause I want to", restored.getCheckinComment());
        assertEquals(6, restored.getVersionNumber());
        assertFalse(oldVersionNumber == restored.getVersionNumber());
    }

    @Test
    public void testGetSucceedingVersionForShareableAsset() {
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testGetSucceedingVersionForShareableAsset", "desc");
        asset.checkin("version0");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        // Making the assset sharable creates the version 2.
        assertEquals(2, asset.getVersionNumber());
        linkedAsset.updateContent("new content1");
        linkedAsset.checkin("la");

        AssetItem succeedingRuleItem = (AssetItem)linkedAsset.getSucceedingVersion();
        assertTrue(succeedingRuleItem == null);

        linkedAsset.updateContent("new content2");
        linkedAsset.checkin("la");

        assertEquals(4, linkedAsset.getVersionNumber());

        AssetItem predecessorRuleItem = (AssetItem)linkedAsset.getPrecedingVersion();
        assertEquals("new content1", predecessorRuleItem.getContent());
        succeedingRuleItem = (AssetItem)predecessorRuleItem.getSucceedingVersion();
        assertNotNull(succeedingRuleItem);
        assertEquals(linkedAsset.getContent(), succeedingRuleItem.getContent());
    }

    @Test
    public void testGetSuccessorVersionsIteratorForShareableAsset() {
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testGetSuccessorVersionsIteratorForShareableAsset", "desc");
        asset.checkin("version0");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        Iterator iterator = linkedAsset.getSuccessorVersionsIterator();
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());

        linkedAsset.updateContent("new content").checkin("ya");

        iterator = linkedAsset.getSuccessorVersionsIterator();
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());

        AssetItem predecessorRuleItem = (AssetItem)linkedAsset.getPrecedingVersion();
        iterator = predecessorRuleItem.getSuccessorVersionsIterator();
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        AssetItem nextRuleItem = (AssetItem)iterator.next();
        assertEquals("new content", nextRuleItem.getContent());
        assertFalse(iterator.hasNext());

        linkedAsset.updateContent("newer content");
        linkedAsset.checkin("boo");

        iterator = predecessorRuleItem.getSuccessorVersionsIterator();
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        nextRuleItem = (AssetItem)iterator.next();
        assertEquals("new content", nextRuleItem.getContent());
        assertTrue(iterator.hasNext());
        nextRuleItem = (AssetItem)iterator.next();
        assertEquals("newer content", nextRuleItem.getContent());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testGetPredecessorVersionsIteratorForShareableAsset() {
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testGetPredecessorVersionsIteratorForShareableAsset", "desc");
        asset.checkin("version0");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        linkedAsset.updateContent("test content");
        linkedAsset.checkin("lalalalala");

        Iterator iterator = linkedAsset.getPredecessorVersionsIterator();
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());

        asset.updateContent("new content");
        asset.checkin("boo");

        iterator = linkedAsset.getPredecessorVersionsIterator();
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        AssetItem nextRuleItem = (AssetItem)iterator.next();

        assertEquals("test content", nextRuleItem.getContent());

        asset.updateContent("newer content");
        asset.checkin("wee");

        iterator = linkedAsset.getPredecessorVersionsIterator();
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        nextRuleItem = (AssetItem)iterator.next();
        assertTrue(iterator.hasNext());
        assertEquals("new content", nextRuleItem.getContent());
        nextRuleItem = (AssetItem)iterator.next();

        assertEquals("test content", nextRuleItem.getContent());

        assertEquals("", ((AssetItem)iterator.next()).getContent());
    }

    @Test
    public void testHistoryIteratorForShareableAsset() throws Exception {
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testHistoryIteratorForShareableAsset", "desc");
        asset.checkin("version0");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        linkedAsset = getRepo().loadAssetByUUID(linkedAsset.getUUID());
        linkedAsset.updateContent("wo");
        linkedAsset.checkin("version2");

        asset = getRepo().loadAssetByUUID(asset.getUUID());
        asset.updateContent("ya");
        asset.checkin("version3");

        Iterator it = asset.getHistory();
        for (int i = 0; i < 3; i++) {
            assertTrue(it.hasNext());
            it.next();
        }
    }

    @Test
    public void testGetTitleForShareableAsset() {
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testGetTitleForShareableAsset", "desc");
        asset.checkin("version0");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        assertEquals("testGetTitleForShareableAsset", linkedAsset.getName());
        assertEquals("testGetTitleForShareableAsset", asset.getName());
        // NOTE: Linked AssetItem does not have its own Title property.
        assertEquals("testGetTitleForShareableAsset", linkedAsset.getTitle());
        assertEquals("testGetTitleForShareableAsset", asset.getTitle());
    }

    @Test
    public void testDublinCorePropertiesForShareableAsset() {
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testDublinCorePropertiesForShareableAsset", "desc");
        asset.checkin("version0");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        linkedAsset.updateCoverage("b");
        assertEquals("b", linkedAsset.getCoverage());
        linkedAsset.checkin("woo");

        linkedAsset = getDefaultPackage().loadAsset("testDublinCorePropertiesForShareableAsset");
        assertEquals("b", linkedAsset.getCoverage());
        assertEquals("", linkedAsset.getExternalRelation());
        assertEquals("", linkedAsset.getExternalSource());

        linkedAsset = getRepo().loadGlobalArea().loadAsset("testDublinCorePropertiesForShareableAsset");
        assertEquals("b", linkedAsset.getCoverage());
        assertEquals("", linkedAsset.getExternalRelation());
        assertEquals("", linkedAsset.getExternalSource());
    }

    @Test
    public void testGetFormatForShareableAsset() throws Exception {
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testGetFormatForShareableAsset", "desc");
        asset.checkin("version0");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        linkedAsset.updateContent("la");
        assertEquals(AssetItem.DEFAULT_CONTENT_FORMAT, linkedAsset.getFormat());

        assertTrue(linkedAsset.getNode().hasProperty(AssetItem.CONTENT_PROPERTY_NAME));
        assertFalse(linkedAsset.getNode().hasProperty(AssetItem.CONTENT_PROPERTY_BINARY_NAME));

        linkedAsset.updateFormat("blah");
        assertEquals("blah", linkedAsset.getFormat());
    }

    @Test
    public void testAnonymousPropertiesForShareableAsset() {
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testAnonymousPropertiesForShareableAsset", "desc");
        asset.checkin("version0");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        linkedAsset.updateUserProperty("fooBar", "value");
        assertEquals("value", linkedAsset.getUserProperty("fooBar"));

        linkedAsset.checkin("lalalala");
        try {
            linkedAsset.updateUserProperty("drools:content", "whee");
            fail("should not be able to set built in properties this way.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testBinaryAssetForShareableAsset() throws Exception {
        AssetItem asset = getRepo().loadGlobalArea().addAsset("testBinaryAssetForShareableAsset", "desc");
        asset.checkin("version0");
        AssetItem linkedAsset = getDefaultPackage().addAssetImportedFromGlobalArea(asset.getName());

        String data = "abc 123";
        ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes());
        linkedAsset.updateBinaryContentAttachment(in);
        linkedAsset.updateBinaryContentAttachmentFileName("x.x");
        in.close();

        assertEquals(data, linkedAsset.getContent());

        assertFalse(linkedAsset.getNode().hasProperty(AssetItem.CONTENT_PROPERTY_NAME));
        assertTrue(linkedAsset.getNode().hasProperty(AssetItem.CONTENT_PROPERTY_BINARY_NAME));
        linkedAsset.checkin("lalalala");

        assertTrue(linkedAsset.isBinary());

        asset = getRepo().loadGlobalArea().loadAsset("testBinaryAssetForShareableAsset");
        InputStream in2 = asset.getBinaryContentAttachment();
        assertNotNull(in2);

        byte[] data2 = asset.getBinaryContentAsBytes();
        assertEquals(data, new String(data2));
        assertEquals("x.x", asset.getBinaryContentAttachmentFileName());
        assertTrue(asset.isBinary());

        linkedAsset.updateContent("qed");
        linkedAsset.checkin("");
        linkedAsset = getRepo().loadAssetByUUID(linkedAsset.getUUID());
        assertEquals("qed", linkedAsset.getContent());
    }

}
