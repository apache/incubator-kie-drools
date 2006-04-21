package com.sample.benchmark;

import java.io.FileWriter;

public class GenerateRuleSet {

    String linebreak = System.getProperty("line.separator");
    String tab = "  ";

    /**
	 * 
	 */
	public GenerateRuleSet() {
		super();
	}

	public void generate3Condition(String outputFile, int count) {
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
                buf.append("    $acc : Account(status == \"standard\", title == \"mr\", accountId == \"acc" + idx +
                		"\")" + linebreak);
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

	public void generate2Condition(String outputFile, int count) {
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
                buf.append("    $acc : Account(status == \"standard\", accountId == \"acc" + idx +
                		"\")" + linebreak);
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
	
	public void generateOneCondition(String outputFile, int count) {
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
                buf.append("    $acc : Account(accountId == \"acc" + idx +
                		"\")" + linebreak);
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
	
	public static void main(String[] args) {
        // the number of rules
        int count = 100;
        String objectName = "Account";
        String outputFile = count + "_rules.drl";
        if (args == null || args.length < 1) {
            // print out a message of the options
            System.out.println("No parameters were set. Please provide a rule count");
            System.out.println("filename, ruleType: 1, 2 or 3.");
            System.out.println("");
            System.out.println("example: GenerateRuleSet 1000 1000rules.drl 1");
        } else {
            if (args[0] != null) {
                count = Integer.parseInt(args[0]);
                outputFile = args[1];
            }
            int type = 1;
            if (args[2] != null) {
            	type = Integer.parseInt(args[2]);
            }
            GenerateRuleSet grs = new GenerateRuleSet();
            if (type == 3) {
                grs.generate3Condition(outputFile,count);
            } else if (type == 2) {
            	grs.generate2Condition(outputFile, count);
            } else {
                grs.generateOneCondition(outputFile,count);
            }
        }
        
    }
}
