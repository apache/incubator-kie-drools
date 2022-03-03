/**
 * {@link org.drools.compiler.builder.impl.processors.CompilationPhase}s originate as
 * methods in {@link org.drools.compiler.builder.impl.KnowledgeBuilderImpl}.
 * This package contains classes that have been refactored in order to maximize
 * reuse of that code.
 *
 * Each {@link org.drools.compiler.builder.impl.processors.CompilationPhase} was
 * usually contains one method for processing and one method to get
 * the results (errors or warnings).
 *
 */
package org.drools.compiler.builder.impl.processors;