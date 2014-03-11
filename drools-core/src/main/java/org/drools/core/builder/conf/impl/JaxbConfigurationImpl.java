/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.builder.conf.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.JaxbConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.xjc.Language;
import com.sun.tools.xjc.Options;

public class JaxbConfigurationImpl extends ResourceConfigurationImpl implements JaxbConfiguration {
    private final Logger logger = LoggerFactory.getLogger( JaxbConfigurationImpl.class ); 
    
    private Options xjcOpts;
    private String systemId;
    
    private List<String> classes;

    public JaxbConfigurationImpl() { }

    public JaxbConfigurationImpl(Options xjcOpts,
                                 String systemId) {
        this.xjcOpts = xjcOpts;
        this.systemId = systemId;
        this.classes = new ArrayList<String>();
    }


    public Options getXjcOpts() {
        return xjcOpts;
    }
    
    
    public String getSystemId() {
        return systemId;
    }


    public List<String> getClasses() {
        return classes;
    }


    public void setClasses(List<String> classes) {
        this.classes = classes;
    }
    
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
    
    public void setXjcOpts(Options xjcOpts) {
        this.xjcOpts = xjcOpts;
    }
    
    public byte[] toByteArray() {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream( buf );
            out.writeObject( xjcOpts );
            out.writeObject( systemId );
            out.writeObject( classes );
            out.close();
        } catch ( IOException e ) {
            logger.error( "Error serializing decision table configuration.", e );
        }
        return buf.toByteArray();
    }
    
    @SuppressWarnings("unchecked")
    public JaxbConfiguration fromByteArray( byte[] conf ) {
        try {
            ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( conf ) );
            this.xjcOpts = (Options) in.readObject();
            this.systemId = (String) in.readObject();
            this.classes =  (List<String>) in.readObject();
        } catch ( Exception e ) {
            logger.error( "Error deserializing decision table configuration.", e );
        }
        return this;
    }


    public Properties toProperties() {
        Properties prop = super.toProperties();
        prop.setProperty( "drools.jaxb.conf.systemId", systemId );
        prop.setProperty( "drools.jaxb.conf.classes", classes.toString() );
        if (xjcOpts != null) {
            // how to serialize Options to a property file???
            prop.setProperty( "drools.jaxb.conf.opts.class", xjcOpts.getClass().getName() );
            if (xjcOpts.getSchemaLanguage() != null) {
                prop.setProperty( "drools.jaxb.conf.opts.lang", xjcOpts.getSchemaLanguage().toString() );
            }
        }
        return prop;
    }

    public ResourceConfiguration fromProperties(Properties prop) {
        super.fromProperties(prop);
        systemId = prop.getProperty( "drools.jaxb.conf.systemId", null );
        String classesStr = prop.getProperty( "drools.jaxb.conf.classes", "[]" );
        classesStr = classesStr.substring( 1, classesStr.length()-1 ).trim();
        classes = new ArrayList<String>();
        if( classesStr != null && classesStr.length() > 1 ) {
            // can't use Arrays.asList() because have to trim() each element
            for( String clz : classesStr.split( "," ) ) {
                classes.add( clz.trim() );
            }
        }

        // how to deserialize Options from a properties file?
        String optsClass = prop.getProperty( "drools.jaxb.conf.opts.class", null );
        if (optsClass != null) {
            try {
                xjcOpts = (Options) Class.forName( optsClass ).newInstance();
                String optsLang = prop.getProperty( "drools.jaxb.conf.opts.lang", null );
                if (optsLang != null) {
                    xjcOpts.setSchemaLanguage( Language.valueOf(optsLang) );
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return this;
    }
}
