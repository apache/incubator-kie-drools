/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.factmodel.traits;

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
