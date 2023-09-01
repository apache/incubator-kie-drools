package org.kie.dmn.feel.parser.feel11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

public class FEELTestRigExample {

    public static void main(String[] args) throws Exception {
        try (Scanner sysin = new Scanner(System.in)) {

            final String FEEL_EXPRESSION = "sum(my variable+2)";

            // Set FEEL variables name+type in scope:
            Map<String, Type> variablesInScopeTypes = new HashMap<>();
            variablesInScopeTypes.put("my variable", BuiltInType.UNKNOWN);

            FEELTestRig feelTestRig = new FEELTestRig(new String[]{"FEEL_1_1", "compilation_unit", "-tree", "-gui"}, variablesInScopeTypes, Collections.emptyMap());
            feelTestRig.process(FEEL_EXPRESSION);

            System.out.println("Press any key to continue...");
            sysin.nextLine();
        }
    }
}
