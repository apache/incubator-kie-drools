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
import org.drools.repository.PackageItem;

/**
 * This will be called as content is saved to the repository - you can hook in and also store content in an external store.
 * Content can be text or binary.
 *
 * To install an implementation of this, create an instance of SaveEvent, make it available on the classpath
 * and set the system property 'guvnor.saveEventListener' with the full name of the class. 
 *
 * @author Michael Neale
 */
public interface SaveEvent {

    /**
     * When the content of the asset changes, or some meta data. This will also be called when it is new.
     */
    public void onAssetCheckin(AssetItem item);

    /**
     * When it is hard deleted. A soft delete is just a checkin with the archive flag set.
     */
    public void onAssetDelete(AssetItem item);


    /**
     * Called once, when a package is created.
     */
    public void onPackageCreate(PackageItem item);

}
