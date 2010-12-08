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

package org.drools.repository.events;

import junit.framework.TestCase;
import org.drools.repository.RulesRepository;
import org.drools.repository.PackageItem;
import org.drools.repository.AssetItem;
import org.drools.repository.RepositorySessionUtil;

import java.io.ByteArrayInputStream;

/**
 * @author Michael Neale
 */
public class StorageEventManagerTest extends TestCase {

    @Override
    protected void tearDown() throws Exception {
        StorageEventManager.le = null;
        StorageEventManager.se = null;
    }


    public void testLoadEvent() {
        System.setProperty("guvnor.loadEventListener", "org.drools.repository.events.MockLoadEvent");
        LoadEvent le = StorageEventManager.loadEvent();
        assertNotNull(le);
        assertTrue(le instanceof MockLoadEvent);

        System.setProperty("guvnor.loadEventListener", "");
        assertNull(StorageEventManager.loadEvent());


        StorageEventManager.le = le;
        assertNotNull(StorageEventManager.getLoadEvent());
        assertTrue(StorageEventManager.hasLoadEvent());

        StorageEventManager.le = null;
        assertFalse(StorageEventManager.hasLoadEvent());


    }


    public void testSaveEvent() {
        System.setProperty("guvnor.saveEventListener", "org.drools.repository.events.MockSaveEvent");
        SaveEvent le = StorageEventManager.saveEvent();
        assertNotNull(le);
        assertTrue(le instanceof MockSaveEvent);

        System.setProperty("guvnor.saveEventListener", "");
        assertNull(StorageEventManager.saveEvent());


        StorageEventManager.se = le;
        assertNotNull(StorageEventManager.getSaveEvent());
        assertTrue(StorageEventManager.hasSaveEvent());

        StorageEventManager.se = null;
        assertFalse(StorageEventManager.hasSaveEvent());

    }


    public void testAssetContentCallbacks() {

        StorageEventManager.le = null;
        StorageEventManager.se = null;

        RulesRepository repo = getRepo();
        PackageItem pkg = repo.loadDefaultPackage();
        AssetItem asset = pkg.addAsset("testAssetContentCallbacks", "");
        assertEquals(0, asset.getContentLength());
        asset.updateContent("boo");
        asset.checkin("");

        asset.updateContent("whee");
        StorageEventManager.le = new MockLoadEvent();
        StorageEventManager.se = new MockSaveEvent();

        asset.checkin("");
        assertTrue(((MockSaveEvent)StorageEventManager.se).checkinCalled);

        asset.getContent();
        assertTrue(((MockLoadEvent) StorageEventManager.le).loadCalled);

    }

    public void testCheckinListener() throws Exception {
        StorageEventManager.le = null;
        StorageEventManager.se = null;

        final AssetItem[] x = new AssetItem[1];

        CheckinEvent e = new CheckinEvent() {
            public void afterCheckin(AssetItem item) {
                x[0] = item;
            }
        };
        StorageEventManager.registerCheckinEvent(e);
        RulesRepository repo = getRepo();
        PackageItem pkg = repo.loadDefaultPackage();
        AssetItem asset = pkg.addAsset("testCheckinListener", "");
        assertEquals(0, asset.getContentLength());
        asset.updateContent("boo");
        asset.checkin("");

        assertSame(asset, x[0]);



    }


    private RulesRepository getRepo() {
        return RepositorySessionUtil.getRepository();
    }

}
