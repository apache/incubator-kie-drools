/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.mvel.compiler.lang.descr;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.mvel.compiler.Person;
import org.junit.Test;

import static org.drools.core.util.StringUtils.generateUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
    public void testSerializationImportDescr() {
        PackageDescrBuilder builder = DescrFactory.newPackage().name("foo");
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

            assertFalse(newDescr.getPreferredPkgUUID().isPresent());
        } catch (IOException | ClassNotFoundException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testSerializationPkgUUID() {
        PackageDescrBuilder builder = DescrFactory.newPackage().name("foo");
        String className = Person.class.getName();
        builder.newImport().target(className).end();
        PackageDescr descr = builder.getDescr();
        String pkgUUID = generateUUID();
        descr.setPreferredPkgUUID(pkgUUID);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            descr.writeExternal(out);

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            PackageDescr newDescr = new PackageDescr();
            newDescr.readExternal(in);

            assertTrue(newDescr.getPreferredPkgUUID().isPresent());
            assertEquals(pkgUUID, newDescr.getPreferredPkgUUID().get());
        } catch (IOException | ClassNotFoundException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetPreferredPkgUUID() {
        PackageDescr descr = new PackageDescr();
        assertFalse(descr.getPreferredPkgUUID().isPresent());
        String pkgUUID = generateUUID();
        descr.setPreferredPkgUUID(pkgUUID);
        assertTrue(descr.getPreferredPkgUUID().isPresent());
        assertEquals(pkgUUID, descr.getPreferredPkgUUID().get());
    }
}
