package org.kie.efesto.common.core.identifiers.componentroots;

import org.kie.efesto.common.api.identifiers.Id;
import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.LocalUriId;

public class LocalComponentIdFoo extends LocalUriId implements Id {

    public static final String PREFIX = "foo";

    private final String fileName;
    private final String name;
    private final String secondName;


    public LocalComponentIdFoo(String fileName, String name, String secondName) {
        super(LocalUri.Root.append(PREFIX).append(fileName).append(name).append(secondName));
        this.fileName = fileName;
        this.name = name;
        this.secondName = secondName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public String getSecondName() {
        return secondName;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

}
