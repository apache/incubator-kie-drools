/**
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier.report.components;

public class MessageType {
    public static final MessageType NOT_SPECIFIED     = new MessageType( "NOT_SPECIFIED" );
    public static final MessageType RANGE_CHECK       = new MessageType( "RANGE_CHECK" );
    public static final MessageType MISSING_EQUALITY  = new MessageType( "MISSING_EQUALITY" );
    public static final MessageType REDUNDANCY        = new MessageType( "REDUNDANCY" );
    public static final MessageType SUBSUMPTION       = new MessageType( "SUBSUMPTION" );
    public static final MessageType MISSING_COMPONENT = new MessageType( "MISSING_COMPONENT" );
    public static final MessageType OPTIMISATION      = new MessageType( "OPTIMISATION" );
    public static final MessageType INCOHERENCE       = new MessageType( "INCOHERENCE" );
    public static final MessageType OVERLAP           = new MessageType( "OVERLAP" );
    public static final MessageType ALWAYS_FALSE      = new MessageType( "ALWAYS_FALSE" );
    public static final MessageType ALWAYS_TRUE       = new MessageType( "ALWAYS_TRUE" );
    public static final MessageType EQUIVALANCE       = new MessageType( "EQUIVALANCE" );

    public final String             type;

    public MessageType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}
