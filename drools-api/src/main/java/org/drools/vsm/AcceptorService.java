package org.drools.vsm;

import java.io.IOException;

public interface AcceptorService {
    void start() throws IOException;

    void stop();
}
