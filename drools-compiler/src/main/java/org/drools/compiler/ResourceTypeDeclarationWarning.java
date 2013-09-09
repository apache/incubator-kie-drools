package org.drools.compiler;

import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.internal.InternalResource;

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
        return "Resource " + ((InternalResource) getResource()).getName() + " was created with type " + actualResourceType + " but is being added as " + declaredResourceType;
    }

    @Override
    public int[] getLines() {
        return new int[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
