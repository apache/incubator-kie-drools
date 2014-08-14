package org.drools.core.command.runtime.rule;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBContext;

import static org.junit.Assert.*;
import org.drools.core.common.DisconnectedFactHandle;
import org.junit.Test;

public class CommandSerializationTest {

    @Test
    public void updateCommandTest() throws Exception {
        DisconnectedFactHandle discFactHandle = new DisconnectedFactHandle(2, 3, 4, 5l, "entry-point-id", "str-obj", true);

        DisconnectedFactHandle copyDiscFactHandle = roundTrip(discFactHandle, DisconnectedFactHandle.class);
        verifyDisconnectedFactHandle(discFactHandle, copyDiscFactHandle);

        UpdateCommand cmd = new UpdateCommand(discFactHandle, "new-str-object");
        UpdateCommand copyCmd = roundTrip(cmd, UpdateCommand.class);

        verifyDisconnectedFactHandle(discFactHandle, copyCmd.getHandle());
        assertEquals("entry point", cmd.getEntryPoint(), copyCmd.getEntryPoint());
        assertEquals("object", cmd.getObject(), copyCmd.getObject());
    }

    @Test
    public void insertObjectCommandTest() throws Exception {
        InsertObjectCommand cmd = new InsertObjectCommand("obj", "out-id");
        cmd.setReturnObject(false);
        cmd.setEntryPoint("entry-point");
        
        InsertObjectCommand copyCmd = roundTrip(cmd, InsertObjectCommand.class);
        
        assertEquals( "object", cmd.getObject(), copyCmd.getObject());
        assertEquals( "out id", cmd.getOutIdentifier(), copyCmd.getOutIdentifier() );
        assertEquals( "return obj", cmd.isReturnObject(), copyCmd.isReturnObject() );
        assertEquals( "entry point", cmd.getEntryPoint(), copyCmd.getEntryPoint() );
        assertEquals( "disconnected", cmd.isDisconnected(), copyCmd.isDisconnected() );
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

    private <T> T roundTrip( Object obj, Class<T> objClass ) throws Exception {
        Class[] classes = { objClass };
        JAXBContext ctx = getJaxbContext(classes);
        String xmlOut = marshall(ctx, obj);
        return unmarshall(ctx, xmlOut, objClass);
    }

    private <T> T unmarshall( JAXBContext ctx, String xmlIn, Class<T> objClass ) throws Exception {
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlIn.getBytes(Charset.forName("UTF-8")));
        Object out = ctx.createUnmarshaller().unmarshal(xmlStrInputStream);
        return (T) out;
    }

    private String marshall( JAXBContext ctx, Object obj ) throws Exception {
        StringWriter writer = new StringWriter();
        ctx.createMarshaller().marshal(obj, writer);
        return writer.getBuffer().toString();
    }

    private JAXBContext getJaxbContext( Class... classes ) throws Exception {
        return JAXBContext.newInstance(classes);
    }
}
