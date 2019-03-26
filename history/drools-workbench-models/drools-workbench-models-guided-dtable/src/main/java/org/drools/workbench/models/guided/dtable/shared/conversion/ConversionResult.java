/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.conversion;

import java.util.ArrayList;
import java.util.List;

/**
 * A single result of a conversion process
 */
public class ConversionResult {

    private static final long serialVersionUID = 540L;

    private List<ConversionAsset> newAssets = new ArrayList<ConversionAsset>();

    private List<ConversionMessage> messages = new ArrayList<ConversionMessage>();

    public boolean isConverted() {
        for ( ConversionMessage message : messages ) {
            if ( message.getMessageType() == ConversionMessageType.ERROR ) {
                return false;
            }
        }
        return true;
    }

    public void addNewAsset( ConversionAsset newAsset ) {
        this.newAssets.add( newAsset );
    }

    public List<ConversionAsset> getNewAssets() {
        return this.newAssets;
    }

    public void addMessage( String message,
                            ConversionMessageType messageType ) {
        messages.add( new ConversionMessage( message,
                                             messageType ) );
    }

    /**
     * Get all messages of all types
     * @return
     */
    public List<ConversionMessage> getMessages() {
        return messages;
    }

    /**
     * Get all messages of a particular type
     * @param messageType
     * @return
     */
    public List<ConversionMessage> getMessages( ConversionMessageType messageType ) {
        List<ConversionMessage> messages = new ArrayList<ConversionMessage>();
        for ( ConversionMessage message : this.messages ) {
            if ( message.getMessageType() == messageType ) {
                messages.add( message );
            }
        }
        return messages;
    }

}
