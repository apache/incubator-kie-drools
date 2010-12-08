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

import org.drools.repository.AssetItem;
import org.drools.repository.VersionableItem;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

/**
 * This manages storage events, which may load/save from another location, or just notify on change etc.
 * @author Michael Neale
 */
public class StorageEventManager {


    static List<CheckinEvent> checkinEvents = new ArrayList<CheckinEvent>();
    static LoadEvent le = loadEvent();
    static SaveEvent se = saveEvent();

    static LoadEvent loadEvent() {
        String leClassName = System.getProperty("guvnor.loadEventListener", "");
        try {
            if (!leClassName.equals("")) {
                return (LoadEvent) Class.forName(leClassName).newInstance();
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("Unable to initialise the load event listener: " + leClassName);
            e.printStackTrace();
            return null;
        }
    }

    static SaveEvent saveEvent() {
        String seClassName = System.getProperty("guvnor.saveEventListener", "");
        try {
            if (!seClassName.equals("")) {
                return (SaveEvent) Class.forName(seClassName).newInstance();
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("Unable to initialise the save event listener: " + seClassName);
            e.printStackTrace();
            return null;
        }
    }

    public static boolean hasLoadEvent() {
        return le != null;
    }

    public static boolean hasSaveEvent() {
        return se != null;
    }

    public static LoadEvent getLoadEvent() {
        return le;
    }

    public static SaveEvent getSaveEvent() {
        return se;
    }

    public static void registerCheckinEvent(CheckinEvent ev) {
        checkinEvents.add(ev);
    }


    /**
     * Process any checkin event listeners.
     */
    public static void doCheckinEvents(VersionableItem item) {
        if (item instanceof AssetItem) {
            AssetItem asset = (AssetItem) item;
            for (CheckinEvent e : checkinEvents) {
                e.afterCheckin(asset);
            }
        }
    }
}
