package org.drools.smf;

import java.net.MalformedURLException;
import java.net.URL;

import org.drools.rule.RuleSet;

/**
 * References the results from the compilation of a RuleSet.
 * @author mproctor
 *
 */
public class RuleSetPackage
{
    final RuleSet ruleSet;
    final URL     binJar;
    final URL     srcJar;

    public RuleSetPackage(final RuleSet ruleSet,
                          final URL binJar,
                          final URL srcJar)
    {
        this.ruleSet = ruleSet;
        this.binJar = binJar;
        this.srcJar = srcJar;
    }

    public URL getBinJar()
    {
        return binJar;
    }

    public RuleSet getRuleSet()
    {
        return ruleSet;
    }

    /**
     * If the compiler produced no src then a src jar will not exist and this method returns null.
     * 
     * @return
     */    
    public URL getSrcJar()
    {
        return srcJar;
    }    

}
