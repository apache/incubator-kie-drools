package org.kie.dmn.model.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.AuthorityRequirement;
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.KnowledgeSource;

public class TKnowledgeSource extends TDRGElement implements KnowledgeSource {

    private List<AuthorityRequirement> authorityRequirement;
    private String type;
    private DMNElementReference owner;
    private String locationURI;

    @Override
    public List<AuthorityRequirement> getAuthorityRequirement() {
        if ( authorityRequirement == null ) {
            authorityRequirement = new ArrayList<>();
        }
        return this.authorityRequirement;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType( final String value ) {
        this.type = value;
    }

    @Override
    public DMNElementReference getOwner() {
        return owner;
    }

    @Override
    public void setOwner(final DMNElementReference value) {
        this.owner = value;
    }

    @Override
    public String getLocationURI() {
        return locationURI;
    }

    @Override
    public void setLocationURI( final String value ) {
        this.locationURI = value;
    }

}
