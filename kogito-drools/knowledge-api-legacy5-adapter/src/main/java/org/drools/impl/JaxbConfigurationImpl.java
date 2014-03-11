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

package org.drools.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.drools.builder.JaxbConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.xjc.Options;

public class JaxbConfigurationImpl implements JaxbConfiguration {
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
}
