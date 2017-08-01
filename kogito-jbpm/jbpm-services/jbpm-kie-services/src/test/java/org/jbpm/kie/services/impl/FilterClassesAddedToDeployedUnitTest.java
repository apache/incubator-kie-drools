/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.kie.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBContext;

import org.jbpm.kie.services.impl.jaxb.Child;
import org.jbpm.kie.services.impl.jaxb.GrandChild;
import org.jbpm.kie.services.impl.jaxb.GreatGrandChild;
import org.jbpm.kie.services.impl.jaxb.Parent;
import org.junit.Test;


public class FilterClassesAddedToDeployedUnitTest {

    @Test
    public void jaxbContextAndTheClasspathTest() throws Exception {
        Class [] boundClasses = {
                Parent.class
        };
        JAXBContext ctx = JAXBContext.newInstance(boundClasses);
        System.out.println(ctx.toString());

        Parent parent = new Parent();
        parent.child = new Child();
        parent.child.grandChild = new GrandChild();
        parent.child.grandChild.greatGrandChild = new GreatGrandChild();
        parent.child.grandChild.greatGrandChild.song = "Carrie & Lowell";
        parent.child.grandChild.greatGrandChild.og = "ignored";

        StringWriter writer = new StringWriter();
        ctx.createMarshaller().marshal(parent, writer);

        String xmlStr = writer.toString();

        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlStr.getBytes(Charset.forName("UTF-8")));

        System.out.println(ctx.toString());

        Parent copyParent = (Parent) ctx.createUnmarshaller().unmarshal(xmlStrInputStream);

        assertEquals( parent.child.grandChild.greatGrandChild.song,
                      copyParent.child.grandChild.greatGrandChild.song );

        assertNotEquals( parent.child.grandChild.greatGrandChild.og,
                      copyParent.child.grandChild.greatGrandChild.og );
    }
}
