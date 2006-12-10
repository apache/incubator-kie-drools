package org.codehaus.jfdi.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jfdi.SymbolTable;

class SymbolTableImpl implements SymbolTable {
	
	private List stackFrames;
	
	SymbolTableImpl() {
		this.stackFrames = new ArrayList();
	}
	
	void pushFrame() {
		stackFrames.add( new HashMap() );
	}
	
	void popFrame() {
		stackFrames.remove( stackFrames.size() - 1 );
	}

	public Object put(String identifier, Object object) {
		for ( Iterator frameIter = stackFrames.iterator(); frameIter.hasNext(); ) {
			Map frame = (Map) frameIter.next();
			if ( frame.containsKey( identifier ) ) {
				return frame.put( identifier, object );
			}
		}
		
		Map lastFrame = (Map) stackFrames.get( stackFrames.size() - 1 );
		return lastFrame.put( identifier, object );
	}

	public Object get(String identifier) {
		for ( int i = stackFrames.size(); i >= 0 ; --i ) {
			Map frame = (Map) stackFrames.get( i );
			if ( frame.containsKey( identifier ) ) {
				return frame.get( identifier );
			}
		}
		return null;
	}
	
}
