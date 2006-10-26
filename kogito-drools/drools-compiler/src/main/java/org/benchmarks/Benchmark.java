package org.benchmarks;
import java.util.List;

public interface Benchmark {
    
    public void init() throws Exception;
    public void assertObjects( ) throws Exception;
    public void fireAllRules() throws Exception;

}