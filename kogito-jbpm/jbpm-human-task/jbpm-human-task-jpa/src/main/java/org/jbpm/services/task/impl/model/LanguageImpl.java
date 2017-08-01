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

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LanguageImpl implements org.kie.internal.task.api.model.Language {

    @Column(nullable=false)
    private String mapkey;

    public String getMapkey() {
        return mapkey;
    }

    public void setMapkey(String language) {
        this.mapkey = language;
    }
 
    public LanguageImpl() { 
        
    }
    
    public LanguageImpl(String lang) { 
        this.mapkey = lang;
    }

    @Override
    public int hashCode() {
        return this.mapkey == null ? 0 : this.mapkey.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == null ) { 
           if( this.mapkey == null ) { 
               return true;
           }
           return false;
        }
        else if( obj instanceof String ) { 
            return obj.equals(this.mapkey);
        }
        else if( obj instanceof LanguageImpl ) { 
            LanguageImpl other = (LanguageImpl) obj;
            if( this.mapkey == null ) { 
                return (other).mapkey == null;
            }
            else { 
                return this.mapkey.equals(other.mapkey);
            }
        }
        else { 
            return false;
        }
    }

    @Override
    public String toString() {
        return mapkey == null ? null : mapkey.toString();
    }
    
}
