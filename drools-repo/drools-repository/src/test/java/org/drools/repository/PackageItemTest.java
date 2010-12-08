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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class PackageItemTest extends TestCase {

	private PackageItem loadGlobalArea() {
		return getRepo().loadGlobalArea();
	}
	
    public void testListPackages() throws Exception {
        RulesRepository repo = getRepo();
        PackageItem item = repo.createPackage( "testListPackages1", "lalalala" );

        assertNotNull(item.getCreator());

        item.updateStringProperty( "goo", "whee" );
        assertEquals("goo", item.getStringProperty( "whee" ));
        assertFalse(item.getCreator().equals( "" ));

        List list = iteratorToList( repo.listPackages() );
        int prevSize = list.size();
        repo.createPackage( "testListPackages2", "abc" );

        list = iteratorToList( repo.listPackages() );

        assertEquals(prevSize + 1, list.size());
    }

//    public void testAddPackageProperties() throws Exception {
//        RulesRepository repo = getRepo();
//        PackageItem item = repo.createPackage( "testListPackages1", "lalalala" );
//
//        assertNotNull(item.getCreator());
//
//        HashMap hash = new HashMap();
//        hash.put("Eligibility rules", "Underage");
//
//        String[] testProp = new String[]{"Test1","Test2"};
//
//        item.node.checkout();
//        item.node.setProperty("testing", testProp);
//        //item.node.setProperty("testing", "blah");
//
//        String[] newProp = item.getStringPropertyArray( "testing" );
//        assertTrue((testProp[0]).equals(newProp[0]));
//        assertTrue(("Test2").equals(newProp[1]));
//
      // assertEquals(testProp[0], );
//        assertFalse(item.getCreator().equals( "" ));
//
//        List list = iteratorToList( repo.listPackages() );
//        int prevSize = list.size();
//        repo.createPackage( "testListPackages2", "abc" );
//
//        list = iteratorToList( repo.listPackages() );
//
//        assertEquals(prevSize + 1, list.size());
//    }

    public void testPackageRemove() throws Exception {
        RulesRepository repo = getRepo();
        PackageItem p = repo.createPackage("removeMe", "");
        AssetItem a = p.addAsset("Whee", "");
        a.updateContent("yeah");
        a.checkin("la");
        p.addAsset("Waa", "");
        repo.save();


        PackageItem pkgNested = p.createSubPackage("NestedGoodness");
        assertNotNull(pkgNested);

        int n = iteratorToList(repo.listPackages()).size();

        p = repo.loadPackage("removeMe");
        p.remove();
        repo.save();

        int n_ = iteratorToList(repo.listPackages()).size();
        assertEquals(n - 1, n_);
    }

    public void testRulePackageItem() throws Exception {
        RulesRepository repo = getRepo();

        //calls constructor
        PackageItem rulePackageItem1 = repo.createPackage("testRulePackage", "desc");
        assertNotNull(rulePackageItem1);
        assertEquals("testRulePackage", rulePackageItem1.getName());

        Iterator it = getRepo().listPackages();
        assertTrue(it.hasNext());

        while (it.hasNext()) {
            PackageItem pack = (PackageItem) it.next();
            if (pack.getName().equals( "testRulePackage" )) {
                return;
            }
        }
        fail("should have picked up the testRulePackage but didnt.");
    }

    /**
     * This is showing how to copy a package with standard JCR
     */
    public void testPackageCopy() throws Exception {
        RulesRepository repo = getRepo();

        PackageItem pkg = repo.createPackage( "testPackageCopy", "this is something" );

        AssetItem it1 = pkg.addAsset( "testPackageCopy1", "la" );
        AssetItem it2 = pkg.addAsset( "testPackageCopy2", "la" );

        it1.updateContent( "new content" );
        it2.updateContent( "more content" );
        it1.checkin( "c" );
        it2.checkin( "c" );

        it1 = pkg.loadAsset( "testPackageCopy1" );
        List hist1 = iteratorToList( it1.getHistory() );
        System.out.println(hist1.size());


        repo.getSession().getWorkspace().copy( pkg.getNode().getPath(), pkg.getNode().getPath() + "_");

        PackageItem pkg2 = repo.loadPackage( "testPackageCopy_" );
        assertNotNull(pkg2);

        assertEquals(2, iteratorToList( pkg2.getAssets() ).size() );
        AssetItem it1_ = pkg2.loadAsset( "testPackageCopy1" );

        it1.updateContent( "new content2" );
        it1.checkin( "la" );
        it1_ = pkg2.loadAsset( "testPackageCopy1" );
        assertEquals("new content", it1_.getContent());
    }

    public void testPackageSnapshot() throws Exception {
        RulesRepository repo = getRepo();

        PackageItem pkg = repo.createPackage( "testPackageSnapshot", "this is something" );
        assertFalse(pkg.isSnapshot());


        AssetItem it1 = pkg.addAsset( "testPackageCopy1", "la" );
        AssetItem it2 = pkg.addAsset( "testPackageCopy2", "la" );

        it1.updateContent( "new content" );
        it1.updateFormat( "drl" );
        it2.updateContent( "more content" );
        it2.updateFormat( "drl" );
        it1.checkin( "c" );
        it2.checkin( "c" );

        long ver1 = it1.getVersionNumber();
        long ver2 = it2.getVersionNumber();
        assertFalse( ver1 == 0 );

        assertEquals(2, iteratorToList(pkg.listAssetsByFormat( new String[] {"drl"} )).size());
        repo.createPackageSnapshot( "testPackageSnapshot", "PROD 2.0" );

        //just check we can load it all via UUID as well...
        PackageItem pkgLoaded = repo.loadPackageSnapshot( "testPackageSnapshot", "PROD 2.0" );
        assertTrue(pkgLoaded.isSnapshot());
        assertEquals("PROD 2.0", pkgLoaded.getSnapshotName());
        assertEquals("testPackageSnapshot", pkgLoaded.getName());

        PackageItem _pkgLoaded = repo.loadPackageByUUID( pkgLoaded.getUUID() );
        assertNotNull(_pkgLoaded);
        assertEquals(pkgLoaded.getCreatedDate(), _pkgLoaded.getCreatedDate());
        assertEquals(pkgLoaded.getName(), _pkgLoaded.getName());
        //assertEquals("testPackageSnapshot", pkgLoaded.getName());
        List loadedAssets = iteratorToList( pkgLoaded.getAssets() );
        List _loadedAssets = iteratorToList( _pkgLoaded.getAssets() );
        assertEquals(loadedAssets.size(), _loadedAssets.size());

        //now make some changes on the main line
        it1.updateContent( "XXX" );
        it1.checkin( "X" );
        assertFalse(it1.getVersionNumber()==  ver1 );
        AssetItem it3 = pkg.addAsset( "testPackageCopy3", "x" );
        it3.updateFormat( "drl" );
        it3.checkin( "a" );
        assertEquals(3, iteratorToList( pkg.listAssetsByFormat( new String[] {"drl"} )).size());



        PackageItem pkg2 = repo.loadPackageSnapshot( "testPackageSnapshot", "PROD 2.0" );
        assertNotNull(pkg2);
        List snapAssets = iteratorToList( pkg2.getAssets() );
        assertEquals(2, snapAssets.size());
        assertFalse(pkg2.getUUID().equals( pkg.getUUID() ));
        assertTrue(snapAssets.get( 0 ) instanceof AssetItem);
        assertTrue(snapAssets.get( 1 ) instanceof AssetItem);

        AssetItem sn1 = (AssetItem) snapAssets.get( 0 );
        AssetItem sn2 = (AssetItem) snapAssets.get( 1 );
        assertEquals("la", sn1.getDescription());
        assertEquals("la", sn2.getDescription());
        assertEquals(ver1, sn1.getVersionNumber());
        assertEquals(ver2, sn2.getVersionNumber());


        assertEquals(2, iteratorToList(pkg2.listAssetsByFormat( new String[] {"drl"} )).size());

        //now check we can list the snappies
        String[] res = repo.listPackageSnapshots("testPackageSnapshot");

        assertEquals(1, res.length);
        assertEquals("PROD 2.0", res[0]);

        res = repo.listPackageSnapshots( "does not exist" );
        assertEquals(0, res.length);

        repo.removePackageSnapshot( "testPackageSnapshot", "XX" );
        //does nothing... but should not barf...
        try {
            repo.removePackageSnapshot( "NOTHING SENSIBLE", "XX" );
            fail("should not be able to remove this.");
        } catch (RulesRepositoryException e) {
            assertNotNull(e.getMessage());
        }

        repo.removePackageSnapshot( "testPackageSnapshot", "PROD 2.0" );
        repo.save();

        res = repo.listPackageSnapshots( "testPackageSnapshot" );
        assertEquals(0, res.length);

        repo.createPackageSnapshot( "testPackageSnapshot", "BOO" );
        res = repo.listPackageSnapshots( "testPackageSnapshot" );
        assertEquals(1, res.length);
        repo.copyPackageSnapshot( "testPackageSnapshot", "BOO", "BOO2" );
        res = repo.listPackageSnapshots( "testPackageSnapshot" );
        assertEquals(2, res.length);



        repo.copyPackageSnapshot( "testPackageSnapshot", "BOO", "BOO2" );
        res = repo.listPackageSnapshots( "testPackageSnapshot" );
        assertEquals(2, res.length);


        assertEquals("BOO", res[0]);
        assertEquals("BOO2", res[1]);
    }

    private RulesRepository getRepo() {
        return RepositorySessionUtil.getRepository();
    }

    public void testLoadRulePackageItem() {

        PackageItem rulePackageItem = getRepo().createPackage("testLoadRuleRuleItem", "desc");

        rulePackageItem = getRepo().loadPackage("testLoadRuleRuleItem");
        assertNotNull(rulePackageItem);
        assertEquals("testLoadRuleRuleItem", rulePackageItem.getName());

        assertEquals("desc", rulePackageItem.getDescription());
        assertEquals(PackageItem.PACKAGE_FORMAT, rulePackageItem.getFormat());
        // try loading rule package that was not created
        try {
            rulePackageItem = getRepo().loadPackage("anotherRuleRuleItem");
            fail("Exception not thrown loading rule package that was not created.");
        } catch (RulesRepositoryException e) {
            // that is OK!
            assertNotNull(e.getMessage());
        }
    }

    /**
     * This will test getting rules of specific versions out of a package.
     */
    public void testPackageRuleVersionExtraction() throws Exception {
        PackageItem pack = getRepo().createPackage( "package extractor", "foo" );

        AssetItem rule1 = pack.addAsset( "rule number 1", "yeah man" );
        rule1.checkin( "version0" );

        AssetItem rule2 = pack.addAsset( "rule number 2", "no way" );
        rule2.checkin( "version0" );

        AssetItem rule3 = pack.addAsset( "rule number 3", "yes way" );
        rule3.checkin( "version0" );

        getRepo().save();

        pack = getRepo().loadPackage( "package extractor" );
        List rules = iteratorToList( pack.getAssets() );
        assertEquals(3, rules.size());

        getRepo().createState( "foobar" );

        StateItem state = getRepo().getState( "foobar" );

        rule1.updateState( "foobar" );
        rule1.checkin( "yeah" );

        pack = getRepo().loadPackage( "package extractor" );

        rules = iteratorToList( pack.getAssetsWithStatus(state) );

        assertEquals(1, rules.size());

        //now lets try an invalid state tag
        getRepo().createState( "whee" );
        rules = iteratorToList( pack.getAssetsWithStatus( getRepo().getState( "whee" ) ) );
        assertEquals(0, rules.size());

        //and Draft, as we start with Draft, should be able to get all three back
        //although an older version of one of them
        rules = iteratorToList( pack.getAssetsWithStatus(getRepo().getState( StateItem.DRAFT_STATE_NAME )) );
        assertEquals(3, rules.size());

        //now do an update, and pull it out via state
        rule1.updateContent( "new content" );
        getRepo().createState( "extractorState" );
        rule1.updateState( "extractorState" );
        rule1.checkin( "latest" );

        rules = iteratorToList( pack.getAssetsWithStatus(getRepo().getState( "extractorState" )) );
        assertEquals(1, rules.size());
        AssetItem rule = (AssetItem) rules.get( 0 );
        assertEquals("new content", rule.getContent());

        //get the previous one via state

        getRepo().createState( "foobar" );
        rules = iteratorToList( pack.getAssetsWithStatus(getRepo().getState( "foobar" )) );
        assertEquals(1, rules.size());
        AssetItem prior = (AssetItem) rules.get( 0 );

        assertFalse("new content".equals( prior.getContent() ));
    }

    public void testIgnoreState() throws Exception {
        PackageItem pack = getRepo().createPackage( "package testIgnoreState", "foo" );

        getRepo().createState( "x" );
        AssetItem rule1 = pack.addAsset( "rule number 1", "yeah man" );
        rule1.updateState( "x" );
        rule1.checkin( "version0" );


        AssetItem rule2 = pack.addAsset( "rule number 2", "no way" );
        rule2.updateState( "x" );
        rule2.checkin( "version0" );

        AssetItem rule3 = pack.addAsset( "rule number 3", "yes way" );
        getRepo().createState( "disabled" );

        rule3.updateState( "disabled" );
        rule3.checkin( "version0" );

        getRepo().save();


        Iterator result = pack.getAssetsWithStatus( getRepo().getState( "x" ), getRepo().getState( "disabled" ) );
        List l = iteratorToList( result );
        assertEquals(2, l.size());
    }

    public void testDuplicatePackageName() throws Exception {
        PackageItem pack = getRepo().createPackage( "dupePackageTest", "testing for dupe" );
        assertNotNull(pack.getName());

        try {
            getRepo().createPackage( "dupePackageTest", "this should fail" );
            fail("Should not be able to add a package of the same name.");
        } catch (RulesRepositoryException e) {
            assertNotNull(e.getMessage());
        }
    }

    public void testPackageInstanceWrongNodeType() throws Exception {
        PackageItem pack = getRepo().loadDefaultPackage();
        AssetItem rule = pack.addAsset( "packageInstanceWrongNodeType", "" );

        try {
            new PackageItem(this.getRepo(), rule.getNode());
            fail("Can't create a package from a rule node.");
        } catch (RulesRepositoryException e) {
            assertNotNull(e.getMessage());
        }
    }

    public void testLoadRulePackageItemByUUID() throws Exception {

        PackageItem rulePackageItem = getRepo().createPackage("testLoadRuleRuleItemByUUID", "desc");

        String uuid = null;
            uuid = rulePackageItem.getNode().getUUID();


        rulePackageItem = getRepo().loadPackageByUUID(uuid);
        assertNotNull(rulePackageItem);
        assertEquals("testLoadRuleRuleItemByUUID", rulePackageItem.getName());

        // try loading rule package that was not created
        try {
            rulePackageItem = getRepo().loadPackageByUUID("01010101-0101-0101-0101-010101010101");
            fail("Exception not thrown loading rule package that was not created.");
        } catch (RulesRepositoryException e) {
            // that is OK!
            assertNotNull(e.getMessage());
        }
    }

    public void testAddAssetTrailingWhitespace() {
        PackageItem pkg = getRepo().createPackage("testAddAssetTrailingWhitespace","desc");
        pkg.addAsset("wee ", "");

        assertNotNull(pkg.loadAsset("wee"));
    }

    public void testAddRuleRuleItem() {
            PackageItem rulePackageItem1 = getRepo().createPackage("testAddRuleRuleItem","desc");


            AssetItem ruleItem1 = rulePackageItem1.addAsset("testAddRuleRuleItem", "test description");
            ruleItem1.updateContent( "test content" );
            ruleItem1.checkin( "updated the rule content" );

            Iterator rulesIt = rulePackageItem1.getAssets();
            assertNotNull(rulesIt);
            AssetItem first = (AssetItem) rulesIt.next();
            assertFalse(rulesIt.hasNext());
            assertEquals("testAddRuleRuleItem", first.getName());

            //test that it is following the head revision
            ruleItem1.updateContent("new lhs");
            ruleItem1.checkin( "updated again" );
            rulesIt = rulePackageItem1.getAssets();
            assertNotNull(rulesIt);

            List rules = iteratorToList( rulesIt );
            assertEquals(1, rules.size());
            assertEquals("testAddRuleRuleItem", ((AssetItem)rules.get(0)).getName());
            assertEquals("new lhs", ((AssetItem)rules.get(0)).getContent());

            AssetItem ruleItem2 = rulePackageItem1.addAsset("testAddRuleRuleItem2", "test content");

            rules = iteratorToList(rulePackageItem1.getAssets());
            assertNotNull(rules);
            assertEquals(2, rules.size());
    }

    public void testAddRuleItemFromGlobalArea() {
        AssetItem ruleItem1 = loadGlobalArea().addAsset("testAddRuleItemFromGlobalAreaRuleItem", "test description");
        ruleItem1.updateContent( "test content" );
        ruleItem1.checkin( "updated the rule content" );
        
        PackageItem rulePackageItem2 = getRepo().createPackage("testAddRuleItemFromGlobalArea1","desc");
        AssetItem linkedRuleItem1 = rulePackageItem2.addAssetImportedFromGlobalArea(ruleItem1.getName());
        linkedRuleItem1.updateContent( "test content for linked" );
        linkedRuleItem1.checkin( "updated the rule content for linked" );       
 
        //test that it is following the head revision
        ruleItem1.updateContent("new lhs");
        ruleItem1.checkin( "updated again" );        
        
        Iterator rulesIt2 = rulePackageItem2.getAssets();
		List rules2 = iteratorToList(rulesIt2);
		assertEquals(1, rules2.size());

		AssetItem ai = (AssetItem) rules2.get(0);
		assertTrue(ai.getName().equals("testAddRuleItemFromGlobalAreaRuleItem"));
		assertEquals("new lhs", ai.getContent());
		assertEquals("test description", ai.getDescription());
		assertEquals("updated again", ai.getCheckinComment());       
    }
    
    List iteratorToList(Iterator it) {
        List list = new ArrayList();
        while(it.hasNext()) {
            list.add( it.next() );
        }
        return list;
    }

    public void testGetRules() {
            PackageItem rulePackageItem1 = getRepo().createPackage("testGetRules", "desc");

            assertFalse(rulePackageItem1.containsAsset("goober"));


            AssetItem ruleItem1 = rulePackageItem1.addAsset("testGetRules", "desc" );
            ruleItem1.updateContent( "test lhs content" );


            assertTrue(rulePackageItem1.containsAsset( "testGetRules" ));
            assertFalse(rulePackageItem1.containsAsset( "XXXXYYYYZZZZ" ));


            List rules = iteratorToList(rulePackageItem1.getAssets());
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("testGetRules", ((AssetItem)rules.get(0)).getName());

            AssetItem ruleItem2 = rulePackageItem1.addAsset("testGetRules2", "desc" );
            ruleItem2.updateContent( "test lhs content" );

            rules = iteratorToList(rulePackageItem1.getAssets());
            assertNotNull(rules);
            assertEquals(2, rules.size());

            //now lets test loading rule
            AssetItem loaded = rulePackageItem1.loadAsset( "testGetRules" );
            assertNotNull(loaded);
            assertEquals("testGetRules", loaded.getName());
            assertEquals("desc", loaded.getDescription());
    }

    public void testToString() {
            PackageItem rulePackageItem1 = getRepo().createPackage("testToStringPackage", "desc");

            AssetItem ruleItem1 = rulePackageItem1.addAsset("testToStringPackage", "test lhs content" );
            ruleItem1.updateContent( "test lhs content" );

            assertNotNull(rulePackageItem1.toString());
    }

    public void testRemoveRule() {
            PackageItem rulePackageItem1 = getRepo().createPackage("testRemoveRule", "desc");

            AssetItem ruleItem1 = rulePackageItem1.addAsset("testRemoveRule", "test lhs content" );
            ruleItem1.updateContent( "test lhs content" );



            Iterator rulesIt = rulePackageItem1.getAssets();
            AssetItem next = (AssetItem) rulesIt.next();

            assertFalse(rulesIt.hasNext());
            assertEquals("testRemoveRule", next.getName());



            ruleItem1.updateContent("new lhs");
            List rules = iteratorToList(rulePackageItem1.getAssets());
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("testRemoveRule", ((AssetItem)rules.get(0)).getName());
            assertEquals("new lhs", ((AssetItem)rules.get(0)).getContent());

            AssetItem ruleItem2 = rulePackageItem1.addAsset("testRemoveRule2", "test lhs content");

            //remove the rule, make sure the other rule in the pacakge stays around
            rulePackageItem1.loadAsset(ruleItem1.getName()).remove();
            rulePackageItem1.rulesRepository.save();
            rules = iteratorToList(rulePackageItem1.getAssets());
            assertEquals(1, rules.size());
            assertEquals("testRemoveRule2", ((AssetItem)rules.get(0)).getName());

            //remove the rule that is following the head revision, make sure the pacakge is now empty
            rulePackageItem1.loadAsset(ruleItem2.getName()).remove();
            rules = iteratorToList(rulePackageItem1.getAssets());
            assertNotNull(rules);
            assertEquals(0, rules.size());
    }

    public void testSearchByFormat() throws Exception {
        PackageItem pkg = getRepo().createPackage( "searchByFormat", "" );
        getRepo().save();


        AssetItem item = pkg.addAsset( "searchByFormatAsset1", "" );
        item.updateFormat( "xyz" );
        item.checkin( "la" );

        item = pkg.addAsset( "searchByFormatAsset2", "wee" );
        item.updateFormat( "xyz" );
        item.checkin( "la" );

        item = pkg.addAsset( "searchByFormatAsset3", "wee" );
        item.updateFormat( "ABC" );
        item.checkin( "la" );

        Thread.sleep( 150 );

        AssetItemIterator it = pkg.queryAssets( "drools:format='xyz'" );
        List list = iteratorToList( it );
        assertEquals(2, list.size());
        assertTrue(list.get( 0 ) instanceof AssetItem);
        assertTrue(list.get( 1 ) instanceof AssetItem);


        AssetItemIterator it2 = pkg.listAssetsByFormat( new String[] {"xyz"} );
        List list2 = iteratorToList( it2 );
        assertEquals(2, list2.size());
        assertTrue(list2.get( 0 ) instanceof AssetItem);
        assertTrue(list2.get( 1 ) instanceof AssetItem);

        it2 = pkg.listAssetsByFormat( new String[] {"xyz", "ABC"} );
        list2 = iteratorToList( it2 );
        assertEquals(3, list2.size());
        assertTrue(list2.get( 0 ) instanceof AssetItem);
        assertTrue(list2.get( 1 ) instanceof AssetItem);
        assertTrue(list2.get( 2 ) instanceof AssetItem);
    }

    public void testSearchSharedAssetByFormat() throws Exception {
        AssetItem item = loadGlobalArea().addAsset( "testSearchSharedAssetByFormat", "" );
        item.updateFormat( "testSearchSharedAssetByFormat" );
        item.checkin( "la" );
        
        AssetItemIterator it = loadGlobalArea().queryAssets( "drools:format='testSearchSharedAssetByFormat'" );
        List list = iteratorToList( it );
        assertEquals(1, list.size());
        assertTrue(list.get( 0 ) instanceof AssetItem);
        
        PackageItem pkg2 = getRepo().createPackage( "testSearchSharedAssetByFormat", "" );
        getRepo().save();
        AssetItem linkedItem = pkg2.addAssetImportedFromGlobalArea(item.getName());

        Thread.sleep( 150 );

        item = loadGlobalArea().loadAsset("testSearchSharedAssetByFormat");
        assertEquals("testSearchSharedAssetByFormat", item.getFormat());

        it = loadGlobalArea().queryAssets( "drools:format='testSearchSharedAssetByFormat'" );
        list = iteratorToList( it );
        assertEquals(1, list.size());
        assertTrue(list.get( 0 ) instanceof AssetItem);
 
/*        linkedItem = pkg2.loadAsset("testSearchLinkedAssetByFormatAsset2");
        assertNotNull(linkedItem);
        assertEquals("global", linkedItem.getPackageName());
       	
        it = pkg2.queryAssets( "drools:format='xyz'" );
        list = iteratorToList( it );*/
        
        //REVISIT: Not working yet.
        //assertEquals(1, list.size());
        //assertTrue(list.get( 0 ) instanceof AssetItem);
    }
    
    public void testListArchivedAssets() throws Exception {
        PackageItem pkg = getRepo().createPackage( "org.drools.archivedtest", "" );
        getRepo().save();


        AssetItem item = pkg.addAsset( "archivedItem1", "" );
        item.archiveItem( true );
        item.checkin( "la" );

        item = pkg.addAsset( "archivedItem2", "wee" );
        item.archiveItem( true );
        item.checkin( "la" );

        item = pkg.addAsset( "archivedItem3", "wee" );
        item.archiveItem( true );
        item.checkin( "la" );

        item = pkg.addAsset( "NOTarchivedItem", "wee" );
        item.checkin( "la" );


        Thread.sleep( 150 );

        AssetItemIterator it = pkg.listArchivedAssets();

        List list = iteratorToList( it );
        assertEquals(3, list.size());
        assertTrue(list.get( 0 ) instanceof AssetItem);
        assertTrue(list.get( 1 ) instanceof AssetItem);
        assertTrue(list.get( 2 ) instanceof AssetItem);


        it = pkg.queryAssets( "", true );

        list = iteratorToList( it );
        assertEquals(4, list.size());
    }

    public void testExcludeAssetTypes() throws Exception {
        PackageItem pkg = getRepo().createPackage( "testExcludeAssetTypes", "" );
        getRepo().save();


        AssetItem item = pkg.addAsset( "a1", "" );
        item.updateFormat("drl");
        item.checkin( "la" );

        item = pkg.addAsset( "a2", "wee" );
        item.updateFormat("xls");
        item.checkin( "la" );


        AssetItemIterator it = pkg.listAssetsNotOfFormat(new String[] {"drl"});
        List ls = iteratorToList(it);
        assertEquals(1, ls.size());
        AssetItem as = (AssetItem) ls.get(0);
        assertEquals("a2", as.getName());

        it = pkg.listAssetsNotOfFormat(new String[] {"drl", "wang"});
        ls = iteratorToList(it);
        assertEquals(1, ls.size());
        as = (AssetItem) ls.get(0);
        assertEquals("a2", as.getName());

        it = pkg.listAssetsNotOfFormat(new String[] {"drl", "xls"});
        ls = iteratorToList(it);
        assertEquals(0, ls.size());
    }

    public void testSortHistoryByVersionNumber() {
        PackageItem item = new PackageItem();
        List l = new ArrayList();

        AssetItem i1 = new MockAssetItem(42);
        AssetItem i2 = new MockAssetItem(1);

        l.add( i2 );
        l.add( i1 );

        assertEquals(i2, l.iterator().next());

        item.sortHistoryByVersionNumber( l );

        assertEquals(i1, l.iterator().next());
    }


    public void testMiscProperties() {
        PackageItem item = getRepo().createPackage( "testHeader", "ya" );

        updateHeader( "new header", item );
        item.updateExternalURI( "boo" );
        getRepo().save();
        assertEquals("new header", getHeader(item));
        item = getRepo().loadPackage("testHeader");
        assertEquals("new header", getHeader(item));
        assertEquals("boo", item.getExternalURI());
    }

    public void testGetFormatAndUpToDate() {
            PackageItem rulePackageItem1 = getRepo().createPackage("testGetFormat", "woot");
            assertNotNull(rulePackageItem1);
            assertEquals(PackageItem.PACKAGE_FORMAT, rulePackageItem1.getFormat());
            assertFalse(rulePackageItem1.isBinaryUpToDate());
            rulePackageItem1.updateBinaryUpToDate(true);
            assertTrue(rulePackageItem1.isBinaryUpToDate());
            rulePackageItem1.updateBinaryUpToDate(false);
            assertFalse(rulePackageItem1.isBinaryUpToDate());
    }

    public static void updateHeader(String h, PackageItem pkg) {
    	pkg.checkout();
    	AssetItem as = null;
    	if (pkg.containsAsset("drools")) {
    		as = pkg.loadAsset("drools");
    	} else {
    		as = pkg.addAsset("drools", "");
    	}
		as.updateContent(h);
		//as.checkin("");
    }

    public static String getHeader(PackageItem pkg) {
    	if (pkg.containsAsset("drools")) {
    		return pkg.loadAsset("drools").getContent();
    	} else {
    		return "";
    	}
    }

    public void testPackageCheckinConfig() {
        PackageItem item = getRepo().createPackage( "testPackageCheckinConfig", "description" );

        AssetItem rule = item.addAsset( "testPackageCheckinConfig", "w" );
        rule.checkin( "goo" );

        assertEquals(1, iteratorToList( item.getAssets() ).size());
        updateHeader( "la", item );
        item.checkin( "woot" );

        updateHeader( "we", item );
        item.checkin( "gah" );





//        PackageItem pre = (PackageItem) item.getPrecedingVersion();
//        assertNotNull(pre);
//        assertEquals("la", getHeader(pre));

        AssetItem rule_ = getRepo().loadAssetByUUID( rule.getUUID() );
        assertEquals(rule.getVersionNumber(), rule_.getVersionNumber());

        item = getRepo().loadPackage( "testPackageCheckinConfig");
        long v = item.getVersionNumber();
        item.updateCheckinComment( "x" );
        getRepo().save();

        assertEquals(v, item.getVersionNumber());
    }
	
    static class MockAssetItem extends AssetItem {
        private long version;

        MockAssetItem(long ver) {
            this.version = ver ;
        }

        public long getVersionNumber() {
            return this.version;
        }

        public boolean equals(Object in) {
            return in == this;
        }

        public String toString() {
            return Long.toString( this.version );
        }
    }
}