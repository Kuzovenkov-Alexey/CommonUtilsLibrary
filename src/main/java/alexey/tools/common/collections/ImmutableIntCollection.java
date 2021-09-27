package alexey.tools.common.collections;

public interface ImmutableIntCollection {
    default int[] toArray() { return new int[0]; }
    default int[] toArray(int[] a) { return a; }

    default int last() { throw new IndexOutOfBoundsException("Index: -1"); }
    default int first() { throw new IndexOutOfBoundsException("Index: 0"); }

    default int indexOf(int e) { return -1; }
    default boolean contains(int e) { return false; }

    default int get(int index) { throw new IndexOutOfBoundsException("Index: " + index); }
    default int unsafeGet(int index) { throw new IndexOutOfBoundsException(); }

    default int size() { return 0; }
    default int capacity() { return 0; }

    default boolean isEmpty() { return true; }
    default boolean isNotEmpty() { return false; }
    default boolean isBroken() { return false; }
}
