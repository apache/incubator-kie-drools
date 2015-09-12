package org.drools.core.command.runtime.rule;

import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.Context;

@XmlRootElement
@XmlAccessorType( XmlAccessType.NONE )
public class EnableAuditLogCommand implements GenericCommand<Void> {

    private static final long serialVersionUID = -2615993429554597508L;

    @XmlAttribute( required = true )
    private String directory;
    @XmlAttribute( required = true )
    private String filename;
    private String auditLogFile;

    public EnableAuditLogCommand( String directory, String filename ) {
        this.directory = directory;
        this.filename = filename;

        if ( directory != null ) {
            auditLogFile = directory + File.separator + filename;
        }

    }

    @Override
    public Void execute( Context context ) {
        KieSession ksession = ( (KnowledgeCommandContext) context ).getKieSession();
        KieServices.Factory.get().getLoggers().newFileLogger( ksession, auditLogFile );
        return null;
    }

    @Override
    public String toString() {
        return "KieServices.Factory.get().getLoggers().newFileLogger( ksession, " + auditLogFile + " )";
    }

}
