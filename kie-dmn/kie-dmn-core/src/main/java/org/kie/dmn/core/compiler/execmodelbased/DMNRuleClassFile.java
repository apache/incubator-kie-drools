package org.kie.dmn.core.compiler.execmodelbased;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.core.util.IoUtils;

public class DMNRuleClassFile {

    public static List<String> getClassFile(ClassLoader classLoader) {
        List<String> modelFiles;
        try {
            InputStream resourceAsStream = classLoader.getResourceAsStream("META-INF/kie/dmn");
            if (resourceAsStream != null) {
                String allClasses = new String(IoUtils.readBytesFromInputStream(resourceAsStream));
                modelFiles = Arrays.asList(allClasses.split("\\n"));
            } else {
                modelFiles = new ArrayList<>();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return modelFiles;
    }
}
