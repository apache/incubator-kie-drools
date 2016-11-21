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
