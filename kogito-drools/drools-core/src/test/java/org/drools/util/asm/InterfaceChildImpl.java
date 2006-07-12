package org.drools.util.asm;

public class InterfaceChildImpl extends AbstractClass
    implements
    InterfaceChild {
    
    private String bar;
    private int foo;
    private int baz;
    private String URI;
    public InterfaceChildImpl(String HTML,
                              String bar,
                              int foo,
                              int baz,
                              String uri) {
        super(HTML);
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
    public void setBar(String bar) {
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
    public void setBaz(int baz) {
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
    public void setFoo(int foo) {
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
    public void setURI(String uri) {
        this.URI = uri;
    }
    
    

}
