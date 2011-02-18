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

package org.drools.util.asm;

public class InterfaceChildImpl extends AbstractClass
    implements
    InterfaceChild {

    private String bar;
    private int    foo;
    private int    baz;
    private String URI;

    public InterfaceChildImpl(final String HTML,
                              final String bar,
                              final int foo,
                              final int baz,
                              final String uri) {
        super( HTML );
        this.bar = bar;
        this.foo = foo;
        this.baz = baz;
        this.URI = uri;
    }

    /**
     * @return the bar
     */
    public String getBar() {
        return this.bar;
    }

    /**
     * @param bar the bar to set
     */
    public void setBar(final String bar) {
        this.bar = bar;
    }

    /**
     * @return the baz
     */
    public int getBaz() {
        return this.baz;
    }

    /**
     * @param baz the baz to set
     */
    public void setBaz(final int baz) {
        this.baz = baz;
    }

    /**
     * @return the foo
     */
    public int getFoo() {
        return this.foo;
    }

    /**
     * @param foo the foo to set
     */
    public void setFoo(final int foo) {
        this.foo = foo;
    }

    /**
     * @return the uRI
     */
    public String getURI() {
        return this.URI;
    }

    /**
     * @param uri the uRI to set
     */
    public void setURI(final String uri) {
        this.URI = uri;
    }

}
