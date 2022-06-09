package org.optaplanner.core.impl.testdata.util;

import java.util.ArrayList;
import java.util.Collection;

public class CodeAssertableArrayList<E> extends ArrayList<E> implements CodeAssertable {

    private final String code;

    public CodeAssertableArrayList(String code, Collection<? extends E> c) {
        super(c);
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

}
