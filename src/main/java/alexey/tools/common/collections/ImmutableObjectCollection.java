package alexey.tools.common.collections;

import java.util.Collection;

public interface ImmutableObjectCollection<T> {
    default Object[] toArray() { return new Object[0]; }
    default <E> E[] toArray(E[] a) { return a; }

    default T last() { throw new IndexOutOfBoundsException("Index: -1"); }
    default T first() { throw new IndexOutOfBoundsException("Index: 0"); }

    default int indexOf(Object e) { return -1; }
    default int indexOfNull() { return -1; }

    default boolean containsAll(Collection<?> c) { return false; }
    default boolean contains(Object e) { return false; }
    default boolean containsReference(Object e) { return false; }

    default T get(int index) { throw new IndexOutOfBoundsException("Index: " + index); }
    default T unsafeGet(int index) { throw new IndexOutOfBoundsException(); }

    default int size() { return 0; }
    default int capacity() { return 0; }

    default boolean isEmpty() { return true; }
    default boolean isNotEmpty() { return false; }

    ImmutableObjectCollection EMPTY_OBJECT_COLLECTION = new ImmutableObjectCollection<>() {};

    @SuppressWarnings("unchecked")
    static <E> ImmutableObjectCollection<E> emptyList() { return EMPTY_OBJECT_COLLECTION; }
}
