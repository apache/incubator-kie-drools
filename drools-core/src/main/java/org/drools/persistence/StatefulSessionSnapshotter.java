package org.drools.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.drools.StatefulSession;
import org.drools.common.InternalRuleBase;
import org.drools.marshalling.DefaultMarshaller;
import org.drools.marshalling.Marshaller;

public class StatefulSessionSnapshotter implements ByteArraySnapshotter{
	StatefulSession session;
    Marshaller marshaller = new DefaultMarshaller();
	
	public StatefulSessionSnapshotter(StatefulSession session) {
		this.session = session;
	}

	public byte[] getSnapshot() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            marshaller.write( baos, (InternalRuleBase) session.getRuleBase(), session );
        } catch (IOException e) {
        	throw new RuntimeException( "Unable to get session snapshot", e );
        }
        
        return baos.toByteArray();
	}

	public void loadSnapshot(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
        try {
            marshaller.read( bais, (InternalRuleBase) session.getRuleBase(), session );
        } catch (Exception e) {
        	throw new RuntimeException( "Unable to load session snapshot", e );
        }
		
	}
}
