/*
 * Copyright 2015 JBoss Inc
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

package org.drools.compiler.compiler;

import org.drools.core.io.internal.InternalResource;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

public class ResourceTypeDeclarationWarning extends DroolsWarning {

    private ResourceType declaredResourceType;
    private ResourceType actualResourceType;

    public ResourceTypeDeclarationWarning( Resource resource, ResourceType declaredResourceType, ResourceType actualResourceType ) {
        super( resource );
        this.declaredResourceType = declaredResourceType;
        this.actualResourceType = actualResourceType;
    }

    public ResourceType getDeclaredResourceType() {
        return declaredResourceType;
    }

    public void setDeclaredResourceType( ResourceType declaredResourceType ) {
        this.declaredResourceType = declaredResourceType;
    }

    public ResourceType getActualResourceType() {
        return actualResourceType;
    }

    public void setActualResourceType( ResourceType actualResourceType ) {
        this.actualResourceType = actualResourceType;
    }

    @Override
    public String getMessage() {
        return "Resource " + getResource().getSourcePath() + " was created with type " + actualResourceType + " but is being added as " + declaredResourceType;
    }

    @Override
    public int[] getLines() {
        return new int[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }
}