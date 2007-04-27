package org.drools.lang.descr;

import java.util.List;

import junit.framework.TestCase;

public class PackageDescrTest extends TestCase {

    public void testAttributeOverriding() {
        PackageDescr desc = new PackageDescr("foo");
        
        AttributeDescr at1 = new AttributeDescr("foo", "bar");
        AttributeDescr at2 = new AttributeDescr("foo2", "default");
        
        desc.addAttribute( at1 );
        desc.addAttribute( at2 );
        
        RuleDescr rule = new RuleDescr("abc");
        rule.addAttribute( new AttributeDescr("foo", "overridden") );
        
        desc.addRule( rule );
        
        List pkgAts = desc.getAttributes();
        assertEquals("bar", ((AttributeDescr)pkgAts.get( 0 )).getValue());
        assertEquals("default", ((AttributeDescr)pkgAts.get( 1 )).getValue());
        
        
        List ruleAts = rule.getAttributes();
        assertEquals("overridden", ((AttributeDescr)ruleAts.get( 0 )).getValue());
        assertEquals("default", ((AttributeDescr)ruleAts.get( 1 )).getValue());
        
        
        
    }
    
}
