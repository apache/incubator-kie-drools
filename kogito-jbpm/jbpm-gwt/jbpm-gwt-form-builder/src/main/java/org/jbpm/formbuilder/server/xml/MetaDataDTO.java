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

public class MetaDataDTO {

    private String _title;
    private String _uuid;
    private String _format;

    @XmlElement
    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        this._title = title;
    }

    @XmlElement
    public String getUuid() {
        return _uuid;
    }

    public void setUuid(String uuid) {
        this._uuid = uuid;
    }

    @XmlElement
    public String getFormat() {
        return _format;
    }

    public void setFormat(String format) {
        this._format = format;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_format == null) ? 0 : _format.hashCode());
        result = prime * result + ((_title == null) ? 0 : _title.hashCode());
        result = prime * result + ((_uuid == null) ? 0 : _uuid.hashCode());
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
        MetaDataDTO other = (MetaDataDTO) obj;
        if (_format == null) {
            if (other._format != null)
                return false;
        } else if (!_format.equals(other._format))
            return false;
        if (_title == null) {
            if (other._title != null)
                return false;
        } else if (!_title.equals(other._title))
            return false;
        if (_uuid == null) {
            if (other._uuid != null)
                return false;
        } else if (!_uuid.equals(other._uuid))
            return false;
        return true;
    }
}
