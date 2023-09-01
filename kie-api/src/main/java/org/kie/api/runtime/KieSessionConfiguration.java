package org.kie.api.runtime;

import org.kie.api.runtime.conf.KieSessionOptionsConfiguration;

/**
 * A class to store Session related configuration. It must be used at session instantiation time
 * or not used at all.
 *
 * This class will automatically load default values from a number of places, accumulating properties from each location.
 * This list of locations, in given priority is:
 * System properties, home directory, working directory, META-INF/ of optionally provided classLoader
 * META-INF/ of Thread.currentThread().getContextClassLoader() and META-INF/ of  ClassLoader.getSystemClassLoader()
 *
 * So if you want to set a default configuration value for all your new KieSession, you can simply set the property as
 * a System property.
 *
 * After the KieSession is created, it makes the configuration immutable and there is no way to make it
 * mutable again. This is to avoid inconsistent behaviour inside KieSession.
 */
public interface KieSessionConfiguration
        extends
        KieSessionOptionsConfiguration {

}
