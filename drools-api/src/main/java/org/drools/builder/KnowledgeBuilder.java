package org.drools.builder;

import java.io.Reader;
import java.net.URL;
import java.util.Collection;

import org.drools.definition.KnowledgePackage;

public interface KnowledgeBuilder
    extends
    RuleBuilder,
    ProcessBuilder {

    void addResource(URL url,
                     KnowledgeType type);

    void addResource(URL url,
                     KnowledgeType type,
                     ResourceConfiguration configuration);

    void addResource(Reader reader,
                     KnowledgeType type);

    void addResource(Reader reader,
                     KnowledgeType type,
                     ResourceConfiguration configuration);

    /**
     * Returns the built packages.
     * 
     * If the KnowledgeBuilder has errors the Collection will be empty. The hasErrors()
     * method should always be checked first, to make sure you are getting the packages
     * that you wanted built.
     * 
     * @return
     *     The Collection of KnowledgePackages
     */
    Collection<KnowledgePackage> getKnowledgePackages();

    /**
     * If errors occurred during the build process they are added here
     * @return
     */
    boolean hasErrors();

    /**
     * Return errors that occurred during the build process.
     * @return
     */
    KnowledgeBuilderErrors getErrors();

}
