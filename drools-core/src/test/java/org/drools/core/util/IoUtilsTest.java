package org.drools.core.util;

import org.drools.io.ReaderInputStream;
import org.drools.util.IoUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

public class IoUtilsTest {

    @Test
    public void testReadEmptyStream() throws IOException {
        // DROOLS-971
        byte[] bytes = IoUtils.readBytesFromInputStream( new ReaderInputStream( new StringReader( "" ) ) );
        assertThat(bytes).isEmpty();
    }
	
	@Test
    public void testAsSystemSpecificPath() {
        String urlPath = "c:\\workdir\\server-local\\instance\\tmp\\vfs\\deployment\\deploymentf7b5abe7c4c1cb56\\rules-with-kjar-1.0.jar-57cc270a5729d6b2\\rules-with-kjar-1.0.jar";
        String specificPath = IoUtils.asSystemSpecificPath(urlPath, 1);
        // Check that windows drive (even in lower case) is not removed
        assertThat(specificPath).isEqualTo(urlPath);
    }
}
