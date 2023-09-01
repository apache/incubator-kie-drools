package org.kie.dmn.model.v1_3;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.AuthorityRequirement;
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.KnowledgeSource;


public class TKnowledgeSource extends TDRGElement implements KnowledgeSource {

    protected List<AuthorityRequirement> authorityRequirement;
    protected String type;
    protected DMNElementReference owner;
    protected String locationURI;

    @Override
    public List<AuthorityRequirement> getAuthorityRequirement() {
        if (authorityRequirement == null) {
            authorityRequirement = new ArrayList<>();
        }
        return this.authorityRequirement;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String value) {
        this.type = value;
    }

    @Override
    public DMNElementReference getOwner() {
        return owner;
    }

    @Override
    public void setOwner(DMNElementReference value) {
        this.owner = value;
    }

    @Override
    public String getLocationURI() {
        return locationURI;
    }

    @Override
    public void setLocationURI(String value) {
        this.locationURI = value;
    }

}
