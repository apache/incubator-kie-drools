package org.apache.commons.jci.compilers;

import org.apache.commons.jci.problems.CompilationProblemHandler;
import org.apache.commons.jci.readers.ResourceReader;
import org.apache.commons.jci.stores.ResourceStore;

public abstract class AbstractJavaCompiler implements JavaCompiler {

	protected CompilationProblemHandler problemHandler;

	public void setCompilationProblemHandler( final CompilationProblemHandler pHandler ) {
		problemHandler = pHandler;
	}

	public CompilationResult compile( final String[] pClazzNames, final ResourceReader pReader, final ResourceStore pStore ) {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		if (classLoader == null) {
			classLoader = this.getClass().getClassLoader();
		}

		return compile(pClazzNames, pReader, pStore, classLoader);
	}

}
