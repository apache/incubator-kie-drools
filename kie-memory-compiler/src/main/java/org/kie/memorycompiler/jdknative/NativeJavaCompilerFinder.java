package org.kie.memorycompiler.jdknative;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;


public class NativeJavaCompilerFinder implements JavaCompilerFinder {

	@Override
	public JavaCompiler getJavaCompiler() {
		return ToolProvider.getSystemJavaCompiler();
	}

}
