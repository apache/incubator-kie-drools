/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.impl.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.jbpm.services.task.utils.CollectionUtils;
import org.kie.internal.task.api.model.Deadline;

@Embeddable
public class DeadlinesImpl implements org.kie.internal.task.api.model.Deadlines {    
    @OneToMany(cascade = CascadeType.ALL, targetEntity=DeadlineImpl.class)
    @JoinColumn(name = "Deadlines_StartDeadLine_Id", nullable = true)    
    private List<Deadline> startDeadlines = Collections.emptyList();
    
    @OneToMany(cascade = CascadeType.ALL, targetEntity=DeadlineImpl.class)
    @JoinColumn(name = "Deadlines_EndDeadLine_Id", nullable = true)    
    private List<Deadline> endDeadlines  = Collections.emptyList();
    
    public void writeExternal(ObjectOutput out) throws IOException {
        CollectionUtils.writeDeadlineList( startDeadlines, out );
        CollectionUtils.writeDeadlineList( endDeadlines, out );       
    } 
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        startDeadlines = CollectionUtils.readDeadlinesList( in );
        endDeadlines = CollectionUtils.readDeadlinesList( in );        
    }        
      
    public List<Deadline> getStartDeadlines() {
        return startDeadlines;
    }
    
    public void setStartDeadlines(List<Deadline> startDeadlines) {
        this.startDeadlines = startDeadlines;
    }
    
    public List<Deadline> getEndDeadlines() {
        return endDeadlines;
    }
    
    public void setEndDeadlines(List<Deadline> endDeadlines) {
        this.endDeadlines = endDeadlines;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + CollectionUtils.hashCode( endDeadlines );
        result = prime * result + CollectionUtils.hashCode( startDeadlines );
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof DeadlinesImpl) ) return false;
        DeadlinesImpl other = (DeadlinesImpl) obj;       
        
        return CollectionUtils.equals( endDeadlines, other.endDeadlines ) && CollectionUtils.equals( startDeadlines, other.startDeadlines );
    }
                   
    
}
