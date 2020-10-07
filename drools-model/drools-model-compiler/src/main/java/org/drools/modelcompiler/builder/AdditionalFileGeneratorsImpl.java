package org.drools.modelcompiler.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AdditionalFileGeneratorsImpl implements Consumer<AdditionalFileGenerator>,
                                                     AdditionalFileGenerators {

    List<AdditionalFileGenerator> children = new ArrayList<>();

    @Override
    public List<AdditionalFileGenerator> getChildren() {
        return children;
    }

    @Override
    public void accept(AdditionalFileGenerator additionalFileGenerator) {
        children.add(additionalFileGenerator);
    }
}
