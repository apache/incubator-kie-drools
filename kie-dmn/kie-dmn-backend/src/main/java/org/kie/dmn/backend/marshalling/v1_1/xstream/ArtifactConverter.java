package org.kie.dmn.backend.marshalling.v1_1.xstream;

import com.thoughtworks.xstream.XStream;

public abstract class ArtifactConverter extends DMNElementConverter {

    public ArtifactConverter(XStream xstream) {
        super(xstream);
    }

}
