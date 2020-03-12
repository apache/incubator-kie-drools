package org.kie.dmn.typesafe;

import org.kie.dmn.api.core.DMNModel;

public class DMNInputSetGenerator {

    private DMNModel dmnModel;

    public DMNInputSetGenerator(DMNModel dmnModel) {
        this.dmnModel = dmnModel;
    }

    public String getType(String tPerson) {

        return "public class TPerson {\n" +
                "\n" +
                "    private String name;\n" +
                "\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "}\n";
    }
}
