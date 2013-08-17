/*
 * Copyright 2011 Red Hat Inc.
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
package org.drools.persistence.jta;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;

/**
 * This class is used to test transactions. 
 * It specifically has a 
 * 
 *
 */
@Entity
@Indexed
public class TransactionTestObject implements Serializable {

    private static final long serialVersionUID = 8991032325499307158L;

    @Id @DocumentId @Field
    @Column(name="ID")
    private Long id;
    @Field
    private String name;

    @Field @FieldBridge(impl=TransactionTestObjectBridge.class)
	private TransactionTestObject subObject;
	
    public TransactionTestObject(){}

    public Long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setSubObject(TransactionTestObject subObject) {
        if( subObject == this ) { 
            // no-op
            return;
        }
        this.subObject = subObject;
    }

    public TransactionTestObject getSubObject() {
        return subObject;
    }
    
    public void setId(Long id) {
		this.id = id;
	}
}
