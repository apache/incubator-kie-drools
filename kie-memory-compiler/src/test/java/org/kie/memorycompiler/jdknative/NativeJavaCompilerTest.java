/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.memorycompiler.jdknative;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import javax.tools.JavaCompiler;

import org.junit.jupiter.api.Test;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.memorycompiler.KieMemoryCompilerException;

public class NativeJavaCompilerTest {

	@Test
	public void simulateJre() {
		NativeJavaCompiler compiler = new NativeJavaCompiler(new NullJavaCompilerFinder());

		assertThatExceptionOfType(KieMemoryCompilerException.class).isThrownBy(() -> compiler.compile(null, null, null, null, null));
	}

	@Test
	public void simulateJreWithException() {
		NativeJavaCompiler compiler = new NativeJavaCompiler(new ExceptionThrowingJavaCompilerFinder());

		assertThatExceptionOfType(KieMemoryCompilerException.class).isThrownBy(() -> compiler.compile(null, null, null, null, null));
	}

	@Test
	public void emptySourcesShouldSucceed() {
		NativeJavaCompiler compiler = new NativeJavaCompiler();
		CompilationResult result = compiler.compile(new String[0], null, null, null, compiler.createDefaultSettings());
		assertThat(result.getErrors()).isEmpty();
	}

	/**
	 * Tests that NativeJavaCompiler can resolve classes from directory-based
	 * classpath entries (not JARs). This exercises the processDirectory() method
	 * which was added to fix kjar builds where kie-maven-plugin compiles DRL
	 * referencing Java classes from target/classes.
	 *
	 * The test compiles source that references CompilationResult (a class from
	 * this module's target/classes directory) using a URLClassLoader that includes
	 * target/classes as a directory URL — without setting any classpath on
	 * JavaCompilerSettings (simulating the classic DRL compilation path).
	 */
	@Test
	public void compileWithDirectoryClasspath() throws Exception {
		// target/classes contains compiled classes from this module as a directory (not a JAR)
		File targetClasses = new File("target/classes");
		assertThat(targetClasses).isDirectory();

		// Create a classloader with target/classes as a directory URL
		URLClassLoader classLoader = new URLClassLoader(
				new URL[]{targetClasses.toURI().toURL()},
				ClassLoader.getPlatformClassLoader()
		);

		// Source that references CompilationResult from the directory-based classpath
		String source =
				"package org.kie.memorycompiler.test;\n" +
				"import org.kie.memorycompiler.CompilationResult;\n" +
				"public class DirectoryClasspathTest {\n" +
				"    public boolean hasErrors(CompilationResult result) {\n" +
				"        return result.getErrors().length > 0;\n" +
				"    }\n" +
				"}";

		Map<String, byte[]> compiled = KieMemoryCompiler.compileNoLoad(
				Map.of("org.kie.memorycompiler.test.DirectoryClasspathTest", source),
				classLoader
		);

		assertThat(compiled).containsKey("org.kie.memorycompiler.test.DirectoryClasspathTest");
		assertThat(compiled.get("org.kie.memorycompiler.test.DirectoryClasspathTest")).isNotEmpty();
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
