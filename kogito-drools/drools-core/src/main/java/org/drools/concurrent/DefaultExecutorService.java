/**
 * 
 */
package org.drools.concurrent;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class DefaultExecutorService implements ExecutorService {

    private static final long serialVersionUID = 7860812696865293690L;
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