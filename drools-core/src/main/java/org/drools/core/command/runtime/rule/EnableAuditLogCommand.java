package org.drools.core.command.runtime.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.KieServices;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;
import org.kie.memorycompiler.resources.KiePath;

@XmlRootElement
@XmlAccessorType( XmlAccessType.NONE )
public class EnableAuditLogCommand implements ExecutableCommand<Void> {

    private static final long serialVersionUID = -2615993429554597508L;

    @XmlAttribute( required = true )
    private String directory;
    @XmlAttribute( required = true )
    private String filename;
    private KiePath auditLogFile;

    public EnableAuditLogCommand( String directory, String filename ) {
        this.directory = directory;
        this.filename = filename;

        if ( directory != null ) {
            auditLogFile = KiePath.of(directory + '/' + filename);
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
