/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package java.text;

public class FieldPosition {

    public FieldPosition(int field) {

    }

    public FieldPosition(Format.Field attribute) {

    }

    public FieldPosition(Format.Field attribute, int fieldID) {

    }

    public Format.Field getFieldAttribute() {
        return null;
    }

    public int getField() {
        return 0;
    }

    public int getBeginIndex() {
        return 0;
    }

    public int getEndIndex() {
        return 0;
    }

    public void setBeginIndex(int bi) {
    }

    public void setEndIndex(int ei) {
    }

    Format.FieldDelegate getFieldDelegate() {
        return null;
    }

    public boolean equals(FieldPosition obj) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "";
    }

    private boolean matchesField(Format.Field attribute) {
        return false;
    }

    private boolean matchesField(Format.Field attribute, int field) {
        return true;
    }

    private class Delegate implements Format.FieldDelegate {

        public void formatted(Format.Field attr, Object value, int start,
                              int end, StringBuffer buffer) {

        }

        public void formatted(int fieldID, Format.Field attr, Object value,
                              int start, int end, StringBuffer buffer) {

        }
    }
}
