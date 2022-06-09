package org.optaplanner.examples.nqueens.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.nqueens.app.NQueensApp;
import org.optaplanner.examples.nqueens.domain.NQueens;

class NQueensOpenDataFilesTest extends OpenDataFilesTest<NQueens> {

    @Override
    protected CommonApp<NQueens> createCommonApp() {
        return new NQueensApp();
    }
}
