package org.drools;

/**
 *<p>
 * A class to store KnowledgeBase related configuration. It must be used at KnowledgeBase instantiation time
 * or not used at all.
 * </p>
 *
 * <p>
 * This class will automatically load default values from a number of places, accumulating properties from each location.
 * This list of locations, in given priority is:
 * System properties, home directory, working directory, META-INF/ of optionally provided classLoader
 * META-INF/ of Thread.currentThread().getContextClassLoader() and META-INF/ of  ClassLoader.getSystemClassLoader()
 * </p>
 * 
 * <p>
 * So if you want to set a default configuration value for all your new KnowledgeBase, you can simply set the property as
 * a System property.
 * </p>
 * 
 * <p>
 * After the KnowledgeBase is created, it makes the configuration immutable and there is no way to make it
 * mutable again. This is to avoid inconsistent behaviour inside KnowledgeBase.
 * </p>
 * 
 * <p>
 * The following properties are supported:
 * <ul>
 * <li>drools.maintainTms = <true|false></li>
 * <li>drools.assertBehaviour = <identity|equality></li>
 * <li>drools.logicalOverride = <discard|preserve></li>
 * <li>drools.sequential = <true|false></li>
 * <li>drools.sequential.agenda = <sequential|dynamic></li>
 * <li>drools.removeIdentities = <true|false></li>
 * <li>drools.shareAlphaNodes  = <true|false></li>
 * <li>drools.shareBetaNodes = <true|false></li>
 * <li>drools.alphaMemory = <true/false></li>
 * <li>drools.alphaNodeHashingThreshold = <1...n></li>
 * <li>drools.compositeKeyDepth  = <1..3></li>
 * <li>drools.indexLeftBetaMemory = <true/false></li>
 * <li>drools.indexRightBetaMemory = <true/false></li>
 * <li>drools.consequenceExceptionHandler = <qualified class name></li>
 * </ul>
 * </p>
 * 
 * <p>
 * The follow properties have not yet been migrated from the Drools 4.0 api:
 * <ul>
 * <li>drools.executorService = <qualified class name></li>
 * <li>drools.conflictResolver = <qualified class name></li>
 * <li>drools.ruleBaseUpdateHandler = <qualified class name></li>
 * </ul>
 * </p>
 */
public interface KnowledgeBaseConfiguration
    extends
    PropertiesConfiguration {

}
