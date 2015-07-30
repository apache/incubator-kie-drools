/*
 * Copyright 2015 JBoss Inc
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

package org.drools.core.command.runtime.rule;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsInstanceOf.*;
import static org.junit.Assert.*;
import org.drools.core.common.DisconnectedFactHandle;
import org.drools.core.xml.jaxb.util.JaxbListWrapper;
import org.junit.Test;

public class CommandSerializationTest {

    private Class<?>[] annotatedJaxbClasses = { JaxbListWrapper.class };

    @Test
    public void updateCommandTest() throws Exception {
        DisconnectedFactHandle discFactHandle = new DisconnectedFactHandle(2, 3, 4, 5l, "entry-point-id", "str-obj", true);

        DisconnectedFactHandle copyDiscFactHandle = roundTrip(discFactHandle);
        verifyDisconnectedFactHandle(discFactHandle, copyDiscFactHandle);

        UpdateCommand cmd = new UpdateCommand(discFactHandle, "new-str-object");
        UpdateCommand copyCmd = roundTrip(cmd);

        verifyDisconnectedFactHandle(discFactHandle, copyCmd.getHandle());
        assertEquals("entry point", cmd.getEntryPoint(), copyCmd.getEntryPoint());
        assertEquals("object", cmd.getObject(), copyCmd.getObject());
    }

    @Test
    public void insertObjectCommandTest() throws Exception {
        InsertObjectCommand cmd = new InsertObjectCommand("obj", "out-id");
        cmd.setReturnObject(false);
        cmd.setEntryPoint("entry-point");
        
        InsertObjectCommand copyCmd = roundTrip(cmd);
        
        assertEquals( "object", cmd.getObject(), copyCmd.getObject());
        assertEquals( "out id", cmd.getOutIdentifier(), copyCmd.getOutIdentifier() );
        assertEquals( "return obj", cmd.isReturnObject(), copyCmd.isReturnObject() );
        assertEquals( "entry point", cmd.getEntryPoint(), copyCmd.getEntryPoint() );
        assertEquals( "disconnected", cmd.isDisconnected(), copyCmd.isDisconnected() );
    }

    @Test
    public void insertObjectCommandListTest() throws Exception {
        List<String> objectList = new ArrayList<String>();
        objectList.add("obj");
        InsertObjectCommand cmd = new InsertObjectCommand(objectList, "out-id");

        InsertObjectCommand copyCmd = roundTrip(cmd);

        assertNotNull(copyCmd);
        assertThat(copyCmd.getObject(), is(instanceOf(List.class)));
        assertEquals( "object", cmd.getObject(), copyCmd.getObject());
    }

    @Test
    public void insertObjectCommandEmptyListTest() throws Exception {
        List<String> objectList = new ArrayList<String>();
        objectList.add("one-element");
        InsertObjectCommand cmd = new InsertObjectCommand(objectList, "out-id");

        // test list with 1 element
        InsertObjectCommand copyCmd = roundTrip(cmd);

        assertNotNull(copyCmd);
        assertThat(copyCmd.getObject(), is(instanceOf(List.class)));
        assertEquals( "object", cmd.getObject(), copyCmd.getObject());
        
        // test empty list
        objectList.clear();
        copyCmd = roundTrip(cmd);
        
        assertNotNull(copyCmd);
        assertThat(copyCmd.getObject(), is(instanceOf(List.class)));
        assertEquals( "object", cmd.getObject(), copyCmd.getObject());
        
    }

    private void verifyDisconnectedFactHandle( DisconnectedFactHandle orig, DisconnectedFactHandle copy ) {
        assertNotNull("copy disconnected fact handle is null", copy);
        assertEquals("id", orig.getId(), copy.getId());
        assertEquals("identity hash code", orig.getIdentityHashCode(), copy.getIdentityHashCode());
        assertEquals("object hash code", orig.getObjectHashCode(), copy.getObjectHashCode());
        assertEquals("recency", orig.getRecency(), copy.getRecency());
        assertEquals("entry point id", orig.getEntryPointId(), copy.getEntryPointId());
        assertEquals("object", orig.getObject(), copy.getObject());
        assertEquals("trait type", orig.getTraitType(), copy.getTraitType());
    }

    private <T> T roundTrip( Object obj ) throws Exception {
        Class[] classes = { obj.getClass() };
        JAXBContext ctx = getJaxbContext(classes);
        String xmlOut = marshall(ctx, obj);
        return unmarshall(ctx, xmlOut);
    }

    private <T> T unmarshall( JAXBContext ctx, String xmlIn ) throws Exception {
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlIn.getBytes(Charset.forName("UTF-8")));
        Object out = ctx.createUnmarshaller().unmarshal(xmlStrInputStream);
        return (T) out;
    }

    private String marshall( JAXBContext ctx, Object obj ) throws Exception {
        StringWriter writer = new StringWriter();
        ctx.createMarshaller().marshal(obj, writer);
        return writer.getBuffer().toString();
    }

    private JAXBContext getJaxbContext( Class<?>... classes ) throws Exception {
        List<Class<?>> jaxbClassList = new ArrayList<Class<?>>();
        jaxbClassList.addAll(Arrays.asList(classes));
        jaxbClassList.addAll(Arrays.asList(annotatedJaxbClasses));
        Class<?>[] jaxbClasses = jaxbClassList.toArray(new Class[jaxbClassList.size()]);
        return JAXBContext.newInstance(jaxbClasses);
    }
}
