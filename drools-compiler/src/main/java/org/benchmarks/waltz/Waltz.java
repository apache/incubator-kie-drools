package org.benchmarks.waltz;

import org.benchmarks.Benchmark;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public class Waltz {
    public static void help() {
        System.out.println("USAGE: java org.drools.benchmark.manners.Manners <inputfile>");
        System.exit( 0 );
    }

    public static final void main(String[] args) {
        try {
//            if(args.length != 1) {
//                help();
//            }
            

            Benchmark benchmark = new DroolsWaltz();
            //Benchmark benchmark = new JessWaltz();
            
//            InputStream is = Manners.class.getResourceAsStream( "/manners/"+args[0] );
//            List list = getInputObjects( is );
//            list.add( new Count( 1 ) );

            Runtime rt = Runtime.getRuntime();

            // initial stats
            long used1 = rt.totalMemory() - rt.freeMemory();
            long time1 = System.currentTimeMillis();

            // parse and load rulebase
            benchmark.init();
            long used2 = rt.totalMemory() - rt.freeMemory();
            long time2 = System.currentTimeMillis();
            
            // assert objects
            benchmark.assertObjects( );
            long time3 = System.currentTimeMillis();
            long used3 = rt.totalMemory() - rt.freeMemory();

            // fire rules
            benchmark.fireAllRules();
            long time4 = System.currentTimeMillis();
            long used4 = rt.totalMemory() - rt.freeMemory();
            
            // calling gc
            rt.gc();
            long time5 = System.currentTimeMillis();
            long used5 = rt.totalMemory() - rt.freeMemory();

            System.out.println("\n\n RESULTS:\n");
            System.out.println("    - Rules parsing time : "+new Long(time2-time1)+" ms   - Memory used:  + "+new Long((used2-used1)/1024)+" Kb\n");
            System.out.println("    - Assertion time     : " + new Long(time3-time2) +" ms   - Memory used:  + " + new Long((used3-used2)/1024)+" Kb\n");
            System.out.println("    - Rules firing time  : " + new Long(time4-time3) +" ms   - Memory used:  + " + new Long((used4-used3)/1024)+" Kb\n");
            System.out.println("----------------------------------------------------------------\n");
            System.out.println("    - Total time         : "+ new Long(time4-time1)+" ms   - Total memory: + "+new Long((used4-used1)/1024)+" Kb\n");
            System.out.println("    - GC Run time        : "+new Long(time5-time4)+" ms   - Mem after GC: + "+new Long((used5-used1)/1024)+" Kb\n\n");
//            
//            while  (1==1){
//                Thread.yield();
//                Thread.sleep( 2000 );
//            }      
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
