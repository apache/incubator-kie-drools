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
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;

import org.drools.repository.RulesRepository.DateQuery;
import org.drools.repository.migration.MigrateDroolsPackage;

import junit.framework.TestCase;

public class RulesRepositoryTest extends TestCase {
	int running = 0;

    public void testDefaultPackage() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();

        Iterator it = repo.listPackages();
        boolean foundDefault = false;
        while ( it.hasNext() ) {
            PackageItem item = (PackageItem) it.next();
            if ( item.getName().equals( RulesRepository.DEFAULT_PACKAGE ) ) {
                foundDefault = true;
            }
        }
        assertTrue( foundDefault );

        PackageItem def = repo.loadDefaultPackage();
        assertNotNull( def );
        assertEquals( RulesRepository.DEFAULT_PACKAGE,
                      def.getName() );

        String userId = repo.getSession().getUserID();
        assertNotNull( userId );
        assertFalse( userId.equals( "" ) );

        MigrateDroolsPackage mig = new MigrateDroolsPackage();
        assertFalse( mig.needsMigration( repo ) );
        assertTrue( repo.initialized );

    }

    public void testCategoryRename() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();

        CategoryItem root = repo.loadCategory( "/" );
        root.addCategory( "testCatRename",
                          "" );
        repo.loadCategory( "testCatRename" ).addCategory( "testRename",
                                                          "" );

        repo.renameCategory( "testCatRename/testRename",
                             "testAnother" );

        CategoryItem cat = repo.loadCategory( "testCatRename/testAnother" );
        assertNotNull( cat );
        try {
            repo.loadCategory( "testCatRename/testRename" );
            fail( "should not exist." );
        } catch ( RulesRepositoryException e ) {
            assertNotNull( e.getMessage() );
        }

        PackageItem pkg = repo.createPackage( "testCategoryRename",
                                              "" );
        AssetItem asset = pkg.addAsset( "fooBar",
                                        "" );
        asset.addCategory( "testCatRename" );
        asset.addCategory( "testCatRename/testAnother" );
        asset.checkin( "" );

        cat = repo.loadCategory( "testCatRename/testAnother" );
        AssetItemPageResult as = repo.findAssetsByCategory( "testCatRename/testAnother",
                                                      0,
                                                      -1 );
        assertEquals( "fooBar",
                      ((AssetItem) as.assets.get( 0 )).getName() );

        repo.renameCategory( "testCatRename/testAnother",
                             "testYetAnother" );
        as = repo.findAssetsByCategory( "testCatRename/testYetAnother",
                                        0,
                                        -1 );
        assertEquals( "fooBar",
                      ((AssetItem) as.assets.get( 0 )).getName() );

    }

    public void testAddVersionARule() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        PackageItem pack = repo.createPackage( "testAddVersionARule",
                                               "description" );
        repo.save();

        AssetItem rule = pack.addAsset( "my rule",
                                        "foobar" );
        assertEquals( "my rule",
                      rule.getName() );

        rule.updateContent( "foo foo" );
        rule.checkin( "version0" );

        pack.addAsset( "other rule",
                       "description" );

        rule.updateContent( "foo bar" );
        rule.checkin( "version1" );

        PackageItem pack2 = repo.loadPackage( "testAddVersionARule" );

        Iterator it = pack2.getAssets();

        it.next();
        it.next();

        assertFalse( it.hasNext() );

        AssetItem prev = (AssetItem) rule.getPrecedingVersion();

        assertEquals( "foo bar",
                      rule.getContent() );
        assertEquals( "foo foo",
                      prev.getContent() );

    }

    public void testFindByState() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        PackageItem pkg = repo.createPackage( "testFindByStatePackage",
                                              "heheheh" );
        AssetItem asset1 = pkg.addAsset( "asset1",
                                         "" );
        AssetItem asset2 = pkg.addAsset( "asset2",
                                         "" );
        repo.createState( "testFindByState" );
        repo.save();
        asset1.updateState( "testFindByState" );
        asset2.updateState( "testFindByState" );
        asset1.checkin( "" );
        asset2.checkin( "" );

        AssetItemPageResult result = repo.findAssetsByState( "testFindByState",
                                                     true,
                                                     0,
                                                     -1 );
        assertEquals( 2,
                      result.assets.size() );

    }

    public void testFindRulesByName() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();

        repo.loadDefaultPackage().addAsset( "findRulesByNamex1",
                                            "X" );
        repo.loadDefaultPackage().addAsset( "findRulesByNamex2",
                                            "X" );
        repo.save();

        List list = iteratorToList( repo.findAssetsByName( "findRulesByNamex1" ) );
        assertEquals( 1,
                      list.size() );

        list = iteratorToList( repo.findAssetsByName( "findRulesByNamex2" ) );
        assertEquals( 1,
                      list.size() );

        list = iteratorToList( repo.findAssetsByName( "findRulesByNamex%" ) );
        assertEquals( 2,
                      list.size() );

        repo.createPackageSnapshot( RulesRepository.DEFAULT_PACKAGE,
                                    "testFindRulesByName" );
        repo.save();

        list = iteratorToList( repo.findAssetsByName( "findRulesByNamex2" ) );
        AssetItem item = (AssetItem) list.get( 0 );
        assertEquals( "findRulesByNamex2",
                      item.getName() );
        assertEquals( "X",
                      item.getDescription() );
        assertEquals( 1,
                      list.size() );

        list = iteratorToList( repo.findAssetsByName( "findRulesByNamex%" ) );
        assertEquals( 2,
                      list.size() );

    }

    public void testQueryText() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        PackageItem pkg = repo.createPackage( "testQueryTest",
                                              "" );
        AssetItem asset = pkg.addAsset( "asset1",
                                        "testQueryText1" );
        asset.updateSubject( "testQueryText42" );
        asset.checkin( "firstCheckintestQueryTest" );
        asset.updateFormat( "drl" );
        asset.checkin( "firstCheckintestQueryTest2" );
        pkg.addAsset( "asset2",
                      "testQueryText2" );
        repo.save();

        List<AssetItem> ls = iteratorToList( repo.queryFullText( "testQueryText*",
                                                                 false ) );
        assertEquals( 2,
                      ls.size() );

        ls = iteratorToList( repo.queryFullText( "firstCheckintestQueryTest2",
                                                 false ) );
        assertEquals( 1,
                      ls.size() );

        ls = iteratorToList( repo.queryFullText( "firstCheckintestQueryTest",
                                                 false ) );
        assertEquals( 0,
                      ls.size() );

        ls = iteratorToList( repo.queryFullText( "testQueryText*",
                                                 false ) );
        assertEquals( 2,
                      ls.size() );

        asset.archiveItem( true );
        asset.checkin( "" );

        ls = iteratorToList( repo.queryFullText( "testQueryText*",
                                                 false ) );
        assertEquals( 1,
                      ls.size() );

        ls = iteratorToList( repo.queryFullText( "testQueryText*",
                                                 true ) );
        assertEquals( 2,
                      ls.size() );

    }

    public void testQuery() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();

        AssetItem asset = repo.loadDefaultPackage().addAsset( "testQuery",
                                                              "wanklerotaryengine1cc" );

        //asset.updateBinaryContentAttachment(new ByteArrayInputStream("testingSearchWankle".getBytes()));
        asset.updateContent( "testingSearchWankle" );
        asset.updateSubject( "testQueryXXX42" );
        asset.checkin( "" );

        Map<String, String[]> q = new HashMap<String, String[]>();
        q.put( "drools:subject",
               new String[]{"testQueryXXX42"} );

        AssetItemIterator asit = repo.query( q,
                                             false,
                                             null );
        List<AssetItem> results = iteratorToList( asit );
        assertEquals( 1,
                      results.size() );
        AssetItem as = results.get( 0 );
        assertEquals( "testQuery",
                      as.getName() );

        asset.updateExternalSource( "database" );
        asset.checkin( "" );

        q = new HashMap<String, String[]>();
        q.put( "drools:subject",
               new String[]{"testQueryXXX42"} );
        q.put( AssetItem.SOURCE_PROPERTY_NAME,
               new String[]{"database"} );
        results = iteratorToList( repo.query( q,
                                              true,
                                              null ) );
        assertEquals( 1,
                      results.size() );
        as = results.get( 0 );
        assertEquals( "testQuery",
                      as.getName() );

        q = new HashMap<String, String[]>();
        q.put( "drools:subject",
               new String[]{"testQueryXXX42", "wankle"} );
        q.put( AssetItem.SOURCE_PROPERTY_NAME,
               new String[]{"database", "wankle"} );
        results = iteratorToList( repo.query( q,
                                              false,
                                              null ) );
        assertEquals( 1,
                      results.size() );
        as = results.get( 0 );
        assertEquals( "testQuery",
                      as.getName() );

        q = new HashMap<String, String[]>();
        q.put( "drools:subject",
               null );
        q.put( "cruddy",
               new String[0] );
        q.put( AssetItem.SOURCE_PROPERTY_NAME,
               new String[]{"database", "wankle"} );
        results = iteratorToList( repo.query( q,
                                              false,
                                              null ) );
        assertEquals( 1,
                      results.size() );

        //now dates
        q = new HashMap<String, String[]>();
        q.put( "drools:subject",
               new String[]{"testQueryXXX42", "wankle"} );
        q.put( AssetItem.SOURCE_PROPERTY_NAME,
               new String[]{"database", "wankle"} );
        results = iteratorToList( repo.query( q,
                                              false,
                                              new DateQuery[]{new DateQuery( "jcr:created",
                                                                             "1974-07-10T00:00:00.000-05:00",
                                                                             "3074-07-10T00:00:00.000-05:00" )} ) );
        assertEquals( 1,
                      results.size() );
        as = results.get( 0 );
        assertEquals( "testQuery",
                      as.getName() );

        q = new HashMap<String, String[]>();
        q.put( "drools:subject",
               new String[]{"testQueryXXX42", "wankle"} );
        q.put( AssetItem.SOURCE_PROPERTY_NAME,
               new String[]{"database", "wankle"} );
        results = iteratorToList( repo.query( q,
                                              false,
                                              new DateQuery[]{new DateQuery( "jcr:created",
                                                                             "1974-07-10T00:00:00.000-05:00",
                                                                             null )} ) );
        assertEquals( 1,
                      results.size() );
        as = results.get( 0 );
        assertEquals( "testQuery",
                      as.getName() );

        q = new HashMap<String, String[]>();
        q.put( "drools:subject",
               new String[]{"testQueryXXX42", "wankle"} );
        q.put( AssetItem.SOURCE_PROPERTY_NAME,
               new String[]{"database", "wankle"} );
        results = iteratorToList( repo.query( q,
                                              false,
                                              new DateQuery[]{new DateQuery( "jcr:created",
                                                                             null,
                                                                             "3074-07-10T00:00:00.000-05:00" )} ) );
        assertEquals( 1,
                      results.size() );
        as = results.get( 0 );
        assertEquals( "testQuery",
                      as.getName() );

        //should return nothing:
        q = new HashMap<String, String[]>();
        q.put( "drools:subject",
               new String[]{"testQueryXXX42", "wankle"} );
        q.put( AssetItem.SOURCE_PROPERTY_NAME,
               new String[]{"database", "wankle"} );
        results = iteratorToList( repo.query( q,
                                              false,
                                              new DateQuery[]{new DateQuery( "jcr:created",
                                                                             "3074-07-10T00:00:00.000-05:00",
                                                                             null )} ) );
        assertEquals( 0,
                      results.size() );

        q = new HashMap<String, String[]>();
        q.put( "drools:subject",
               new String[]{"testQueryXXX42", "wankle"} );
        q.put( AssetItem.SOURCE_PROPERTY_NAME,
               new String[]{"database", "wankle"} );
        results = iteratorToList( repo.query( q,
                                              false,
                                              new DateQuery[]{new DateQuery( "jcr:created",
                                                                             null,
                                                                             "1974-07-10T00:00:00.000-05:00" )} ) );
        assertEquals( 0,
                      results.size() );

    }

    public void testLoadRuleByUUIDWithConcurrentSessions() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();

        PackageItem rulePackageItem = repo.loadDefaultPackage();
        AssetItem rule = rulePackageItem.addAsset( "testLoadRuleByUUID",
                                                   "this is a description" );

        repo.save();

        String uuid = rule.getNode().getIdentifier();

        AssetItem loaded = repo.loadAssetByUUID( uuid );
        assertNotNull( loaded );
        assertEquals( "testLoadRuleByUUID",
                      loaded.getName() );
        assertEquals( "this is a description",
                      loaded.getDescription() );

        long oldVersionNumber = loaded.getVersionNumber();

        loaded.updateContent( "xxx" );
        loaded.checkin( "woo" );

        AssetItem reload = repo.loadAssetByUUID( uuid );
        assertEquals( "testLoadRuleByUUID",
                      reload.getName() );
        assertEquals( "xxx",
                      reload.getContent() );
        System.out.println( reload.getVersionNumber() );
        System.out.println( loaded.getVersionNumber() );
        assertFalse( reload.getVersionNumber() == oldVersionNumber );

        // try loading rule package that was not created
        try {
            repo.loadAssetByUUID( "01010101-0101-0101-0101-010101010101" );
            fail( "Exception not thrown loading rule package that was not created." );
        } catch ( RulesRepositoryException e ) {
            // that is OK!
            assertNotNull( e.getMessage() );
        }

        //now test concurrent session access...

        AssetItem asset1 = repo.loadDefaultPackage().addAsset( "testMultiSession",
                                                               "description" );
        asset1.updateContent( "yeah" );
        asset1.checkin( "boo" );
        uuid = asset1.getUUID();
        asset1.updateState( "Draft" );
        repo.save();

        Session s2 = repo.getSession().getRepository().login( new SimpleCredentials( "fdd",
                                                                                     "password".toCharArray() ) );

        RulesRepository repo2 = new RulesRepository( s2 );

        AssetItem asset2 = repo2.loadAssetByUUID( uuid );
        asset2.updateContent( "yeah 42" );
        asset2.checkin( "yeah" );

        asset1 = repo.loadAssetByUUID( uuid );
        assertEquals( "yeah 42",
                      asset1.getContent() );
        asset1.updateContent( "yeah 43" );
        asset1.checkin( "la" );

        asset2 = repo2.loadAssetByUUID( uuid );
        assertEquals( "yeah 43",
                      asset2.getContent() );
    }

    public void testAddRuleCalendarWithDates() {
        RulesRepository rulesRepository = RepositorySessionUtil.getRepository();

        Calendar effectiveDate = Calendar.getInstance();
        Calendar expiredDate = Calendar.getInstance();
        expiredDate.setTimeInMillis( effectiveDate.getTimeInMillis() + (1000 * 60 * 60 * 24) );
        AssetItem ruleItem1 = rulesRepository.loadDefaultPackage().addAsset( "testAddRuleCalendarCalendar",
                                                                             "desc" );
        ruleItem1.updateDateEffective( effectiveDate );
        ruleItem1.updateDateExpired( expiredDate );

        assertNotNull( ruleItem1 );
        assertNotNull( ruleItem1.getNode() );
        assertEquals( effectiveDate,
                      ruleItem1.getDateEffective() );
        assertEquals( expiredDate,
                      ruleItem1.getDateExpired() );

        ruleItem1.checkin( "ho " );
    }

    public void testGetState() {
        RulesRepository rulesRepository = RepositorySessionUtil.getRepository();

        StateItem state0 = rulesRepository.createState( "testGetState" );
        assertNotNull( state0 );
        assertEquals( "testGetState",
                      state0.getName() );
        StateItem stateItem1 = rulesRepository.getState( "testGetState" );
        assertNotNull( stateItem1 );
        assertEquals( "testGetState",
                      stateItem1.getName() );

        StateItem stateItem2 = rulesRepository.getState( "testGetState" );
        assertNotNull( stateItem2 );
        assertEquals( "testGetState",
                      stateItem2.getName() );
        assertEquals( stateItem1,
                      stateItem2 );
    }

    public void testGetTag() {
        RulesRepository rulesRepository = RepositorySessionUtil.getRepository();

        CategoryItem root = rulesRepository.loadCategory( "/" );
        CategoryItem tagItem1 = root.addCategory( "testGetTag",
                                                  "ho" );
        assertNotNull( tagItem1 );
        assertEquals( "testGetTag",
                      tagItem1.getName() );
        assertEquals( "testGetTag",
                      tagItem1.getFullPath() );

        CategoryItem tagItem2 = rulesRepository.loadCategory( "testGetTag" );
        assertNotNull( tagItem2 );
        assertEquals( "testGetTag",
                      tagItem2.getName() );
        assertEquals( tagItem1,
                      tagItem2 );

        //now test getting a tag down in the tag hierarchy
        CategoryItem tagItem3 = tagItem2.addCategory( "TestChildTag1",
                                                      "ka" );
        assertNotNull( tagItem3 );
        assertEquals( "TestChildTag1",
                      tagItem3.getName() );
        assertEquals( "testGetTag/TestChildTag1",
                      tagItem3.getFullPath() );
    }

    public void testListPackages() {
        RulesRepository rulesRepository = RepositorySessionUtil.getRepository();
        rulesRepository.createPackage( "testListPackages",
                                       "desc" );

        assertTrue( rulesRepository.containsPackage( "testListPackages" ) );
        assertFalse( rulesRepository.containsPackage( "XXXXXXX" ) );

        Iterator it = rulesRepository.listPackages();
        assertTrue( it.hasNext() );

        boolean found = false;
        //listPackages() should not return the global area even though the global area is a package.
        boolean foundGlobalArea = false;
        while ( it.hasNext() ) {
            PackageItem element = (PackageItem) it.next();
            if ( element.getName().equals( "testListPackages" ) ) {
                found = true;
            }
            
            if ( element.getName().equals(RulesRepository.RULE_GLOBAL_AREA) ) {
            	foundGlobalArea = true;
            }
         }
        assertTrue( found );
        assertFalse( foundGlobalArea );
    }

    public void testFindAssetsByState() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        repo.loadCategory( "/" ).addCategory( "testFindAssetsByStateCat",
                                              "X" );

        PackageItem pkg = repo.createPackage( "testFindAssetsByStatePac",
                                              "" );
        pkg.addAsset( "testCat1",
                      "x",
                      "/testFindAssetsByStateCat",
                      "drl" );
        pkg.addAsset( "testCat2",
                      "x",
                      "/testFindAssetsByStateCat",
                      "drl" );

        repo.save();

        AssetItemPageResult apl = repo.findAssetsByState( "Draft",
                                                    false,
                                                    0,
                                                    -1,
                                                    new RepositoryFilter() {
                                                        public boolean accept(Object artifact,
                                                                              String action) {
                                                            if ( !(artifact instanceof AssetItem) ) return false;

                                                            if ( ((AssetItem) artifact).getName().equalsIgnoreCase( "testCat1" ) ) {
                                                                return true;
                                                            } else {
                                                                return false;
                                                            }
                                                        }
                                                    } );

        assertEquals( 1,
                      apl.assets.size() );
        assertEquals( "testCat1",
                      ((AssetItem) apl.assets.get( 0 )).getName() );
    }

    public void testFindAssetsByCategory() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        repo.loadCategory( "/" ).addCategory( "testFindAssetsByCategoryUsingFilterCat",
                                              "X" );

        PackageItem pkg = repo.createPackage( "testFindAssetsByCategoryUsingFilterPack",
                                              "" );
        pkg.addAsset( "testCat1",
                      "x",
                      "/testFindAssetsByCategoryUsingFilterCat",
                      "drl" );
        pkg.addAsset( "testCat2",
                      "x",
                      "/testFindAssetsByCategoryUsingFilterCat",
                      "drl" );

        repo.save();

        List items = repo.findAssetsByCategory( "/testFindAssetsByCategoryUsingFilterCat",
                                                0,
                                                -1 ).assets;
        assertEquals( 2,
                      items.size() );

        AssetItemPageResult apl = repo.findAssetsByCategory( "/testFindAssetsByCategoryUsingFilterCat",
                                                       false,
                                                       0,
                                                       -1,
                                                       new RepositoryFilter() {
                                                           public boolean accept(Object artifact,
                                                                                 String action) {
                                                               if ( !(artifact instanceof AssetItem) ) return false;

                                                               if ( ((AssetItem) artifact).getName().equalsIgnoreCase( "testCat1" ) ) {
                                                                   return true;
                                                               } else {
                                                                   return false;
                                                               }
                                                           }
                                                       } );

        assertEquals( 1,
                      apl.assets.size() );
        assertEquals( "testCat1",
                      ((AssetItem) apl.assets.get( 0 )).getName() );

        pkg.addAsset( "testCat3",
                      "x",
                      "/testFindAssetsByCategoryUsingFilterCat",
                      "drl" );
        pkg.addAsset( "testCat4",
                      "x",
                      "/testFindAssetsByCategoryUsingFilterCat",
                      "drl" );
        pkg.addAsset( "testCat5",
                      "x",
                      "/testFindAssetsByCategoryUsingFilterCat",
                      "drl" );
        pkg.addAsset( "testCat6",
                      "x",
                      "/testFindAssetsByCategoryUsingFilterCat",
                      "drl" );
        pkg.addAsset( "testCat7",
                      "x",
                      "/testFindAssetsByCategoryUsingFilterCat",
                      "drl" );
        pkg.addAsset( "testCat8",
                      "x",
                      "/testFindAssetsByCategoryUsingFilterCat",
                      "drl" );

        pkg.loadAsset( "testCat1" ).archiveItem( true ).checkin( "" );
        pkg.loadAsset( "testCat2" ).archiveItem( true ).checkin( "" );
        pkg.loadAsset( "testCat3" ).archiveItem( true ).checkin( "" );
        pkg.loadAsset( "testCat4" ).archiveItem( true ).checkin( "" );

        //        apl = repo.findAssetsByCategory( "/testFindAssetsByCategoryUsingFilterCat", 0, 2 );
        //        assertEquals(2, apl.assets.size());
        //        assertTrue(apl.hasNext);
        //
        //        assertEquals(5, apl.currentPosition);
        //        //assertEquals("testCat5", apl.assets.get(0).getName());
        //
        //        apl = repo.findAssetsByCategory( "/testFindAssetsByCategoryUsingFilterCat", 7, 2 );
        //        assertEquals(2, apl.assets.size());
        //        assertFalse(apl.hasNext);
        //        //assertEquals("testCat7", apl.assets.get(0).getName());

        repo.save();

        apl = repo.findAssetsByCategory( "/testFindAssetsByCategoryUsingFilterCat",
                                         0,
                                         -1 );

        assertEquals( 4,
                      apl.assets.size() );
        List<String> names = new ArrayList<String>();

        for ( AssetItem as : apl.assets ) {
            if ( names.contains( as.getName() ) ) {
                fail( "dupe returned." );
            }
            names.add( as.getName() );
        }

        names = new ArrayList<String>();

        boolean hasNext = true;
        int skip = 0;
        while ( hasNext ) {
            apl = repo.findAssetsByCategory( "/testFindAssetsByCategoryUsingFilterCat",
                                             skip,
                                             2 );
            for ( AssetItem as : apl.assets ) {
                if ( names.contains( as.getName() ) ) {
                    fail( "dupe returned" );
                }
                names.add( as.getName() );
            }
            //we add the num of results returned, and sub 2 to work out where to start next
            skip = (int) (apl.currentPosition + apl.assets.size() - 2);
            hasNext = apl.hasNext;
        }
        assertEquals( 4,
                      names.size() );
        assertTrue( names.contains( "testCat5" ) );
        assertTrue( names.contains( "testCat6" ) );
        assertTrue( names.contains( "testCat7" ) );
        assertTrue( names.contains( "testCat8" ) );

    }

    public void testFunnyOrdering() throws Exception {

    }

    /**
     * Here we are testing to make sure that category links don't pick up stuff in snapshots area.
     */
    public void testCategoriesAndSnapshots() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        repo.loadCategory( "/" ).addCategory( "testCategoriesAndSnapshots",
                                              "X" );

        PackageItem pkg = repo.createPackage( "testCategoriesAndSnapshots",
                                              "" );
        pkg.addAsset( "testCat1",
                      "x",
                      "/testCategoriesAndSnapshots",
                      "drl" );
        pkg.addAsset( "testCat2",
                      "x",
                      "/testCategoriesAndSnapshots",
                      "drl" );
        repo.save();

        List items = repo.findAssetsByCategory( "/testCategoriesAndSnapshots",
                                                0,
                                                -1 ).assets;
        assertEquals( 2,
                      items.size() );

        repo.createPackageSnapshot( "testCategoriesAndSnapshots",
                                    "SNAP 1" );
        items = repo.findAssetsByCategory( "testCategoriesAndSnapshots",
                                           0,
                                           -1 ).assets;
        assertEquals( 2,
                      items.size() );

        assertTrue( repo.containsSnapshot( "testCategoriesAndSnapshots",
                                           "SNAP 1" ) );
        assertFalse( repo.containsSnapshot( "testCategoriesAndSnapshots",
                                            "SNAP XXXX" ) );

        assertFalse( repo.containsSnapshot( "gooberWhhewasssllllelelelelele",
                                            "SNAP" ) );

    }

    public void testMoveRulePackage() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        PackageItem pkg = repo.createPackage( "testMove",
                                              "description" );
        AssetItem r = pkg.addAsset( "testMove",
                                    "description" );
        r.checkin( "version0" );
        String uuid = r.getUUID();
        assertEquals( "testMove",
                      r.getPackageName() );

        repo.save();

        assertEquals( 1,
                      iteratorToList( pkg.getAssets() ).size() );

        repo.createPackage( "testMove2",
                            "description" );
        repo.moveRuleItemPackage( "testMove2",
                                  r.node.getIdentifier(),
                                  "explanation" );

        pkg = repo.loadPackage( "testMove" );
        assertEquals( 0,
                      iteratorToList( pkg.getAssets() ).size() );

        pkg = repo.loadPackage( "testMove2" );
        assertEquals( 1,
                      iteratorToList( pkg.getAssets() ).size() );

        r = (AssetItem) pkg.getAssets().next();
        assertEquals( "testMove",
                      r.getName() );
        assertEquals( "testMove2",
                      r.getPackageName() );
        assertEquals( "explanation",
                      r.getCheckinComment() );

        AssetItem p = (AssetItem) r.getPrecedingVersion();
        assertEquals( "testMove",
                      p.getPackageName() );
        assertEquals( "version0",
                      p.getCheckinComment() );
        assertEquals( uuid,
                      r.getUUID() );
    }

    public void testCopyAsset() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        repo.createPackage( "testCopyAsset",
                            "asset" );
        AssetItem item = repo.loadDefaultPackage().addAsset( "testCopyAssetSource",
                                                             "desc" );
        item.updateContent( "la" );
        item.checkin( "" );
        item.updateDescription( "mmm" );
        item.checkin( "again" );
        assertEquals( 2,
                      item.getVersionNumber() );

        String uuid = repo.copyAsset( item.getUUID(),
                                      "testCopyAsset",
                                      "testCopyAssetDestination" );
        AssetItem dest = repo.loadAssetByUUID( uuid );
        assertEquals( dest.getName(),
                      dest.getTitle() );
        assertEquals( "la",
                      dest.getContent() );
        assertEquals( "testCopyAsset",
                      dest.getPackageName() );
        assertFalse( uuid.equals( item.getUUID() ) );
        assertEquals( 1,
                      dest.getVersionNumber() );
    }

    public void testRenameAsset() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        repo.createPackage( "testRenameAsset",
                            "asset" );
        AssetItem item = repo.loadPackage( "testRenameAsset" ).addAsset( "testRenameAssetSource",
                                                                         "desc" );
        item.updateContent( "la" );
        item.checkin( "" );

        String uuid = repo.renameAsset( item.getUUID(),
                                        "testRename2" );
        item = repo.loadAssetByUUID( uuid );
        assertEquals( "testRename2",
                      item.getName() );
        assertEquals( "testRename2",
                      item.getTitle() );

        List assets = iteratorToList( repo.loadPackage( "testRenameAsset" ).getAssets() );
        assertEquals( 1,
                      assets.size() );
        item = (AssetItem) assets.get( 0 );
        assertEquals( "testRename2",
                      item.getName() );
        assertEquals( "la",
                      item.getContent() );

    }

    public void testRenamePackage() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        PackageItem original = repo.createPackage( "testRenamePackage",
                                                   "asset" );
        List packagesOriginal = iteratorToList( repo.listPackages() );
        AssetItem item = repo.loadPackage( "testRenamePackage" ).addAsset( "testRenameAssetSource",
                                                                           "desc" );
        item.updateContent( "la" );
        item.checkin( "" );

        String uuid = repo.renamePackage( original.getUUID(),
                                          "testRenamePackage2" );

        PackageItem pkg = repo.loadPackageByUUID( uuid );
        assertEquals( "testRenamePackage2",
                      pkg.getName() );

        List assets = iteratorToList( repo.loadPackage( "testRenamePackage2" ).getAssets() );
        assertEquals( 1,
                      assets.size() );
        item = (AssetItem) assets.get( 0 );
        assertEquals( "testRenameAssetSource",
                      item.getName() );
        assertEquals( "la",
                      item.getContent() );
        assertEquals( "testRenamePackage2",
                      item.getPackageName() );

        List packageFinal = iteratorToList( repo.listPackages() );
        assertEquals( packagesOriginal.size(),
                      packageFinal.size() );

    }

    public void testCopyPackage() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        PackageItem source = repo.createPackage( "testCopyPackage",
                                                 "asset" );
        AssetItem item = source.addAsset( "testCopyPackage",
                                          "desc" );
        item.updateContent( "la" );
        item.checkin( "" );
        repo.save();

        repo.copyPackage( "testCopyPackage",
                          "testCopyPackage2" );
        PackageItem dest = repo.loadPackage( "testCopyPackage2" );
        assertNotNull( dest );
        assertFalse( source.getUUID().equals( dest.getUUID() ) );

        assertEquals( 1,
                      iteratorToList( dest.getAssets() ).size() );
        AssetItem item2 = (AssetItem) dest.getAssets().next();

        assertEquals( "testCopyPackage",
                      item.getPackageName() );
        assertEquals( "testCopyPackage2",
                      item2.getPackageName() );

        item2.updateContent( "goober choo" );
        item2.checkin( "yeah" );

        assertEquals( "la",
                      item.getContent() );

        try {
            repo.copyPackage( "testCopyPackage",
                              "testCopyPackage2" );
            fail( "should not be able to copy when existing." );

        } catch ( RulesRepositoryException e ) {
            assertNotNull( e.getMessage() );
        }

    }

    public void testListStates() {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        StateItem[] items = repo.listStates();
        assertTrue( items.length > 0 );

        repo.createState( "testListStates" );

        StateItem[] items2 = repo.listStates();
        assertEquals( items.length + 1,
                      items2.length );
    }

    public void testRenameState() {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        StateItem[] items = repo.listStates();
        assertTrue( items.length > 0 );

        final String oldName = "stateThatHasALongNameAndWillBeRenamed";
        repo.createState( oldName );

        StateItem[] items2 = repo.listStates();
        assertEquals( items.length + 1,
                      items2.length );

        final String newName = "stateThatHasALongNameAndWillBeRenamedNameAfterTheRenaming";
        repo.renameState( oldName,
                          newName );

        StateItem[] items3 = repo.listStates();
        assertEquals( items2.length,
                      items3.length );
        try {
            repo.loadState( oldName );
            fail( "Should never be here. Old name is still used." );
        } catch ( RulesRepositoryException e ) {
            // Works
        }

        assertNotNull( repo.loadState( newName ) );
    }

    public void testRemoveState() {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        StateItem[] items = repo.listStates();
        assertTrue( items.length > 0 );

        final String name = "stateThatHasALongNameAndWillBeRenamed";
        repo.createState( name );

        StateItem[] items2 = repo.listStates();
        assertEquals( items.length + 1,
                      items2.length );

        repo.loadState( name ).remove();
        repo.save();

        StateItem[] items3 = repo.listStates();
        assertEquals( items2.length -1,
                      items3.length );
        try {
            repo.loadState( name );
            fail( "Should never be here. Removed state still exists." );
        } catch ( RulesRepositoryException e ) {
            // Works
        }

    }

    public void xtestImportExport() {
		RulesRepository repo = RepositorySessionUtil.getRepository();
		byte[] repository_unitest;
		byte[] repository_backup;

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		repo.exportRulesRepositoryToStream(bout);
		repository_backup = bout.toByteArray();
		assertNotNull(repository_backup);

		repo.createPackage("testImportExport", "nodescription");
		bout = new ByteArrayOutputStream();
		repo.exportRulesRepositoryToStream(bout);

		repository_unitest = bout.toByteArray();

		repo.importRulesRepositoryFromStream(new ByteArrayInputStream(
				repository_backup));
		assertFalse(repo.containsPackage("testImportExport"));

		repo.importRulesRepositoryFromStream(new ByteArrayInputStream(
				repository_unitest));

		assertTrue(repo.containsPackage("testImportExport"));

		repo.importRepository(new ByteArrayInputStream(repository_unitest));
		assertTrue(repo.containsPackage("testImportExport"));
	}

	public void testShareableNodes() throws Exception {
		RulesRepository repo = RepositorySessionUtil.getRepository();
		AssetItem item = repo.loadDefaultPackage().addAsset("testShareableNodeOriginal", "desc");
		item.updateContent("la");
		item.getNode().addMixin("mix:shareable");
		PackageItem source = repo.createPackage("testShareableNodesPackage", "desc");
		repo.save();

		source.checkout();
		
		Session session = repo.getSession();
		Workspace workspace = session.getWorkspace();
		String path = "/drools:repository/drools:package_area/testShareableNodesPackage/assets/testShareableNodeShared";
		workspace.clone(workspace.getName(), item.getNode().getPath(), path, false);		
		repo.save();
		
		AssetItem originalItem = repo.loadDefaultPackage().loadAsset("testShareableNodeOriginal");
		AssetItem sharedItem = repo.loadPackage("testShareableNodesPackage").loadAsset("testShareableNodeShared");
		
	    assertTrue( originalItem.getContent().equals("la"));
	    assertTrue( sharedItem.getContent().equals("la"));
	    
	    originalItem.remove();
	}
	
	public void testShareableNodesWithQuery() throws Exception {
		RulesRepository repo = RepositorySessionUtil.getRepository();
		AssetItem item = repo.loadGlobalArea().addAsset("testShareableNodesWithQueryOriginal", "desc");
		item.updateFormat("xyz");
		item.getNode().addMixin("mix:shareable");
		PackageItem source = repo.createPackage("testShareableNodesWithQueryPackage", "desc");
		repo.save();

	    
        AssetItemIterator it = repo.loadGlobalArea().queryAssets( "drools:format='xyz'" );
        List list = iteratorToList( it );
        assertEquals(1, list.size());
        assertTrue(list.get( 0 ) instanceof AssetItem);
        
		source.checkout();
		
		Session session = repo.getSession();
		Workspace workspace = session.getWorkspace();
		String path = "/drools:repository/drools:package_area/testShareableNodesWithQueryPackage/assets/testShareableNodesWithQueryShared";
		workspace.clone(workspace.getName(), item.getNode().getPath(), path, false);		
		repo.save();
		
		AssetItem originalItem = repo.loadGlobalArea().loadAsset("testShareableNodesWithQueryOriginal");
		AssetItem sharedItem = repo.loadPackage("testShareableNodesWithQueryPackage").loadAsset("testShareableNodesWithQueryShared");
		
	    assertTrue( originalItem.getFormat().equals("xyz"));
	    assertTrue( sharedItem.getFormat().equals("xyz"));
	    
        it = repo.loadGlobalArea().queryAssets( "drools:format='xyz'" );
        list = iteratorToList( it );
        assertEquals(1, list.size());
        assertTrue(list.get( 0 ) instanceof AssetItem);
	}	
	
	public void xtestImportExportWithShareableNodes() throws Exception {
		RulesRepository repo = RepositorySessionUtil.getRepository();
		AssetItem item = repo.loadDefaultPackage().addAsset("testShareableNodeOriginal", "desc");
		item.updateContent("la");
		item.getNode().addMixin("mix:shareable");
		PackageItem source = repo.createPackage("testShareableNodesPackage", "desc");
		repo.save();

		source.checkout();
		
		Session session = repo.getSession();
		Workspace workspace = session.getWorkspace();
		String path = "/drools:repository/drools:package_area/testShareableNodesPackage/assets/testShareableNodeShared";
		workspace.clone(workspace.getName(), item.getNode().getPath(), path, false);		
		repo.save();
		
		byte[] repository_backup;

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		repo.exportRulesRepositoryToStream(bout);
		repository_backup = bout.toByteArray();
		assertNotNull(repository_backup);

		repo.importRulesRepositoryFromStream(new ByteArrayInputStream(
				repository_backup));
		assertTrue(repo.containsPackage("testShareableNodesPackage"));
		assertTrue(repo.loadPackage("testShareableNodesPackage").containsAsset("testShareableNodeOriginal"));
	}
	
	//In this test case we expect an ItemExistException from the second thread,
        //other than ending up with two packages with same name.
	//https://jira.jboss.org/jira/browse/GUVNOR-346
    public void testConcurrentCopyPackage() throws Exception {
       // set up testing data               
       RulesRepository repo = RepositorySessionUtil.getMultiThreadedRepository();
       PackageItem source = repo.createPackage("testConcurrentCopyPackage",
               "asset");
       AssetItem item = source.addAsset("testCopyPackage", "desc");
       item.updateContent("la");
       item.checkin("");
       repo.save();

       int NUM_ITERATIONS = 40;
       int NUM_SESSIONS = 2;
              for (int n = 0; n < NUM_ITERATIONS; n++) {
           Node folderNode = repo.getAreaNode(RulesRepository.RULE_PACKAGE_AREA);
                      // cleanup
           while (folderNode.hasNode("testConcurrentCopyPackage2")) {
               folderNode.getNode("testConcurrentCopyPackage2").remove();
               repo.save();
           }

           Thread[] threads = new Thread[NUM_SESSIONS];
           for (int i = 0; i < threads.length; i++) {
               String id = "session#" + i;
               ConcurrentCopySession ts = new ConcurrentCopySession(id);
               Thread t = new Thread(ts);
               t.setName(id);
               t.start();
               threads[i] = t;
           }
           for (int i = 0; i < threads.length; i++) {
               threads[i].join();
           }

           //Node folderNode = repo.getAreaNode(RulesRepository.RULE_PACKAGE_AREA);
           NodeIterator results = folderNode.getNodes("testConcurrentCopyPackage2");
           assertEquals(1, results.getSize());
       }        }

      class ConcurrentCopySession implements Runnable {
       String identity;
       Random r;
       RulesRepository localRepo;
              ConcurrentCopySession(String identity) {
           this.identity = identity;
           r = new Random();
           localRepo = RepositorySessionUtil.getMultiThreadedRepository();
       }

       private void randomSleep() {
           long l = r.nextInt(90) + 20;
           try {
               Thread.sleep(l);
           } catch (InterruptedException ie) {
           }
       }

       public void run() {
           try {
               //This returns different repository instances for different threads
               localRepo.copyPackage("testConcurrentCopyPackage",
                       "testConcurrentCopyPackage2");
               PackageItem dest = localRepo
                       .loadPackage("testConcurrentCopyPackage2");
               assertNotNull(dest);
               randomSleep();                       } catch (RulesRepositoryException rre) {
               //expected
           } finally {
           }
       }
   }

    private static boolean deleteDir(File dir) {

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }


    public static <T> List<T> iteratorToList(Iterator<T> it) {
        List<T> list = new ArrayList<T>();
        while ( it.hasNext() ) {
            list.add( it.next() );
        }
        return list;
    }
}
