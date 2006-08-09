package org.drools.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


/**
 * This utility provides local persistence for rules.
 * <p/>
 * Rules can be saved to a single file for later synchronization with the repository.
 * 
 * This uses XStream, as well as (optionally) object serialization.
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class LocalStore {

    public static final int MODE_XSTREAM = 1;
    public static final int MODE_OBJECT_SER = 2;
    
    private int mode = MODE_XSTREAM;
    
    /** Default instance. */
    public LocalStore() {
    }
    
    /** Can use XStream for XML storage, or object serialization if required */
    public LocalStore(int mode) {
        this.mode = mode;
    }
    
    /** Saves the asset locally. Applies to the whole object graph */
    public void save(Asset ruleAsset, OutputStream out) {
        if (mode == MODE_XSTREAM) {            
            OutputStreamWriter writer = new OutputStreamWriter(out);
            getXStream().toXML(ruleAsset, writer);
        } else if (mode == MODE_OBJECT_SER) {
            try {
                ObjectOutputStream objOut = new ObjectOutputStream(out);
                objOut.writeObject(ruleAsset);
                objOut.flush();
            }
            catch ( IOException e ) {
                throw new RepositoryException("Unable to store asset locally.", e);
            }
        } else {
            throw new IllegalArgumentException("Unknown local persistence mode.");
        }
    }
    
    
    /** Loads up the whole object graph. */
    public Asset load(InputStream input) {
        if (mode == MODE_XSTREAM) {            
            InputStreamReader reader = new InputStreamReader(input);
            Asset asset = (Asset) getXStream().fromXML(reader);
            return asset;            
        } else if (mode == MODE_OBJECT_SER) {
            try {
                
                ObjectInputStream objIn = new ObjectInputStream(input);
                Asset asset = (Asset) objIn.readObject();
                return asset;
                
            }
            catch ( Exception e ) {
                throw new RepositoryException("Unable to store asset locally.", e);
            } 
        } else {
            throw new IllegalArgumentException("Unknown local persistence mode.");
        }
        
    }

    private XStream getXStream() {
        XStream xstream = new XStream(new DomDriver());
        return xstream;
    }
    
    
    
    
    
}
