package org.jbpm.kie.services.impl.bpmn2;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.xml.ImportHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This handler adds classes imported (via the &gt;extensionElement&lt;) to the list
 * of referenced classes.
 */
public class ProcessGetImportHandler extends ImportHandler {

    private BPMN2DataServiceExtensionSemanticModule module;
    private ProcessDescriptionRepository repository;

    public ProcessGetImportHandler(BPMN2DataServiceExtensionSemanticModule module) {
        this.module = module;
        this.repository = module.getRepo();
    }

    public Object start( final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser )
            throws SAXException {
        // does checks
        super.start(uri, localName, attrs, parser);

        final String name = attrs.getValue("name");
        final String type = attrs.getValue("importType");
        final String location = attrs.getValue("location");
        final String namespace = attrs.getValue("namespace");

        if( type == null || location == null || namespace == null ) {
            String mainProcessId = module.getRepoHelper().getProcess().getId();
            ProcessDescRepoHelper repoHelper = repository.getProcessDesc(mainProcessId);
            if( name.contains(".") ) { 
                repoHelper.getReferencedClasses().add(name);
            } else { 
                repoHelper.getUnqualifiedClasses().add(name);
            }
        }

        return null;
    }

}
