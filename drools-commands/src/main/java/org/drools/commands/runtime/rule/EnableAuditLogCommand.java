/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.commands.runtime.rule;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.kie.api.KieServices;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;
import org.drools.util.PortablePath;

@XmlRootElement
@XmlAccessorType( XmlAccessType.NONE )
public class EnableAuditLogCommand implements ExecutableCommand<Void> {

    private static final long serialVersionUID = -2615993429554597508L;

    @XmlAttribute( required = true )
    private String directory;
    @XmlAttribute( required = true )
    private String filename;
    private PortablePath auditLogFile;

    public EnableAuditLogCommand( String directory, String filename ) {
        this.directory = directory;
        this.filename = filename;

        if ( directory != null ) {
            auditLogFile = PortablePath.of(directory + '/' + filename);
        }

    }

    @Override
    public Void execute( Context context ) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        KieServices.Factory.get().getLoggers().newFileLogger( ksession, auditLogFile.asString() );
        return null;
    }

    @Override
    public String toString() {
        return "KieServices.Factory.get().getLoggers().newFileLogger( ksession, " + auditLogFile.asString() + " )";
    }

}
