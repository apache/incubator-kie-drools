/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.models.guided.dtable.shared.conversion;

/**
 * A message resulting from the conversion process
 */
public class ConversionMessage {

    private static final long serialVersionUID = 540L;

    private String message;

    private ConversionMessageType messageType;

    public ConversionMessage() {
    }

    public ConversionMessage( String message,
                              ConversionMessageType messageType ) {
        this.message = message;
        this.messageType = messageType;
    }

    public String getMessage() {
        return this.message;
    }

    public ConversionMessageType getMessageType() {
        return this.messageType;
    }

}