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
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "files")
public class FileListDTO {

    public static final Class<?>[] RELATED_CLASSES = new Class<?>[] { FileListDTO.class };
    
    private List<String> _file = new ArrayList<String>();

    public FileListDTO() {
        // jaxb needs a default constructor
    }
    
    public FileListDTO(List<String> file) {
        super();
        if (file != null) {
            for (String f : file) {
                this._file.add(f);
            }
        }
    }

    @XmlElement
    public List<String> getFile() {
        return _file;
    }

    public void setFile(List<String> file) {
        this._file = file;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_file == null) ? 0 : _file.hashCode());
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
        FileListDTO other = (FileListDTO) obj;
        if (_file == null) {
            if (other._file != null)
                return false;
        } else if (!_file.equals(other._file))
            return false;
        return true;
    }
}
