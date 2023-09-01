package org.kie.dmn.feel.util;

/**
 * Just a helper class for pair of values
 * @param <L>
 * @param <R>
 */
public class Pair<L,R> {

    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() { return left; }
    public R getRight() { return right; }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof Pair) ) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if ( left != null ? !left.equals( pair.left ) : pair.left != null ) return false;
        return right != null ? right.equals( pair.right ) : pair.right == null;

    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

}
