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

package org.drools.compiler.compiler.io;

import java.util.Collection;


public interface Folder extends Resource {
    String getName();
    
    File getFile(String name);
    
    boolean exists();
    
    boolean create();
    
    Folder getFolder(String name);
    
    Path getPath();
    
    Folder getParent();
    
    Collection<? extends Resource> getMembers();
}
