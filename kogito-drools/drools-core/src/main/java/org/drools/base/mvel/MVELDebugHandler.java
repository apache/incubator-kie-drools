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

package org.drools.base.mvel;

import org.mvel2.MVELRuntime;
import org.mvel2.debug.Debugger;
import org.mvel2.debug.Frame;

/**
 * Debug Handler for MVEL dialect.
 *
 * Takes care of registering breakpoints and calling required methods
 * to trigger eclipse debugger to keep breakpoints in sync etc.
 *
 * @author Ahti Kitsik
 *
 */
public final class MVELDebugHandler {

    private static int onBreakReturn = Debugger.CONTINUE;

    public final static String DEBUG_LAUNCH_KEY="mvel.debugger";

    private static Boolean debugMode = null;

    public static final boolean verbose = false;

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
	private final static int onBreak(Frame frame) {
        // We always fall back to Debugger.CONTINUE after each onBreak to avoid eternal step-over flag
        //int oldReturn = onBreakReturn;
        //onBreakReturn = Debugger.CONTINUE;
        //return oldReturn;
		if (verbose) {
            System.out.println("Continuing with "+(onBreakReturn==Debugger.CONTINUE?"continue":"step-over"));
        }
        return onBreakReturn;
	}

    protected final static void registerBreakpoint(String sourceName, int lineNumber) {
        if (verbose) {
            System.out.println("Registering breakpoint for "+sourceName+":"+lineNumber);
        }
        MVELRuntime.registerBreakpoint( sourceName, lineNumber );
    }

    protected final static void clearAllBreakpoints() {
        if (verbose) {
            System.out.println("Clearing all breakpoints");
        }
        MVELRuntime.clearAllBreakpoints();
    }

    protected final static void removeBreakpoint(String sourceName, int lineNumber) {
        if (verbose) {
            System.out.println("Removing breakpoint from "+sourceName+":"+lineNumber);
        }
        MVELRuntime.removeBreakpoint( sourceName, lineNumber );
    }

	private static class MVELDebugger implements Debugger {

        public MVELDebugger() {
        }

		public int onBreak(Frame frame) {
			if (verbose) {
			    System.out.println("onBreak call for "+frame.getSourceName()+":"+frame.getLineNumber());
            }
			return MVELDebugHandler.onBreak(frame);
			// This call is supposed to be catched by the remote debugger
		}

	}

    protected final static void setOnBreakReturn(int value) {
        onBreakReturn = value;
    }

    /**
     * Returns current debug mode.<br/>
     * Holds lazy initialized internal reference to improve performance.<br/>
     * Therefore you can't change System property "mvel.debugger" after isDebugMode is called at least once.<br/>
     * <br/>
     * To update debug mode at runtime use {@link MVELDebugHandler#setDebugMode(boolean)}<br/>
     * @return <code>true</code> if debug mode is enabled.
     */
    public static boolean isDebugMode() {
        if (debugMode==null) {
            debugMode = Boolean.valueOf(System.getProperty(DEBUG_LAUNCH_KEY));
        }
        return debugMode.booleanValue();
    }

    /**
     * Sets debug mode on/off.<br/>
     * Updates local MVELDebugHandler property and System property "mvel.debugger"<br/>
     * <br/>
     * There's no need to ever call this method unless you write junit tests!<br/>
     *
     * @param b is Debug enabled?
     */
    public static void setDebugMode(boolean b) {
        debugMode = Boolean.valueOf( b );
        System.setProperty( DEBUG_LAUNCH_KEY, debugMode.toString());
    }

}
