package org.drools.traits.compiler.factmodel.traits;

public class Imp {

    private String name;
    private String school;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }


    public double testMethod( String arg1, int arg2, Object arg3, double arg4 ) {
        return 0.0;
    }


//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//
//        Imp imp = (Imp) o;
//
//        if (getName() != null ? !getName().equals(imp.getName()) : imp.getName() != null) return false;
//        if (getSchool() != null ? !getSchool().equals(imp.getSchool()) : imp.getSchool() != null) return false;
//
//        return true;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Imp imp = (Imp) o;

        if (name != null ? !name.equals(imp.name) : imp.name != null) return false;
        if (school != null ? !school.equals(imp.school) : imp.school != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (school != null ? school.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Imp{" +
                "name='" + name + '\'' +
                ", school='" + school + '\'' +
                '}';
    }
}
