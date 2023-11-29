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
package org.drools.commands.runtime.rule;

import jakarta.xml.bind.annotation.XmlRegistry;

import org.drools.commands.runtime.rule.ModifyCommand.SetterImpl;

@XmlRegistry
public class ObjectFactory {

    public FireAllRulesCommand createFireAllRulesCommand() {
        return new FireAllRulesCommand();
    }
    
    public GetObjectCommand createGetObjectCommand() {
        return new GetObjectCommand();
    }

    public GetObjectsCommand createGetObjectsCommand() {
        return new GetObjectsCommand();
    }
    
    public InsertElementsCommand createInsertElementsCommand() {
        return new InsertElementsCommand();
    }
    
    public InsertObjectCommand createInsertObjectCommand() {
        return new InsertObjectCommand();
    }

    public InsertObjectInEntryPointCommand createInsertObjectInEntryPointCommand() {
        return new InsertObjectInEntryPointCommand();
    }
    
    public ModifyCommand createModifyCommand() {
        return new ModifyCommand();
    }

    public SetterImpl createModifyCommand$SetterImpl() {
        return new SetterImpl();
    }
    
    public QueryCommand createQueryCommand() {
        return new QueryCommand();
    }
    
    public DeleteCommand createRetractCommand() {
        return new DeleteCommand();
    }

}
