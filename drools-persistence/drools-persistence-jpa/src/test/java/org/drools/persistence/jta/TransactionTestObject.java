/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.persistence.jta;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;

/**
 * This class is used to test transactions. 
 * It specifically has a 
 * 
 *
 */
@Entity
@SequenceGenerator(name="txTestIdSeq", sequenceName="TXTESTOBJ_ID_SEQ")
public class TransactionTestObject implements Serializable {

    private static final long serialVersionUID = 8991032325499307158L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="txTestIdSeq")
    @Column(name="ID")
    private Long id;
    
    private String name;

	@OneToOne
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
    
}
