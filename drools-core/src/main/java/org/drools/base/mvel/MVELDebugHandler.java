package org.drools.base.mvel;

import org.mvel.MVELRuntime;
import org.mvel.debug.Debugger;
import org.mvel.debug.Frame;

public final class MVELDebugHandler {

	static {
		MVELRuntime.setThreadDebugger(new MVELDebugger());
	}

	/**
	 * Notify remote debugger that runtime is ready to get latest breakpoint
	 * information
	 *
	 */
	public static void receiveBreakpoints() {
	}

	/**
	 * This is catched by the remote debugger
	 *
	 * @param frame
	 */
	private final static void onBreak(Frame frame) {
	}

    protected final static void registerBreakpoint(String sourceName, int lineNumber) {
        MVELRuntime.registerBreakpoint( sourceName, lineNumber );
    }
    
    protected final static void clearAllBreakpoints() {
        MVELRuntime.clearAllBreakpoints();
    }
    
    protected final static void removeBreakpoint(String sourceName, int lineNumber) {
        MVELRuntime.removeBreakpoint( sourceName, lineNumber );
    }
    
	private static class MVELDebugger implements Debugger {

        public MVELDebugger() {
        }
        
		public int onBreak(Frame frame) {
			MVELDebugHandler.onBreak(frame);
			// This call is supposed to be catched by the remote debugger
			return 0;
		}

	}

    /**
     * Do nothing. ensures that class is loaded prior debug handler
     */
    public static void prepare() {
    	//do nothing
    }
        
}
