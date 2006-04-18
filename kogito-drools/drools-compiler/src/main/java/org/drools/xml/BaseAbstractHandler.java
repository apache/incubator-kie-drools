package org.drools.xml;

import java.util.Set;

/**
 * @author mproctor
 * 
 */
abstract class BaseAbstractHandler
{
    protected PackageReader packageReader;
    protected Set validPeers;
    protected Set validParents;
    protected boolean allowNesting;

    public Set getValidParents()
    {
        return this.validParents;
    }

    public Set getValidPeers()
    {
        return this.validPeers;
    }

    public boolean allowNesting()
    {
        return this.allowNesting;
    }
}