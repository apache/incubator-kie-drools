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
 * An object of this class stores a configuration for a Session. It must be used at session instantiation time
 * or not used at all.
 * <p>
 * This class will automatically load default values from a number of places, accumulating properties from each location.
 * This list of locations, in decreasing priority, is:
 * System properties, home directory, working directory, META-INF/ of an optionally provided classLoader,
 * META-INF/ of Thread.currentThread().getContextClassLoader() and META-INF/ of ClassLoader.getSystemClassLoader().
 * Thus, a default configuration value for all your new Knowledge Sessions is provided by setting the corresponding
 * System property.
 * <p>
 * The configuration is immutable after the KnowledgeSession is created, and there is no way to make it
 * mutable again. This is to avoid inconsistent behaviour within a Knowledge Session.
 * <p>
 * The available properties are:
 * <ul>
 * <li>drools.keepReference = &lt;"true"|"false"&gt;<br>
 * If set to true (the default), the KnowledgeBase will retain a reference to the newly created session.
 * <li>drools.clockType = &lt;"pseudo"|"realtime"&gt;<br>
 * Defines the kind of clock to be used for Event timestamps and temporal reasoning. Default is "realtime".
 * <li>drools.queryListener = &lt;"standard"|"lightweight"&gt;<br>
 * The "standard" (and default) query data collection mechanism copies matching facts for safeguarding against
 * Working Memory modifications happening concurrently. If no such parallel modification
 * is possible, the more efficient lightweight query listener may be used.
 * <li>drools.workItemHandlers is the prefix for the property name for a Drools Flow work item handler.
 * </ul>
 */
public interface KnowledgeSessionConfiguration
    extends
    PropertiesConfiguration,
    KnowledgeSessionOptionsConfiguration {

}
