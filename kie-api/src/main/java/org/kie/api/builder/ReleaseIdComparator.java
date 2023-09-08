/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.api.builder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ReleaseIdComparator implements Comparator<ReleaseId> {

    public enum SortDirection {
        ASCENDING,
        DESCENDING
    }

    private final SortDirection sortDirection;

    public ReleaseIdComparator() {
        this(SortDirection.ASCENDING);
    }

    public ReleaseIdComparator(SortDirection sortDirection) {
        if (sortDirection == null) {
            throw new IllegalArgumentException("sort direction must be defined");
        }
        this.sortDirection = sortDirection;
    }

    @Override
    public int compare(ReleaseId o1, ReleaseId o2) {
        int result = 0;
        if (o1 != o2) {
            result = o1.getGroupId().compareTo(o2.getGroupId());
            if (result == 0) {
                result = o1.getArtifactId().compareTo(o2.getArtifactId());
                if (result == 0) {
                    result = new ComparableVersion(o1.getVersion()).compareTo(
                             new ComparableVersion(o2.getVersion()) );
                }
            }
            if (result != 0 && SortDirection.DESCENDING.equals(sortDirection)) {
                result = result * -1;
            }
        }
        return result;
    }

    public static ReleaseId getEarliest(List<ReleaseId> releaseIds) {
        return getFirstSorted(releaseIds, SortDirection.ASCENDING);
    }

    public static ReleaseId getLatest(List<ReleaseId> releaseIds) {
        return getFirstSorted(releaseIds, SortDirection.DESCENDING);
    }

    private static ReleaseId getFirstSorted(List<ReleaseId> releaseIds, SortDirection sortDirection) {
        if (releaseIds != null && !releaseIds.isEmpty()) {
            releaseIds.sort(new ReleaseIdComparator(sortDirection));
            return releaseIds.get(0);
        }
        return null;
    }

    public static class ComparableVersion implements Comparable<ComparableVersion> {

        private String value;

        private String canonical;

        private ListItem items;

        private interface Item {

            enum ItemType {INTEGER_ITEM, STRING_ITEM, LIST_ITEM}

            int compareTo(Item item);

            ItemType getType();

            boolean isNull();
        }

        private static class IntegerItem implements Item {
            private final int value;
            
            private static final IntegerItem NULL_INTEGER = new IntegerItem();
            
            private IntegerItem () {
                this.value = 0;
            }
            
            public IntegerItem(String str) {
                this.value = Integer.parseInt(str);
            }

            public ItemType getType() {
                return ItemType.INTEGER_ITEM;
            }

            public boolean isNull() {
                return this.value == 0;
            }

            public int compareTo(Item item) {
                if (item == null)
                {
                    return value == 0 ? 0 : 1; // 1.0 == 1, 1.1 > 1
                }

                switch (item.getType())
                {
                    case INTEGER_ITEM:
                        return value - ((IntegerItem) item).value;

                    case STRING_ITEM:
                        return 1; // 1.1 > 1-sp

                    case LIST_ITEM:
                        return 1; // 1.1 > 1-1

                    default:
                        throw new IllegalArgumentException("invalid type: " + item.getType());
                }
            }

            public String toString() {
                return Integer.toString(value);
            }
        }

        /**
         * Represents a string in the version item list, usually a qualifier.
         */
        private static class StringItem implements Item {

            private static final String[] QUALIFIERS = {"alpha", "beta", "milestone", "rc", "snapshot", "", "sp"};

            private static final List<String> _QUALIFIERS = Arrays.asList(QUALIFIERS);

            private static final Map<String, String> ALIASES = new HashMap<>();

            static {
                ALIASES.put("ga", "");
                ALIASES.put("final", "");
                ALIASES.put("cr", "rc");
            }

            /**
             * A comparable value for the empty-string qualifier. This one is used to determine if a given qualifier makes
             * the version older than one without a qualifier, or more recent.
             */
            private static final String RELEASE_VERSION_INDEX = String.valueOf(_QUALIFIERS.indexOf(""));

            private String value;

            public StringItem(String value, boolean followedByDigit) {
                if (followedByDigit && value.length() == 1) {
                    // a1 = alpha-1, b1 = beta-1, m1 = milestone-1
                    switch (value.charAt(0)) {
                        case 'a':
                            value = "alpha";
                            break;
                        case 'b':
                            value = "beta";
                            break;
                        case 'm':
                            value = "milestone";
                            break;
                    }
                }
                this.value = ALIASES.containsKey(value) ? ALIASES.get(value) : value;
            }

            public ItemType getType() {
                return ItemType.STRING_ITEM;
            }

            public boolean isNull() {
                return (comparableQualifier(value).compareTo(RELEASE_VERSION_INDEX) == 0);
            }

            /**
             * Returns a comparable value for a qualifier.
             *
             * This method both takes into account the ordering of known qualifiers as well as lexical ordering for unknown
             * qualifiers.
             *
             * just returning an Integer with the index here is faster, but requires a lot of if/then/else to check for -1
             * or QUALIFIERS.size and then resort to lexical ordering. Most comparisons are decided by the first character,
             * so this is still fast. If more characters are needed then it requires a lexical sort anyway.
             *
             * @return an equivalent value that can be used with lexical comparison
             */
            public static String comparableQualifier(String qualifier) {
                int i = _QUALIFIERS.indexOf(qualifier);

                return i == -1 ? _QUALIFIERS.size() + "-" + qualifier : String.valueOf(i);
            }

            public int compareTo(Item item) {
                if (item == null) {
                    // 1-rc < 1, 1-ga > 1
                    return comparableQualifier(value).compareTo(RELEASE_VERSION_INDEX);
                }
                switch (item.getType()) {
                    case INTEGER_ITEM:
                        return -1; // 1.any < 1.1 ?

                    case STRING_ITEM:
                        return comparableQualifier(value).compareTo(comparableQualifier(((StringItem) item).value));

                    case LIST_ITEM:
                        return -1; // 1.any < 1-1

                    default:
                        throw new IllegalArgumentException("invalid type: " + item.getType());
                }
            }

            public String toString() {
                return value;
            }
        }

        /**
         * Represents a version list item. This class is used both for the global item list and for sub-lists (which start
         * with '-(number)' in the version specification).
         */
        private static class ListItem extends ArrayList<Item> implements Item {

            public ItemType getType() {
                return ItemType.LIST_ITEM;
            }

            public boolean isNull() {
                return (size() == 0);
            }

            void normalize() {
                for (ListIterator<Item> iterator = listIterator(size()); iterator.hasPrevious();) {
                    Item item = iterator.previous();
                    if (item.isNull()) {
                        iterator.remove(); // remove null trailing items: 0, "", empty list
                    } else {
                        break;
                    }
                }
            }

            public int compareTo(Item item) {
                if (item == null) {
                    if (size() == 0) {
                        return 0; // 1-0 = 1- (normalize) = 1
                    }
                    Item first = get(0);
                    return first.compareTo(null);
                }
                switch (item.getType()) {
                    case INTEGER_ITEM:
                        return -1; // 1-1 < 1.0.x

                    case STRING_ITEM:
                        return 1; // 1-1 > 1-sp

                    case LIST_ITEM:
                        Iterator<Item> left = iterator();
                        Iterator<Item> right = ((ListItem) item).iterator();

                        while (left.hasNext() || right.hasNext()) {
                            Item l = left.hasNext() ? left.next() : null;
                            Item r = right.hasNext() ? right.next() : null;

                            // if this is shorter, then invert the compare and mul with -1
                            int result = l == null ? -r.compareTo(l) : l.compareTo(r);

                            if (result != 0) {
                                return result;
                            }
                        }

                        return 0;

                    default:
                        throw new IllegalArgumentException("invalid item: " + item.getType());
                }
            }

            public String toString() {
                StringBuilder buffer = new StringBuilder("(");
                for (Iterator<Item> iter = iterator(); iter.hasNext();)
                {
                    buffer.append(iter.next());
                    if (iter.hasNext())
                    {
                        buffer.append(',');
                    }
                }
                buffer.append(')');
                return buffer.toString();
            }
        }

        public ComparableVersion(String version) {
            parseVersion(version);
        }

        public final void parseVersion(String version) {
            this.value = version;

            items = new ListItem();

            version = version.toLowerCase();

            ListItem list = items;

            Deque<Item> stack = new ArrayDeque<>();
            stack.push(list);

            boolean isDigit = false;

            int startIndex = 0;

            for (int i = 0; i < version.length(); i++) {
                char c = version.charAt(i);

                if (c == '.') {
                    if (i == startIndex) {
                        list.add(IntegerItem.NULL_INTEGER);
                    } else {
                        list.add(parseItem(isDigit, version.substring(startIndex, i)));
                    }
                    startIndex = i + 1;
                } else if (c == '-') {
                    if (i == startIndex) {
                        list.add(IntegerItem.NULL_INTEGER);
                    } else {
                        list.add(parseItem(isDigit, version.substring(startIndex, i)));
                    }
                    startIndex = i + 1;

                    if (isDigit) {
                        list.normalize(); // 1.0-* = 1-*

                        if ((i + 1 < version.length()) && Character.isDigit(version.charAt(i + 1))) {
                            // new ListItem only if previous were digits and new char is a digit,
                            // ie need to differentiate only 1.1 from 1-1
                            list.add(list = new ListItem());

                            stack.push(list);
                        }
                    }
                }
                else if (Character.isDigit(c)) {
                    if (!isDigit && i > startIndex) {
                        list.add(new StringItem(version.substring(startIndex, i), true));
                        startIndex = i;
                    }

                    isDigit = true;
                } else {
                    if (isDigit && i > startIndex) {
                        list.add(parseItem(true, version.substring(startIndex, i)));
                        startIndex = i;
                    }

                    isDigit = false;
                }
            }

            if (version.length() > startIndex) {
                list.add(parseItem(isDigit, version.substring(startIndex)));
            }

            while (!stack.isEmpty()) {
                list = (ListItem) stack.pop();
                list.normalize();
            }

            canonical = items.toString();
        }

        private static Item parseItem(boolean isDigit, String buf) {
            return isDigit && buf.length() < 10 ? new IntegerItem(buf) : new StringItem(buf, false);
        }

        public int compareTo(ComparableVersion o) {
            return items.compareTo(o.items);
        }

        public String toString() {
            return value;
        }

        public boolean equals(Object o) {
            return (o instanceof ComparableVersion) && canonical.equals(((ComparableVersion) o).canonical);
        }

        public int hashCode() {
            return canonical.hashCode();
        }
    }

}
