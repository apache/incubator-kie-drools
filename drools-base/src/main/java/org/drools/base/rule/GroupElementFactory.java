package org.drools.base.rule;

/**
 * A simple factory for GroupElements
 */
public class GroupElementFactory {

    private GroupElementFactory() {
    }

    public static GroupElement newAndInstance() {
        return new GroupElement( GroupElement.AND );
    }

    public static GroupElement newOrInstance() {
        return new GroupElement( GroupElement.OR );
    }

    public static GroupElement newNotInstance() {
        return new GroupElement( GroupElement.NOT );
    }

    public static GroupElement newExistsInstance() {
        return new GroupElement( GroupElement.EXISTS );
    }

}
