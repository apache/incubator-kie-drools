package org.drools.rule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.CodeSource;

import junit.framework.TestCase;

import org.drools.WorkingMemory;
import org.drools.spi.EvalExpression;
import org.drools.spi.Tuple;

public class PackageCompilationDataTest extends TestCase {
    public static class TestEvalExpression implements EvalExpression {
        public boolean evaluate(Tuple t, Declaration[] d, WorkingMemory w) {
            return false;
        }
    }

    public void testCodeSourceUrl() throws IOException {
        final String className = TestEvalExpression.class.getName();
        final PackageCompilationData pcData = new PackageCompilationData(getClass().getClassLoader());
        final EvalCondition invoker = new EvalCondition(null);
        pcData.putInvoker(className, invoker);
        final InputStream is = getClass().getClassLoader().getResourceAsStream(className.replace('.', '/') + ".class");
        try {
            pcData.write(className.replace('.', '/') + ".class", read(is));
        } finally {
            is.close();
        }
        final CodeSource codeSource = invoker.getEvalExpression().getClass().getProtectionDomain().getCodeSource();
        assertNotNull(codeSource.getLocation());
    }

    private static byte[] read(final InputStream is) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final byte[] b = new byte[1024];
        int len;
        while ((len = is.read(b)) > 0) {
            os.write(b, 0, len);
        }
        return os.toByteArray();
    }
}