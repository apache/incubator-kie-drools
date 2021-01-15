package org.kie.dmn.feel.gwt.functions.rebind;

import org.junit.Test;

public class MethodTemplatesTest {

    @Test
    public void name() {
        for (String s : new MethodTemplates().getAll()) {
            System.out.println(s);
        }
    }
}