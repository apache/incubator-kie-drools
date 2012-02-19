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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "asset")
public class AssetDTO {

    public static final Class<?>[] RELATED_CLASSES = new Class<?>[] { AssetDTO.class, MetaDataDTO.class };
    
    private MetaDataDTO _metadata;
    private String _sourceLink;

    @XmlElement
    public MetaDataDTO getMetadata() {
        return _metadata;
    }

    public void setMetadata(MetaDataDTO metadata) {
        this._metadata = metadata;
    }

    @XmlElement
    public String getSourceLink() {
        return _sourceLink;
    }

    public void setSourceLink(String sourceLink) {
        this._sourceLink = sourceLink;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((_metadata == null) ? 0 : _metadata.hashCode());
        result = prime * result
                + ((_sourceLink == null) ? 0 : _sourceLink.hashCode());
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
        AssetDTO other = (AssetDTO) obj;
        if (_metadata == null) {
            if (other._metadata != null)
                return false;
        } else if (!_metadata.equals(other._metadata))
            return false;
        if (_sourceLink == null) {
            if (other._sourceLink != null)
                return false;
        } else if (!_sourceLink.equals(other._sourceLink))
            return false;
        return true;
    }
}
