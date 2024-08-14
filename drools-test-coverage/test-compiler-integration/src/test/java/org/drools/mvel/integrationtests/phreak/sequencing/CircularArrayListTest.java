package org.drools.mvel.integrationtests.phreak.sequencing;

import org.drools.base.util.CircularArrayList;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CircularArrayListTest {

    @Test
    public void testAddingWithWrap() {
        CircularArrayList<Integer> list = new CircularArrayList<>(10);

        for (int i = 0; i < 15; i++) {
            list.add(i);
        }

        System.out.println(list);
        assertThat(list.size()).isEqualTo(15);
        assertThat(list.get(list.size()-1)).isEqualTo(14);
        assertThat(list.get(14)).isEqualTo(14);
        assertThat(list.getHead()).isEqualTo(14);
        assertThat(list.getHeadMinus(3)).isEqualTo(11);

        assertThatThrownBy(() -> list.get(-1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> list.get(0)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> list.get(4)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThat(list.get(5)).isEqualTo(5);
    }

    @Test
    public void testToArray() {
        CircularArrayList<Object> list = new CircularArrayList<>(10);
        assertThat(list.toArray().length).isEqualTo(0);
        list.add(0);
        assertThat(list.toArray()).isEqualTo(new Object[] {0});
        list.add(1);
        assertThat(list.toArray()).isEqualTo(new Object[] {0, 1});
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(8);
        assertThat(list.toArray()).isEqualTo(new Object[] {0, 1, 2, 3, 4, 5, 6, 7, 8});
        list.add(9);
        assertThat(list.toArray()).isEqualTo(new Object[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        list.add(10);
        assertThat(list.toArray()).isEqualTo(new Object[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        list.add(11);
        list.add(12);
        list.add(13);
        list.add(14);
        list.add(15);
        assertThat(list.toArray()).isEqualTo(new Object[] {6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
    }

}
