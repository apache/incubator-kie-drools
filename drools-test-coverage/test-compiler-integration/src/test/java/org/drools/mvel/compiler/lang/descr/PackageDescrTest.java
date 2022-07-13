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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.drools.core.util.StringUtils.generateUUID;

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
        assertThat(((AttributeDescr) pkgAts.get(0)).getValue()).isEqualTo("bar");
        assertThat(((AttributeDescr) pkgAts.get(1)).getValue()).isEqualTo("default");

        desc.afterRuleAdded( rule );
        
        Map<String, AttributeDescr> ruleAts = rule.getAttributes();
        assertThat(((AttributeDescr) ruleAts.get("foo")).getValue()).isEqualTo("overridden");
        assertThat(((AttributeDescr) ruleAts.get("foo2")).getValue()).isEqualTo("default");
        
    }

    @Test
    public void testSerializationImportDescr() {
        PackageDescrBuilder builder = DescrFactory.newPackage().name("foo");
        String className = Person.class.getName();
        builder.newImport().target(className).end();
        PackageDescr descr = builder.getDescr();

        ImportDescr importDescr = new ImportDescr(className);
        ImportDescr badImportDescr = new ImportDescr(null);

        assertThat(descr.getImports().contains(importDescr)).isTrue();

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            descr.writeExternal(out);

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            PackageDescr newDescr = new PackageDescr();
            newDescr.readExternal(in);

            assertThat(newDescr.getImports().contains(badImportDescr)).isFalse();
            assertThat(newDescr.getImports().contains(importDescr)).isTrue();

            assertThat(newDescr.getPreferredPkgUUID().isPresent()).isFalse();
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

            assertThat(newDescr.getPreferredPkgUUID().isPresent()).isTrue();
            assertThat(newDescr.getPreferredPkgUUID().get()).isEqualTo(pkgUUID);
        } catch (IOException | ClassNotFoundException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetPreferredPkgUUID() {
        PackageDescr descr = new PackageDescr();
        assertThat(descr.getPreferredPkgUUID().isPresent()).isFalse();
        String pkgUUID = generateUUID();
        descr.setPreferredPkgUUID(pkgUUID);
        assertThat(descr.getPreferredPkgUUID().isPresent()).isTrue();
        assertThat(descr.getPreferredPkgUUID().get()).isEqualTo(pkgUUID);
    }
}
