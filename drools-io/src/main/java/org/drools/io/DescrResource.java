package org.drools.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;

import org.kie.api.definition.KieDescr;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

public class DescrResource extends BaseResource implements InternalResource, Externalizable {
    private static final long serialVersionUID = 3931132608413160031L;
    
    private KieDescr descr;
    
    public DescrResource() { }

    public DescrResource(KieDescr descr ) {
        if ( descr == null ) {
            throw new IllegalArgumentException( "descr cannot be null" );
        }
        this.descr = descr;
        setResourceType( ResourceType.DESCR );
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        descr = (KieDescr) in.readObject();
    }
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( descr );
    }

    public String getEncoding() {
        return null;
    }

    public URL getURL() throws IOException {
        throw new FileNotFoundException( "descr cannot be resolved to URL");
    }

    public InputStream getInputStream() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.flush();
        oos.close();
        return new ByteArrayInputStream(baos.toByteArray());
    }
    
    public Reader getReader() throws IOException {
        throw new IOException( "descr does not support readers");
    }

    public KieDescr getDescr() {
        return this.descr;
    }
    
    public boolean isDirectory() {
        return false;
    }

    public Collection<Resource> listResources() {
        throw new RuntimeException( "This Resource cannot be listed, or is not a directory" );
    }
    
    public boolean hasURL() {
        return false;
    }
    
    public String toString() {
        return "DescrResource[resource=" + this.descr + "]";
    }

}
