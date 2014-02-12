package org.drools.compiler.builder.impl.errors;

import org.drools.compiler.commons.jci.problems.CompilationProblem;
import org.drools.compiler.compiler.DroolsError;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the super of the error handlers. Each error handler knows how to
 * report a compile error of its type, should it happen. This is needed, as
 * the compiling is done as one hit at the end, and we need to be able to
 * work out what rule/ast element caused the error.
 *
 * An error handler it created for each class task that is queued to be
 * compiled. This doesn't mean an error has occurred, it just means it *may*
 * occur in the future and we need to be able to map it back to the AST
 * element that originally spawned the code to be compiled.
 */
public abstract class ErrorHandler {

    private final List errors  = new ArrayList();

    protected String   message;

    private boolean    inError = false;

    /** This needes to be checked if there is infact an error */
    public boolean isInError() {
        return this.inError;
    }

    public void addError(final CompilationProblem err) {
        this.errors.add(err);
        this.inError = true;
    }

    /**
     *
     * @return A DroolsError object populated as appropriate, should the
     *         unthinkable happen and this need to be reported.
     */
    public abstract DroolsError getError();

    /**
     * We must use an error of JCI problem objects. If there are no
     * problems, null is returned. These errors are placed in the
     * DroolsError instances. Its not 1 to 1 with reported errors.
     */
    protected CompilationProblem[] collectCompilerProblems() {
        if (this.errors.isEmpty()) {
            return null;
        } else {
            final CompilationProblem[] list = new CompilationProblem[this.errors.size()];
            this.errors.toArray(list);
            return list;
        }
    }
}
