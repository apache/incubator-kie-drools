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
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ArchiveItemTest extends RepositoryTestCase {

	@Test
    public void testFindArchivedAssets() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();

        repo.loadDefaultPackage().addAsset( "testFindArchivedAssets1",
                                            "X" );
        repo.loadDefaultPackage().addAsset( "testFindArchivedAssets2",
                                            "X" );
        repo.loadDefaultPackage().addAsset( "testFindArchivedAssets3",
                                            "X" );
        repo.loadDefaultPackage().addAsset( "testFindArchivedAssets4",
                                            "X" );

        AssetItem item = RepositorySessionUtil.getRepository().loadDefaultPackage().loadAsset( "testFindArchivedAssets1" );
        assertFalse( item.isArchived() );
        item.archiveItem( true );
        item.checkin( "archiving item 1" );
        item = RepositorySessionUtil.getRepository().loadDefaultPackage().loadAsset( "testFindArchivedAssets1" );
        assertTrue( item.isArchived() );

        item = RepositorySessionUtil.getRepository().loadDefaultPackage().loadAsset( "testFindArchivedAssets2" );
        assertFalse( item.isArchived() );
        item.archiveItem( true );
        item.checkin( "archiving item 1" );
        item = RepositorySessionUtil.getRepository().loadDefaultPackage().loadAsset( "testFindArchivedAssets2" );
        assertTrue( item.isArchived() );

        item = RepositorySessionUtil.getRepository().loadDefaultPackage().loadAsset( "testFindArchivedAssets3" );
        assertFalse( item.isArchived() );
        item.archiveItem( true );
        item.checkin( "archiving item 1" );
        item = RepositorySessionUtil.getRepository().loadDefaultPackage().loadAsset( "testFindArchivedAssets3" );
        assertTrue( item.isArchived() );
    }

	@Test
    public void testArchiveBooleanFlag() throws Exception {
        AssetItem item = RepositorySessionUtil.getRepository().loadDefaultPackage().addAsset( "testArchiveBooleanFlag",
                                                                                              "yeah" );

        assertFalse( item.isArchived() );
        item.archiveItem( true );
        assertFalse( item.getNode().hasProperty( AssetItem.CONTENT_PROPERTY_NAME ) );
        assertTrue( item.getNode().hasProperty( AssetItem.CONTENT_PROPERTY_ARCHIVE_FLAG ) );
        assertTrue( item.isArchived() );
        item.checkin( "lalalala" );
    }

	@Test
	public void testLoadArchivedAsset() throws Exception {
		AssetItem itemAdd = RepositorySessionUtil.getRepository().loadDefaultPackage().addAsset( "testArchiveBooleanFlag",
        "yeah" );
		itemAdd.archiveItem( true );
		
        AssetItem item = RepositorySessionUtil.getRepository().loadDefaultPackage().loadAsset( "testArchiveBooleanFlag" );
        System.out.println( item );
        assertTrue( item.isArchived() );
    }

	@Test
    public void testFindArchivedAssetsByCategory() {
        getRepo().loadCategory( "/" ).addCategory( "testFindRulesByCatArchive",
                                                   "yeah" );
        AssetItem as1 = getDefaultPackage().addAsset( "testFindRulesByCategoryArch",
                                                      "ya",
                                                      "testFindRulesByCatArchive",
                                                      "drl" );
        getDefaultPackage().addAsset( "testFindRulesByCategoryArch2",
                                      "ya",
                                      "testFindRulesByCatArchive",
                                      AssetItem.DEFAULT_CONTENT_FORMAT ).checkin( "version0" );

        as1.archiveItem( true );
        as1.checkin( "version0" );

        assertEquals( "drl",
                      as1.getFormat() );

        List rules = getRepo().findAssetsByCategory( "testFindRulesByCatArchive", 0, -1  ).assets;
        assertEquals( 1,
                      rules.size() );

        List rules1 = getRepo().findAssetsByCategory( "testFindRulesByCatArchive",
                                                      true, 0, -1  ).assets;
        assertEquals( 2,
                      rules1.size() );
    }

    List iteratorToList(Iterator it) {
        List list = new ArrayList();
        while ( it.hasNext() ) {
            list.add( it.next() );
        }
        return list;
    }

    @Test
    public void testLoadPackages() {
        RulesRepository repo = getRepo();
        PackageItem item = repo.createPackage( "testListPackagesblah",
                                               "lalalala" );

        assertNotNull( item.getCreator() );
        assertFalse( item.getCreator().equals( "" ) );

        List list = iteratorToList( repo.listPackages() );
        int prevSize = list.size();
        repo.createPackage( "testListPackages2blah",
                            "abc" );
        repo.loadPackage( "testListPackages2blah" ).archiveItem( true );
        repo.createPackage( "testListPackages2blah1",
                            "abc" );
        repo.createPackage( "testListPackages2blah3",
                            "abc" );
        repo.createPackage( "testListPackages2blah7",
                            "abc" );

        PackageIterator localIterator = (PackageIterator) repo.listPackages();

        list = iteratorToList( localIterator );

        assertEquals( prevSize + 3,
                      list.size() );

        localIterator = (PackageIterator) repo.listPackages();
        localIterator.setArchivedIterator( true );
        list = iteratorToList( localIterator );
        assertEquals( prevSize + 4,
                      list.size() );
    }

    @Test
    public void testFindRulesByNameArchived() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();

        repo.loadDefaultPackage().addAsset( "findRulesByNameArchived1",
                                            "X" );
        repo.loadDefaultPackage().addAsset( "findRulesByNameArchived2",
                                            "X" );
        repo.save();

        AssetItem item2 = RepositorySessionUtil.getRepository().loadDefaultPackage().loadAsset( "findRulesByNameArchived1" );
        assertFalse( item2.isArchived() );

        item2.archiveItem( true );

        // not archived until repository isn't save.
/*        assertEquals( 1,
        		repo.findAssetsByName( "findRulesByNameArchived1" ).getSize() );*/

        List list = iteratorToList( repo.findAssetsByName( "findRulesByNameArchived1" ) );
        assertEquals( 1,
                      list.size() );

        list = iteratorToList( repo.findAssetsByName( "findRulesByNameArchived1",
                                                      true ) );
        assertEquals( 1,
                      list.size() );

        list = iteratorToList( repo.findAssetsByName( "findRulesByNameArchived2" ) );
        assertEquals( 1,
                      list.size() );

        list = iteratorToList( repo.findAssetsByName( "findRulesByNameArchived%" ) );
        assertEquals( 2,
                      list.size() );

        repo.createPackageSnapshot( RulesRepository.DEFAULT_PACKAGE,
                                    "testFindRulesByName2" );
        repo.save();

        list = iteratorToList( repo.findAssetsByName( "findRulesByNameArchived2" ) );
        AssetItem item = (AssetItem) list.get( 0 );
        assertEquals( "findRulesByNameArchived2",
                      item.getName() );
        assertEquals( "X",
                      item.getDescription() );
        assertEquals( 1,
                      list.size() );

        list = iteratorToList( repo.findAssetsByName( "findRulesByNameArchived1",
                                                      true ) );
        assertEquals( 1,
                      list.size() );

        list = iteratorToList( repo.findAssetsByName( "findRulesByNameArchived1" ) );
        assertEquals( 0,
                      list.size() );

        // return just the un-archived item
        list = iteratorToList( repo.findAssetsByName( "findRulesByNameArchived%" ) );
        assertEquals( 1,
                      list.size() );

        // return all itens including archived
        list = iteratorToList( repo.findAssetsByName( "findRulesByNameArchived%",
                                                      true ) );
        assertEquals( 2,
                      list.size() );

    }

    @Test
    public void testlistbyformatArchived() {

    }

}
