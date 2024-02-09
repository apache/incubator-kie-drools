/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.traits.core.factmodel;

import java.io.ByteArrayInputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This mostly shows how to go to a jar and back, if needed.
 * Probably can be partly ignored eventually.
 */
public class JeneratorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JeneratorTest.class);

    @Test
    public void testRoundTrip() throws Exception {
        Fact f = new Fact();
        f.name = "Foobar";
        Field f1 = new Field();
        f1.name = "name";
        f1.type = "java.lang.String";
        f.fields.add(f1);

        Field f2 = new Field();
        f2.name = "age";
        f2.type = "java.lang.Integer";
        f.fields.add(f2);

        Fact f_  = new Fact();
        f_.name = "Baz";

        Field f1_ = new Field();
        f1_.name = "name";
        f1_.type = "java.lang.String";
        f_.fields.add(f1_);


        Jenerator jen = new Jenerator();
        byte[] data = jen.createJar(new Fact[] {f, f_}, "whee.waa");
        JarInputStream jis = new JarInputStream(new ByteArrayInputStream(data));
        JarEntry je = jis.getNextJarEntry();

        assertThat(je).isNotNull();
        LOGGER.debug(je.getName());
        assertThat(je.getName()).isEqualTo("factmodel.xml");

        je = jis.getNextJarEntry();

        assertThat(je).isNotNull();
        LOGGER.debug(je.getName());
        assertThat(je.getName()).isEqualTo("whee/waa/Foobar.class");

        je = jis.getNextJarEntry();

        assertThat(je).isNotNull();
        LOGGER.debug(je.getName());
        assertThat(je.getName()).isEqualTo("whee/waa/Baz.class");

        Fact[] facts = jen.loadMetaModel(new JarInputStream(new ByteArrayInputStream(data)));
        assertThat(facts.length).isEqualTo(2);
        assertThat(facts[0].name).isEqualTo("Foobar");
        assertThat(facts[1].name).isEqualTo("Baz");


    }

}
