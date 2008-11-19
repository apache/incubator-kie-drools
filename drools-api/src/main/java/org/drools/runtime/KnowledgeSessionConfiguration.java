package org.drools.runtime;

import org.drools.PropertiesConfiguration;

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
 * drools.clockType = <pseudo|realtime|heartbeat|implicit>
 */
public interface KnowledgeSessionConfiguration
    extends
    PropertiesConfiguration {

}
