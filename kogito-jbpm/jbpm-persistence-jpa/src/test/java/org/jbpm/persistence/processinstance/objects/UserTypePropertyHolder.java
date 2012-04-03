/*
 * @(#)UserTypePropertyHolder.java     8 Apr 2009
 * 
 * Copyright © 2009 Andrew Phillips.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jbpm.persistence.processinstance.objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

/**
 * An object with a custom typed property.
 * 
 * @author anphilli
 * @since 8 Apr 2009
 *
 */
@Entity
public class UserTypePropertyHolder {
    
    @Id
    @GeneratedValue
    private Integer id;
    
    @Type(type = "org.jbpm.persistence.processinstance.BlobUserType")
    private byte[] blobInfo;
    
    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the userTypedProperty
     */
    public byte [] getBlobInfo() {
        return blobInfo;
    }
    
    public void setBlobInfo(byte[] blobInfo) {
        this.blobInfo = blobInfo;
    }

}
