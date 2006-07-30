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

package org.drools.reteoo;

import org.drools.common.DefaultFactHandle;

/**
 * CalculatedObjectHandle
 *
 * Created: 13/06/2006
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a> 
 *
 * @version $Id$
 */

public class CalculatedObjectHandle extends DefaultFactHandle {

    private static final long serialVersionUID = -8026531400924584912L;
    
    public CalculatedObjectHandle(Object object) {
        super(-1, object, 0);
    }

}
