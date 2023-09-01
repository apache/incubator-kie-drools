package org.kie.pmml.api.identifiers;

import org.kie.efesto.common.api.identifiers.LocalUri;

public class LocalComponentIdPmml extends AbstractModelLocalUriIdPmml {
    public static final String PREFIX = "pmml";
    private static final long serialVersionUID = 8621199867598971641L;


    public LocalComponentIdPmml(String fileName, String name) {
        super(LocalUri.Root.append(PREFIX).append(fileName).append(name), fileName, name);
    }

}
