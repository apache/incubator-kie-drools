package org.benchmarks.waltz;
import jess.*;

public class WaltzFile implements Userpackage {
    public void add(Rete engine) {
        engine.addUserfunction(new Make3Junction());
    }
}
