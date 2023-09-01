package acme;

import org.kie.dmn.feel.lang.FEELProperty;
import org.kie.dmn.feel.lang.FEELType;

@FEELType
public class Person {
    private String fn;
    private String ln;
    private int age;
    
    public Person() {}
    
    public Person(String fn, String ln) {
        super();
        this.fn = fn;
        this.ln = ln;
    }

    public Person(String fn, String ln, int age) {
        this(fn, ln);
        this.setAge(age);
    }

    @FEELProperty("first name")
    public String getFN() {
        return fn;
    }
    
    public void setFN(String firstName) {
        this.fn = firstName;
    }
    
    @FEELProperty("last name")
    public String getLN() {
        return ln;
    }
    
    public void setLN(String lastName) {
        this.ln = lastName;   
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Person [fn=").append(fn).append(", ln=").append(ln).append("]");
        return builder.toString();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    
}
