/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 
 */
package org.drools.concurrent;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class DefaultExecutorService implements ExecutorService {

    private static final long serialVersionUID = 510l;
    private Thread thread;
    private CommandExecutor executor;
    private boolean running;

    public DefaultExecutorService() {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        thread  = (Thread)in.readObject();
        executor    = (CommandExecutor)in.readObject();
        running     = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(thread);
        out.writeObject(executor);
        out.writeBoolean(running);
    }

    public void setCommandExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    public void startUp() {
        this.thread = new Thread( executor );
        this.thread.start();
        this.running = true;
    }

    public void shutDown() {
        this.executor.shutdown();
        this.running = false;
        this.thread = null;
    }

    public Future submit(Command command) {
        if (!this.running) {
            startUp();
        }
        return this.executor.submit( command );
    }
}
