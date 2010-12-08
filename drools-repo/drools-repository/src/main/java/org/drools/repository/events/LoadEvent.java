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

import java.io.InputStream;

/**
 * This event handler is used to provide alternative asset content.
 * When the asset payload (content) is fetched, it will call this, and it will use its input stream as the source
 * of data rather then the JCR node.
 *
 * Use with care ! (it could slow things down).
 *
 * To install, create an instance of LoadEvent, make it available on the classpath and then set the system property
 * 'loadEventListener' with the value of the full name of the class. 
 *
 * @author Michael Neale
 */
public interface LoadEvent {

    public InputStream loadContent(AssetItem item);
}
