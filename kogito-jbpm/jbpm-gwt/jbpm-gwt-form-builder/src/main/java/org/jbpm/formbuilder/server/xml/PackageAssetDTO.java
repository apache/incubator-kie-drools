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

public class PackageAssetDTO {

    private String _binaryLink;
    private String _refLink;
    private String _sourceLink;
    private MetaDataDTO _metadata;
    
    public PackageAssetDTO() {
        // jaxb needs a default constructor
    }
    
    @XmlElement
    public String getBinaryLink() {
        return _binaryLink;
    }
    
    public void setBinaryLink(String binaryLink) {
        this._binaryLink = binaryLink;
    }
    
    @XmlElement
    public String getRefLink() {
        return _refLink;
    }
    
    public void setRefLink(String refLink) {
        this._refLink = refLink;
    }
    
    @XmlElement
    public String getSourceLink() {
        return _sourceLink;
    }
    
    public void setSourceLink(String sourceLink) {
        this._sourceLink = sourceLink;
    }
    
    @XmlElement
    public MetaDataDTO getMetadata() {
        return _metadata;
    }
    
    public void setMetadata(MetaDataDTO metadata) {
        this._metadata = metadata;
    }
}
