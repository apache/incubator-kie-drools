package org.drools.transaction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;

import javax.transaction.xa.XAResource;

import org.drools.StatefulSession;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.marshalling.DefaultMarshaller;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;

import sun.nio.ch.ChannelInputStream;


public class InMemoryTransactionManager {       
    LinkedList<byte[]> levels;
    
    byte[] lastSave;  
    
    public XAResource getXAResource() {
        return null;
    }
}
