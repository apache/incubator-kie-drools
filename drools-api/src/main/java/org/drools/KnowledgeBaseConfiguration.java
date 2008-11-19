package org.drools;

/**
 * KnowledgeBaseConfiguration
 *
 * A class to store KnowledgeBase related configuration. It must be used at KnowledgeBase instantiation time
 * or not used at all.
 * 
 * This class will automatically load default values from a number of places, accumulating properties from each location.
 * This list of locations, in given priority is:
 * System properties, home directory, working directory, META-INF/ of optionally provided classLoader
 * META-INF/ of Thread.currentThread().getContextClassLoader() and META-INF/ of  ClassLoader.getSystemClassLoader()
 * 
 * So if you want to set a default configuration value for all your new KnowledgeBase, you can simply set the property as
 * a System property.
 *
 * After the KnowledgeBase is created, it makes the configuration immutable and there is no way to make it
 * mutable again. This is to avoid inconsistent behaviour inside KnowledgeBase.
 * 
 * The following properties are supported:
 *
 * drools.maintainTms = <true|false>
 * drools.assertBehaviour = <identity|equality>
 * drools.logicalOverride = <discard|preserve>
 * drools.sequential = <true|false>
 * drools.sequential.agenda = <sequential|dynamic>
 * drools.removeIdentities = <true|false>
 * drools.shareAlphaNodes  = <true|false>
 * drools.shareBetaNodes = <true|false>
 * drools.alphaMemory = <true/false>
 * drools.alphaNodeHashingThreshold = <1...n>
 * drools.compositeKeyDepth  = <1..3>
 * drools.indexLeftBetaMemory = <true/false>
 * drools.indexRightBetaMemory = <true/false>
 * drools.consequenceExceptionHandler = <qualified class name>
 * 
 * The follow properties have not yet been migrated from the Drools 4.0 api:
 *
 * drools.executorService = <qualified class name>
 * drools.conflictResolver = <qualified class name>
 * drools.ruleBaseUpdateHandler = <qualified class name>
 *  
 */
public interface KnowledgeBaseConfiguration
    extends
    PropertiesConfiguration {

}
