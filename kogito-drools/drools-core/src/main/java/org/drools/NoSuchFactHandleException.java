package org.drools;

/*
 * Copyright 2005 JBoss Inc
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

/**
 * Indicates an attempt to retract, modify or retrieve a fact object that is no
 * longer present.
 * 
 * @see FactHandle
 * @see WorkingMemory#getFactHandle
 * 
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 * 
 * @version $Id: NoSuchFactObjectException.java,v 1.3 2003/11/19 21:31:09 bob
 *          Exp $
 */
public class NoSuchFactHandleException extends FactException {
    /**
     * 
     */
    private static final long serialVersionUID = -4900120393032700935L;
    /** Invalid fact object. */
    private final Object      object;

    /**
     * @see java.lang.Exception#Exception()
     * 
     * @param object
     *            The invalid fact object.
     */
    public NoSuchFactHandleException(final Object object) {
        super( createMessage( object ) );
        this.object = object;
    }

    /**
     * @see java.lang.Exception#Exception(Throwable cause)
     * 
     * @param object
     *            The invalid fact object.
     */
    public NoSuchFactHandleException(final Object object,
                                     final Throwable cause) {
        super( createMessage( object ),
               cause );
        this.object = object;
    }

    /**
     * Retrieve the invalid Object.
     * 
     * @return The invalid fact object.
     */
    public Object getObject() {
        return this.object;
    }

    private static String createMessage(final Object object) {
        return object == null ? "null fact object" : "no such fact handle for object:" + object;
    }
}