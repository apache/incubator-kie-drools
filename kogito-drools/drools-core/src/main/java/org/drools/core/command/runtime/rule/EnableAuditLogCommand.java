package org.drools.core.command.runtime.rule;

import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;

@XmlRootElement
@XmlAccessorType( XmlAccessType.NONE )
public class EnableAuditLogCommand implements ExecutableCommand<Void> {

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
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        KieServices.Factory.get().getLoggers().newFileLogger( ksession, auditLogFile );
        return null;
    }

    @Override
    public String toString() {
        return "KieServices.Factory.get().getLoggers().newFileLogger( ksession, " + auditLogFile + " )";
    }

}
