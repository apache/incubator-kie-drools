package com.sample.benchmark;

import java.io.FileWriter;

public class GenerateRuleSet {

	/**
	 * 
	 */
	public GenerateRuleSet() {
		super();
	}

	public static void main(String[] args) {
        // the number of rules
        int count = 100;
        String objectName = "Account";
        String outputFile = count + "_rules.drl";
        if (args == null || args.length < 1) {
            // print out a message of the options
            System.out.println("No parameters were set. Please provide a rule count");
            System.out.println("and filename.");
            System.out.println("");
            System.out.println("example: GenerateRuleSet 1000 1000rules.drl");
        } else {
            if (args[0] != null) {
                count = Integer.parseInt(args[0]);
                outputFile = args[1];
            }
            String linebreak = System.getProperty("line.separator");
            String tab = "  ";
            try {
                FileWriter writer = new FileWriter(outputFile);
                StringBuffer buf = new StringBuffer();
                // delcare the package
                buf.append("package org.drools.samples" + linebreak);
                buf.append("import com.sample.benchmark.models.Account;" + linebreak + linebreak);
                // now loop
                for (int idx=0; idx < count; idx++) {
                    buf.append("rule rule" + idx + "" + linebreak);
                    buf.append("  when" + linebreak);
                    buf.append("    $acc : Account(status == \"standard\", title == \"mr\")"
                            + linebreak);
                    buf.append("  then" + linebreak);
                    buf.append("    System.out.println(\"rule" + idx + " fired\");" + linebreak);
                    buf.append("end" + linebreak + linebreak);
                }
                writer.write(buf.toString());
                writer.close();
                System.out.println("Generated " + count + " rules to " + outputFile);
            } catch (Exception e) {
                
            }
        }
        
    }
}
