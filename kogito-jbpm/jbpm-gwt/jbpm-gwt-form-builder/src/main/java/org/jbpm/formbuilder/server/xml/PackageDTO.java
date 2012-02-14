/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.formbuilder.server.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class PackageDTO {

    private String _title;
    
    private List<String> _assets = new ArrayList<String>();
    private MetaDataDTO _metadata;

    @XmlElement
    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        this._title = title;
    }

    @XmlElement
    public List<String> getAssets() {
        if (_assets == null) {
             _assets = new ArrayList<String>(); 
        }
        return _assets;
    }

    public void setAssets(List<String> assets) {
        this._assets = assets;
    }
    
    @XmlElement
    public MetaDataDTO getMetadata() {
        return _metadata;
    }
    
    public void setMetadata(MetaDataDTO metadata) {
        this._metadata = metadata;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_assets == null) ? 0 : _assets.hashCode());
        result = prime * result
                + ((_metadata == null) ? 0 : _metadata.hashCode());
        result = prime * result + ((_title == null) ? 0 : _title.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PackageDTO other = (PackageDTO) obj;
        if (_assets == null) {
            if (other._assets != null)
                return false;
        } else if (!_assets.equals(other._assets))
            return false;
        if (_metadata == null) {
            if (other._metadata != null)
                return false;
        } else if (!_metadata.equals(other._metadata))
            return false;
        if (_title == null) {
            if (other._title != null)
                return false;
        } else if (!_title.equals(other._title))
            return false;
        return true;
    }
}
