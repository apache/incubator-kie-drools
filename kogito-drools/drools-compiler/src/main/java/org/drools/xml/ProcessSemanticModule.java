package org.drools.xml;

import org.drools.xml.processes.ActionNodeHandler;
import org.drools.xml.processes.ConnectionHandler;
import org.drools.xml.processes.EndNodeHandler;
import org.drools.xml.processes.GlobalHandler;
import org.drools.xml.processes.ImportHandler;
import org.drools.xml.processes.ProcessHandler;
import org.drools.xml.processes.StartNodeHandler;

public class ProcessSemanticModule extends DefaultSemanticModule implements SemanticModule {    
    public ProcessSemanticModule() {
        super ( "http://drools.org/drools-4.0/process" );

        addHandler( "process",
                           new ProcessHandler() );
        addHandler( "start",
                           new StartNodeHandler() );
        addHandler( "end",
                           new EndNodeHandler() );
        addHandler( "action",
                           new ActionNodeHandler() );
        addHandler( "connection",
                           new ConnectionHandler() );
        addHandler( "import",
                           new ImportHandler() );
        addHandler( "global",
                           new GlobalHandler() );        
    }
}
