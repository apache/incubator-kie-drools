/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.task;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.jbpm.task.utils.CollectionUtils;

@Entity
@SequenceGenerator(name="deadlineIdSeq", sequenceName="DEADLINE_ID_SEQ", allocationSize=1)
public class Deadline implements Externalizable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="deadlineIdSeq")
    private long id;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "Deadline_Documentation_Id", nullable = true)
    private List<I18NText> documentation = Collections.emptyList();
    @Column(name = "deadline_date")
    private Date date;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "Deadline_Escalation_Id", nullable = true)
    private List<Escalation> escalations = Collections.emptyList();
    
    @Basic
    private Short escalated = 0;

    public Boolean isEscalated() {
        if (escalated == null) {
            return null;
        }
        return (escalated == 1) ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setEscalated(Boolean escalated) {
        if (escalated == null) {
            this.escalated = null;
        } else {
            this.escalated = (escalated == true) ? new Short("1") : new Short("0");
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(id);

        if (date != null) {
            out.writeBoolean(true);
            out.writeLong(date.getTime());
        } else {
            out.writeBoolean(false);
        }
        CollectionUtils.writeI18NTextList(documentation, out);
        CollectionUtils.writeEscalationList(escalations, out);

        out.writeShort(escalated);
        
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        id = in.readLong();

        if (in.readBoolean()) {
            date = new Date(in.readLong());
        }
        documentation = CollectionUtils.readI18NTextList(in);
        escalations = CollectionUtils.readEscalationList(in);

        escalated = in.readShort();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<I18NText> getDocumentation() {
        return documentation;
    }

    public void setDocumentation(List<I18NText> documentation) {
        this.documentation = documentation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Escalation> getEscalations() {
        return escalations;
    }

    public void setEscalations(List<Escalation> escalations) {
        this.escalations = escalations;
    }

   
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + CollectionUtils.hashCode(documentation);
        result = prime * result + CollectionUtils.hashCode(escalations);
        result = prime * result + (isEscalated() ? 1231 : 1237);
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Deadline)) {
            return false;
        }
        Deadline other = (Deadline) obj;

        if (date == null) {
            if (other.date != null) {
                return false;
            }
        } else if (date.getTime() != other.date.getTime()) {
            return false;
        }

        if (isEscalated() != other.isEscalated()) {
            return false;
        }

        return CollectionUtils.equals(documentation, other.documentation) && CollectionUtils.equals(escalations, other.escalations);
    }
}
