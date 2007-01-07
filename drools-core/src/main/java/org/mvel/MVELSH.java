package org.mvel;

import org.mvel.integration.impl.LocalVariableResolverFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MVELSH {
    public static void main(String[] args) {
        System.out.println("MVEL (MVFLEX Expression Language) Command-line Interpreter Tool");
        System.out.println("Copyright (C) 2006 The MVFLEX/Valhalla Project");
        System.out.println("Version 1.1.008  -- Written by: Christopher Brock\n\n");

        Map map = new HashMap();

        boolean output = true;
        boolean stacktrace = false;
        boolean showExecTime = false;
        boolean benchmarkMode = false;
        boolean template = false;


        String in;

        LocalVariableResolverFactory lvrf = new LocalVariableResolverFactory(new HashMap<String, Object>());

        //    ExpressionParser parser = new ExpressionParser();

        Object out = null;

        long time;

        while (true) {
            try {
                System.out.print("mvel$ ");
                in = new BufferedReader(new InputStreamReader(System.in)).readLine();

                if (in.length() == 0) continue;

                if ("quit;".equals(in) || "exit;".equals(in)) return;
                if ("stacktrace;".equals(in)) {
                    stacktrace = !stacktrace;
                    System.out.println("STACKTRACES: " + (stacktrace ? "ON" : "OFF"));
                    continue;
                }

                if ("echo;".equals(in)) {
                    output = !output;

                    System.out.println("OUTPUT ECHO: " + (output ? "ON" : "OFF"));

                    continue;
                }

                if ("template;".equals(in)) {
                    template = !template;
                    System.out.println("TEMPLATE INTERPRETER: " + (template ? "ON" : "OFF"));
                    continue;
                }

                if ("exectime;".equals(in)) {
                    showExecTime = !showExecTime;
                    System.out.println("SHOW EXEC TIME: " + (showExecTime ? "ON " : "OFF"));
                    continue;
                }
                if ("benchmark;".equals(in)) {
                    benchmarkMode = !benchmarkMode;
                    showExecTime = true;
                    System.out.println("MVELSH BENCHMARK MODE: " + (benchmarkMode ? "ON" : "OFF"));
                    continue;
                }
                if ("clear;".equals(in)) {
                    map.clear();
                    System.out.println("CLEARED VARIABLES.");
                    continue;
                }
                if ("help;".equals(in)) {
                    showHelp();
                    continue;
                }

                if (template) {
                    time = System.currentTimeMillis();
                    out = Interpreter.parse(in, null, map);
                    time = System.currentTimeMillis() - time;
                }
                else if (benchmarkMode) {
                    System.out.println("HOTSPOT WARMUP ...");
                    for (int i = 10000; i != 0; i--) {
                        ExpressionParser.eval(in, lvrf);
                    }
                    System.out.println("RUNNING BENCHMARK (10,000 times) ...");

                    time = System.currentTimeMillis();
                    for (int i = 10000; i != 0; i--) {
                        out = ExpressionParser.eval(in, lvrf);

                    }
                    time = System.currentTimeMillis() - time;
                }
                else {
                    time = System.currentTimeMillis();
                    out = ExpressionParser.eval(in, lvrf);
                    time = System.currentTimeMillis() - time;
                }

                if (showExecTime) System.out.println("DONE in : " + time + "ms.");
                if (output) System.out.println((template ? "TOUT: " : "OUT: ") + out);
            }
            catch (Exception e) {
                if (stacktrace) e.printStackTrace();
                else
                    System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private static void showHelp() {
        System.out.println("Commands");
        System.out.println("--------");
        System.out.println("echo            -   toggles output echo on/off");
        System.out.println("template        -   use the template parser shell");
        System.out.println("stacktrace      -   toggles stacktraces on/off");
        System.out.println("benchmark       -   toggles benchmark mode on/off");
        System.out.println("exectime        -   toggles execution time display on/off");
        System.out.println("clear           -   clears all variables");
        System.out.println("quit            -   exits the shell");
        System.out.println("\n");
    }
}
