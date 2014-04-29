package org.drools.compiler.lang.descr;

import org.drools.compiler.Person;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class PackageDescrTest {

    @Test
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

        desc.afterRuleAdded( rule );
        
        Map<String, AttributeDescr> ruleAts = rule.getAttributes();
        assertEquals("overridden", ((AttributeDescr)ruleAts.get( "foo" )).getValue());
        assertEquals("default", ((AttributeDescr)ruleAts.get( "foo2" )).getValue());
        
    }

    @Test
    public void testSerialization() {
        PackageDescrBuilder builder = DescrFactory.newPackage().name( "foo" );
        String className = Person.class.getName();
        builder.newImport().target(className).end();
        PackageDescr descr = builder.getDescr();

        ImportDescr importDescr = new ImportDescr(className);
        ImportDescr badImportDescr = new ImportDescr(null);

        assertTrue(descr.getImports().contains(importDescr));

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            descr.writeExternal(out);

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            PackageDescr newDescr = new PackageDescr();
            newDescr.readExternal(in);

            assertFalse(newDescr.getImports().contains(badImportDescr));
            assertTrue(newDescr.getImports().contains(importDescr));

        } catch ( IOException ioe ) {
            fail( ioe.getMessage() );
        } catch ( ClassNotFoundException cnfe ) {
            fail( cnfe.getMessage() );
        }

    }

}
