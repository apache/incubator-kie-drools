package org.drools.compiler.xml.processes;

import java.util.HashSet;

import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.TypeObject;
import org.drools.process.core.ValueObject;
import org.drools.process.core.Work;
import org.drools.process.core.datatype.DataType;
import org.drools.process.core.impl.ParameterDefinitionImpl;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ParameterHandler extends BaseAbstractHandler implements Handler {
	
    public ParameterHandler() {
        if ((this.validParents == null) && (this.validPeers == null)) {
            this.validParents = new HashSet<Class<?>>();
            this.validParents.add(Work.class);
            this.validPeers = new HashSet<Class<?>>();         
            this.validPeers.add(null);            
            this.allowNesting = false;
        }
    }
    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        final String name = attrs.getValue("name");
        emptyAttributeCheck(localName, "name", name, parser);
        Work work = (Work) parser.getParent();
        ParameterDefinition parameterDefinition = new ParameterDefinitionImpl();
        parameterDefinition.setName(name);
        work.addParameterDefinition(parameterDefinition);
        return new ParameterWrapper(parameterDefinition, work);
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return null;
    }
    
    public Class<?> generateNodeFor() {
        return ParameterWrapper.class;
    }
    
    public class ParameterWrapper implements TypeObject, ValueObject {
    	private Work work;
    	private ParameterDefinition parameterDefinition;
    	public ParameterWrapper(ParameterDefinition parameterDefinition, Work work) {
    		this.work = work;
    		this.parameterDefinition = parameterDefinition;
    	}
		public DataType getType() {
			return parameterDefinition.getType();
		}
		public void setType(DataType type) {
			parameterDefinition.setType(type);
		}
		public Object getValue() {
			return work.getParameter(parameterDefinition.getName());
		}
		public void setValue(Object value) {
			work.setParameter(parameterDefinition.getName(), value);
		}
    }

}
