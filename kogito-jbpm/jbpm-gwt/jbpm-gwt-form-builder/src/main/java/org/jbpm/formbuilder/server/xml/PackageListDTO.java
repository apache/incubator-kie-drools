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

@XmlRootElement(name = "packages")
public class PackageListDTO {
    
    public static final Class<?>[] RELATED_CLASSES = new Class<?>[] { PackageListDTO.class, PackageDTO.class, MetaDataDTO.class };

    private List<PackageDTO> _package = new ArrayList<PackageDTO>();

    @XmlElement
    public List<PackageDTO> getPackage() {
        if (_package == null) {
            _package = new ArrayList<PackageDTO>();
        }
        return _package;
    }
    
    public PackageDTO getSelectedPackage(String packageName) {
        for (PackageDTO pkg : getPackage()) {
            if (pkg.getTitle().equals(packageName)) {
                return pkg;
            }
        }
        return null;
    }

    public void setPackage(List<PackageDTO> _package) {
        this._package = _package;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((_package == null) ? 0 : _package.hashCode());
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
        PackageListDTO other = (PackageListDTO) obj;
        if (_package == null) {
            if (other._package != null)
                return false;
        } else if (!_package.equals(other._package))
            return false;
        return true;
    }
}
