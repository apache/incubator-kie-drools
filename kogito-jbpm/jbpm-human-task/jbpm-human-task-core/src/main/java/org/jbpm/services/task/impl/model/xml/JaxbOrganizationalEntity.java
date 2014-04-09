package org.jbpm.services.task.impl.model.xml;

import static org.jbpm.services.task.impl.model.xml.AbstractJaxbTaskObject.unsupported;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.jbpm.services.task.impl.model.xml.AbstractJaxbTaskObject.GetterGroup;
import org.jbpm.services.task.impl.model.xml.AbstractJaxbTaskObject.GetterUser;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;

@XmlRootElement(name="organizational-entity")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbOrganizationalEntity implements OrganizationalEntity {

    @XmlElement
    @XmlSchemaType(name="string")
    private String id;
 
    @XmlElement
    private Type type;
    
    @XmlEnum
    public static enum Type {
        USER, GROUP;
    }
    
    public JaxbOrganizationalEntity() { 
        // JAXB default
    }
    
    public JaxbOrganizationalEntity(OrganizationalEntity orgEntity) { 
        this.id = orgEntity.getId();
        if( orgEntity instanceof User ) { 
            this.type = Type.USER;
        } else if (orgEntity instanceof Group ) { 
           this.type = Type.GROUP; 
        } else if(orgEntity instanceof JaxbOrganizationalEntity ) { 
            this.type = ((JaxbOrganizationalEntity) orgEntity).type;
        } else { 
            throw new IllegalArgumentException("Unknown type of organizational entity: " + orgEntity.getClass().getSimpleName());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        unsupported(OrganizationalEntity.class);      
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        unsupported(OrganizationalEntity.class);
    }
    
    public static List<JaxbOrganizationalEntity> convertListFromInterfaceToJaxbImpl(List<OrganizationalEntity> jaxbList) { 
        List<JaxbOrganizationalEntity> jaxbOrgEntList;
        if( jaxbList != null ) { 
            jaxbOrgEntList = new ArrayList<JaxbOrganizationalEntity>(jaxbList.size());
            for( OrganizationalEntity jaxb : jaxbList ) { 
                jaxbOrgEntList.add(new JaxbOrganizationalEntity(jaxb));
            }
        } else { 
            // it would be nice to use Collections.EMPTY_LIST here, but there's a possibility the list is being modified after this call
            jaxbOrgEntList = new ArrayList<JaxbOrganizationalEntity>();
        }
        return jaxbOrgEntList;
    }
    
    public static List<OrganizationalEntity> convertListFromJaxbImplToInterface(List<JaxbOrganizationalEntity> jaxbList) { 
        List<OrganizationalEntity> orgEntList;
        if( jaxbList != null ) {  
            orgEntList = new ArrayList<OrganizationalEntity>(jaxbList.size());
            for( JaxbOrganizationalEntity jaxb : jaxbList ) { 
                orgEntList.add(jaxb.createImplInstance());
            }
        } else { 
            // it would be nice to use Collections.EMPTY_LIST here, but there's a possibility the list is being modified after this call
            orgEntList = new ArrayList<OrganizationalEntity>();
        }
        return orgEntList;
    }
    
    private OrganizationalEntity createImplInstance() { 
        switch(type) { 
        case GROUP:
            return new GetterGroup(this.id);
        case USER:
            return new GetterUser(this.id);
        default:
            throw new IllegalStateException("Unknown organizational type: " + type);
        }
    }
}
