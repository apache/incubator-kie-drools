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

package org.drools.runtime;

import org.drools.PropertiesConfiguration;
import org.drools.runtime.conf.KnowledgeSessionOptionsConfiguration;

/**
 * KnowledgeSessionConfiguration
 *
 * A class to store Session related configuration. It must be used at session instantiation time
 * or not used at all.
 * 
 * This class will automatically load default values from a number of places, accumulating properties from each location.
 * This list of locations, in given priority is:
 * System properties, home directory, working directory, META-INF/ of optionally provided classLoader
 * META-INF/ of Thread.currentThread().getContextClassLoader() and META-INF/ of  ClassLoader.getSystemClassLoader()
 * 
 * So if you want to set a default configuration value for all your new KnowledgeSession, you can simply set the property as
 * a System property.
 *
 * After the KnowledgeSession is created, it makes the configuration immutable and there is no way to make it
 * mutable again. This is to avoid inconsistent behaviour inside KnowledgeSession.
 *
 * 
 * 
 * drools.keepReference = <true|false>
 * drools.clockType = <pseudo|realtime>
 */
public interface KnowledgeSessionConfiguration
    extends
    PropertiesConfiguration,
    KnowledgeSessionOptionsConfiguration {

}
