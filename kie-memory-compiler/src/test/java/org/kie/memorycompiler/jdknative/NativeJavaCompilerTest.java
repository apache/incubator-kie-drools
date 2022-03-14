package org.kie.memorycompiler.jdknative;

import javax.tools.JavaCompiler;

import org.junit.Test;
import org.kie.memorycompiler.KieMemoryCompilerException;

public class NativeJavaCompilerTest {

	@Test(expected = KieMemoryCompilerException.class)
	public void simulateJre() {

		NativeJavaCompiler compiler = new NativeJavaCompiler(new NullJavaCompilerFinder());

		
		compiler.compile(null, null, null, null, null);
	}

	@Test(expected = KieMemoryCompilerException.class)
	public void simulateJreWithException() {

		NativeJavaCompiler compiler = new NativeJavaCompiler(new ExceptionThrowingJavaCompilerFinder());

		
		compiler.compile(null, null, null, null, null);
	}
	
	private static class NullJavaCompilerFinder implements JavaCompilerFinder {

		@Override
		public JavaCompiler getJavaCompiler() {
			return null;
		}
	}
	
	private static class ExceptionThrowingJavaCompilerFinder implements JavaCompilerFinder {

		@Override
		public JavaCompiler getJavaCompiler() {
			throw new RuntimeException("Test exception");
		}
	}


}
