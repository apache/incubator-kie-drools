/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.workitem.email;

public class Email {
	
    private Message    message;
    private Connection connection;

    public String[] getUsedIdentigiers() {
        return null;
    }

    public Email() {
        this.message = new Message();
        this.connection = new Connection();
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connection == null) ? 0 : connection.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final Email other = (Email) obj;
        if ( connection == null ) {
            if ( other.connection != null ) return false;
        } else if ( !connection.equals( other.connection ) ) return false;
        if ( message == null ) {
            if ( other.message != null ) return false;
        } else if ( !message.equals( other.message ) ) return false;
        return true;
    }

}
