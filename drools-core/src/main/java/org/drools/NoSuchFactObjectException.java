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
 * Indicates an attempt to retract, update or retrieve a fact object that is no
 * longer present.
 * 
 * @see FactHandle
 * @see WorkingMemory#assertObject
 * @see WorkingMemory#retractObject
 * @see WorkingMemory#getObject
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * 
 * @version $Id: NoSuchFactObjectException.java,v 1.3 2003/11/19 21:31:09 bob
 *          Exp $
 */
public class NoSuchFactObjectException extends FactException {
    /**
     * 
     */
    private static final long serialVersionUID = 400L;
    /** Invalid fact handle. */
    private final FactHandle  handle;

    /**
     * @see java.lang.Exception#Exception()
     * 
     * @param object
     *            The invalid fact object.
     */
    public NoSuchFactObjectException(final FactHandle handle) {
        super( createMessage( handle ) );
        this.handle = handle;
    }

    /**
     * @see java.lang.Exception#Exception()
     * 
     * @param object
     *            The invalid fact object.
     */
    public NoSuchFactObjectException(final FactHandle handle,
                                     final Throwable cause) {
        super( createMessage( handle ) );
        this.handle = handle;
    }

    /**
     * Retrieve the invalid <code>FactHandle</code>.
     * 
     * @return The invalid fact handle.
     */
    public FactHandle getFactHandle() {
        return this.handle;
    }

    private static String createMessage(final FactHandle handle) {
        return handle == null ? "null fact object" : "no such fact object for handle:" + handle.toExternalForm();
    }
}