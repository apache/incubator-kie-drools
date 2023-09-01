package org.kie.pmml.compiler.api.testutils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dmg.pmml.PMML;
import org.drools.util.FileUtils;

public class TestUtils {

    /**
     * Load a <code>PMML</code> from the given <b>file</b>
     * @param fileName
     * @return
     * @throws IOException
     */
    public static PMML loadFromFile(String fileName) throws IOException {
        return loadFromInputStream(FileUtils.getFileInputStream(fileName));
    }

    /**
     * Load a <code>PMML</code> from the given <b>xml source</b>
     * @param xmlSource
     * @return
     */
    public static PMML loadFromSource(String xmlSource) {
        return loadFromInputStream(new ByteArrayInputStream(xmlSource.getBytes()));
    }

    /**
     * Load a <code>PMML</code> from the given <code>InputStream</code>
     * @param is
     * @return
     * @see org.jpmml.model.PMMLUtil#unmarshal(InputStream)
     */
    public static PMML loadFromInputStream(InputStream is) {
        try {
            return org.jpmml.model.PMMLUtil.unmarshal(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
