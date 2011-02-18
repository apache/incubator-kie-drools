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

package org.drools.command.runtime.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.command.Context;
import org.drools.command.Setter;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.xml.jaxb.util.JaxbListAdapter;
import org.drools.xml.jaxb.util.JaxbListWrapper;
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


    @XmlJavaTypeAdapter(JaxbSetterAdapter.class)
    @XmlElement
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
        WorkingMemoryEntryPoint wmep = ksession.getWorkingMemoryEntryPoint( ((InternalFactHandle)handle).getEntryPoint().getEntryPointId() );
        
        Object object = wmep.getObject( this.handle );
        MVEL.eval( getMvelExpr(),
                   object );

        wmep.update( handle,
                        object );
        return object;
    }

    public FactHandle getFactHandle() {
        return this.handle;
    }
    
    @XmlAttribute(name="fact-handle", required=true)
    public void setFactHandleFromString(String factHandleId) {
        handle = new DefaultFactHandle(factHandleId);
    }
    
    public String getFactHandleFromString() {
        return handle.toExternalForm();
    }

    public List<Setter> getSetters() {
        if ( this.setters == null ) {
            this.setters = new ArrayList<Setter>();
        }
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
    
//    public static class JaxbSetterWrapper<SetterImpl> extends ArrayList<SetterImpl> {
//
//        public JaxbSetterWrapper() {
//            super();
//        }
//
//        public JaxbSetterWrapper(Collection<SetterImpl> c) {
//            super(c);
//        }
//
//        public JaxbSetterWrapper(int initialCapacity) {
//            super(initialCapacity);
//        }
//
//        @XmlElement(name="setters")
//        public List<SetterImpl> getElements() {
//            return this;
//        }
//        
//        public void setElements(List<SetterImpl> elems) {
//            clear();
//            if (elems != null) {
//                addAll(elems);
//            }
//        }
//    }
    
    public static class JaxbSetterAdapter extends XmlAdapter<SetterImpl[], List<SetterImpl>> {

        @Override
        public SetterImpl[] marshal(List<SetterImpl> v) throws Exception {
            return v.toArray( new SetterImpl[ v.size() ] );
        }

        @Override
        public List<SetterImpl> unmarshal(SetterImpl[] v) throws Exception {
            return Arrays.asList( v );
        }

    }
}
