package org.jcp.jsr94.tck;

// java imports
import java.util.Vector;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;

// internal imports
import org.jcp.jsr94.tck.util.TestCaseUtil;

// external imports
import com.sun.tdk.signaturetest.SignatureTest;
import com.sun.javatest.Status;
import com.sun.javatest.Test;

public class ApiSignatureTestJava7Compatible extends ApiSignatureTest {

    public ApiSignatureTestJava7Compatible(String name) {
        super(name);
    }

    @Override
    public void testSignatures() {
        Status status = null;
        try {
            String javaVersion = System.getProperty("java.version");
            Vector args = new Vector();
            // Get the location of the rule execution sets.
            // Currently we assume we can find the signature file here
            // as well.
            String dir = TestCaseUtil.getRuleExecutionSetLocation();
            args.add("-FileName");
            if (javaVersion.startsWith("1.4")) {
                args.add(dir + "/" + "jaxrules14.sig");
            } else if (javaVersion.startsWith("1.5") || javaVersion.startsWith("1.6")) {
                args.add(dir + "/" + "jaxrules.sig");
            } else {
                args.add(dir + "/" + "jaxrules17.sig");
            }

            args.add("-Package");
            args.add("javax.rules");

            ByteArrayOutputStream os = new ByteArrayOutputStream(2000);
            PrintWriter pw = new PrintWriter(os);

            Test test = new SignatureTest();
            status = test.run((String[]) args.toArray(new String[0]),
                    pw, null);

            pw.flush();
            // Print the results.
            System.out.println(os.toString());
            // Check whether we passed.
            assertTrue("[ApiSignatureTest] " + os.toString(),
                    status.isPassed());
            pw.close();
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
