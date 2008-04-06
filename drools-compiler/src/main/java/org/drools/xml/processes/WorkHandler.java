package org.drools.xml.processes;

import java.util.HashSet;

import org.drools.process.core.Work;
import org.drools.process.core.impl.WorkImpl;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class WorkHandler extends BaseAbstractHandler implements Handler {
    
    public WorkHandler() {
        if ((this.validParents == null) && (this.validPeers == null)) {
            this.validParents = new HashSet();
            this.validParents.add(WorkItemNode.class);

            this.validPeers = new HashSet();
            this.validPeers.add(null);

            this.allowNesting = false;
        }
    }
    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startConfiguration(localName, attrs);
        WorkItemNode workItemNode = (WorkItemNode) parser.getParent();
        final String name = attrs.getValue("name");
        emptyAttributeCheck(localName, "name", name, parser);
        Work work = new WorkImpl();
        work.setName(name);
        workItemNode.setWork(work);
        return work;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endConfiguration();
        return null;
    }

    public Class generateNodeFor() {
        return Work.class;
    }    

}
