package org.drools.command.runtime.rule;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.command.Context;
import org.drools.command.Setter;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.DisconnectedFactHandle;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.mvel2.MVEL;

@XmlAccessorType(XmlAccessType.NONE)
public class ModifyCommand
    implements
    GenericCommand<Object> {

    /**
     * if this is true, modify can be any MVEL expressions. If false, it will only allow literal values.
     * (false should be use when taking input from an untrusted source, such as a web service).
     */
    public static boolean ALLOW_MODIFY_EXPRESSIONS = true;


    private FactHandle       handle;
    @XmlAnyElement
    private List<Setter> setters;

    public ModifyCommand() {
    }
    
    public ModifyCommand(FactHandle handle,
                         List<Setter> setters) {
        this.handle = handle;
        this.setters = setters;
    }

    public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        Object object = ksession.getObject( this.handle );
        MVEL.eval( getMvelExpr(),
                   object );

        ksession.update( handle,
                        object );
        return object;
    }

    public FactHandle getFactHandle() {
        return this.handle;
    }
    
    @XmlAttribute(name="fact-handle", required=true)
	public void setFactHandleFromString(String factHandleId) {
		handle = new DisconnectedFactHandle(factHandleId);
	}
    
    public String getFactHandleFromString() {
    	return handle.toExternalForm();
	}

    public List<Setter> getSetters() {
        return this.setters;
    }
    
    public void setSetters(List<Setter> setters) {
    	this.setters = setters;
    }

    private String getMvelExpr() {
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append( "with (this) {\n" );
        int i = 0;
        for ( Setter setter : this.setters ) {
            if ( i++ > 0 ) {
                sbuilder.append( "," );
            }
            if (ALLOW_MODIFY_EXPRESSIONS) {
                sbuilder.append( setter.getAccessor() + " = " + setter.getValue() + "\n" );
            } else {
                sbuilder.append( setter.getAccessor() + " = '" + setter.getValue().replace("\"", "") + "'\n" );
            }
        }
        sbuilder.append( "}" );
        return sbuilder.toString();
    }

    public String toString() {
        return "modify() " + getMvelExpr();
    }

    @XmlRootElement(name="set")
    public static class SetterImpl
        implements
        Setter {
    	@XmlAttribute
        private String accessor;
    	@XmlAttribute
        private String value;
    	
    	public SetterImpl() {
		}

        public SetterImpl(String accessor,
                          String value) {
            this.accessor = accessor;
            this.value = value;
        }

        public String getAccessor() {
            return accessor;
        }

        public String getValue() {
            return value;
        }

    }
}
