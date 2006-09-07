package org.drools.lang.descr;

/**
 * This is the factory for ALL descriptors (eventually).
 * This will be tied in a with a package builder session, so it can add in context information for 
 * validation when parsing.
 * 
 * @author Michael Neale
 *
 */
public class DescrFactory {

    public FromDescr createFrom() {
        return new FromDescr();
    }

    public AccumulateDescr createAccumulate() {
        return new AccumulateDescr();
    }
}
