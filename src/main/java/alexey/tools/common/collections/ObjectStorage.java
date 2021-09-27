package alexey.tools.common.collections;

import alexey.tools.common.misc.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

public class ObjectStorage<T> extends CompactObjectStorage<T> {

    protected int size;

    protected ObjectStorage(Object[] data, int size) {
        this.data = data;
        this.size = size;
    }

    protected ObjectStorage(Object data) {
        this.data = (Object[]) data;
        size = 0;
    }



    public ObjectStorage() {
        data = (Object[]) Array.newInstance(Object.class, 16);
        size = 0;
    }

    public ObjectStorage(Class<T> type, int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Illegal capacity: " + capacity);
        data = (Object[]) Array.newInstance(type, capacity);
        size = 0;
    }

    public ObjectStorage(Class<T> type) {
        data = (Object[]) Array.newInstance(type, 16);
        size = 0;
    }

    public ObjectStorage(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Illegal capacity: " + capacity);
        data = (Object[]) Array.newInstance(Object.class, capacity);
        size = 0;
    }



    @SuppressWarnings("unchecked")
    public T unsafeRemoveAt(int index) {
        Object e = data[index];
        unsafeClearAt(index);
        return (T) e;
    }

    @SuppressWarnings("unchecked")
    public T unsafeRemoveLast() {
        Object e = data[--size];
        data[size] = null;
        return (T) e;
    }

    public void ensureAdd() {
        if (size == data.length) unsafeSetCapacity(doubledCapacity());
    }

    public void unsafeAdd(T e) {
        data[size++] = e;
    }

    public void unsafeAddAll(@NotNull Iterable<T> items) {
        for (T item : items) data[size++] = item;
    }

    public void unsafeSet(int index, T e) {
        data[index] = e;
    }

    public void setSize(int newSize) {
        if (newSize < 0) throw new IllegalStateException("Illegal size: " + newSize);
        if (size == newSize) return;
        if (newSize < size)
            ArrayUtils.unsafeFill(data, newSize, size, null); else
            grow(newSize);
        size = newSize;
    }

    public void unsafeSetSize(int newSize) {
        size = newSize;
    }

    public void ensureSpace(int index) {
        if (index < size) return;
        unsafeExtendSetSize(index + 1);
    }

    public void ensureSize(int newSize) {
        if (newSize > size) unsafeExtendSetSize(newSize);
    }

    public void grow(int newCapacity) {
        if (newCapacity > data.length) unsafeSetCapacity(max(newCapacity));
    }

    public void unsafeExtendSetSize(int newSize) {
        size = newSize;
        grow(size);
    }

    public int doubledCapacity() {
        return data.length << 1;
    }

    public int max(int newCapacity) {
        return Math.max(doubledCapacity(), newCapacity);
    }



    @Override
    public T removeLast() {
        if (size < 1) outOfBounds(size);
        return unsafeRemoveLast();
    }

    @Override
    public T removeAt(int index) {
        rangeCheck(index);
        return unsafeRemoveAt(index);
    }

    @Override
    public void unsafeSetCapacity(int capacity) {
        data = Arrays.copyOf(data, capacity);
    }

    @Override
    public void addAll(@NotNull CompactObjectStorage<? extends T> items) {
        int itemsSize = items.size();
        if (itemsSize < 1) return;
        int newSize = size + itemsSize;
        grow(newSize);
        System.arraycopy(items.data, 0, data, size, itemsSize);
        size = newSize;
    }

    @Override
    public void makeFirst(int index) {
        if (index == 0) return;
        rangeCheck(index);
        Object e = data[index];
        int i = index - 1;
        while (i > -1) {
            data[i + 1] = data[i];
            i--;
        }
        data[0] = e;
    }

    @Override
    public void unsafeClearAt(int index) {
        data[index] = data[--size];
        data[size] = null;
    }

    @Override
    public void addNull() {
        ensureAdd(); size++;
    }

    @Override
    public void set(int index, T e) {
        rangeCheck(index);
        data[index] = e;
    }

    @Override
    public void extendSet(int index, T e) {
        ensureSpace(index);
        data[index] = e;
    }

    @Override
    public T replace(int index, T e) {
        rangeCheck(index);
        return super.replace(index, e);
    }

    @Override
    public void addAll(@NotNull T[] items) {
        int itemsSize = items.length;
        if (itemsSize == 0) return;
        int newSize = size + itemsSize;
        grow(newSize);
        System.arraycopy(items, 0, data, size, itemsSize);
        size = newSize;
    }

    @Override
    public void clearData() {
        ArrayUtils.unsafeFill(data, 0, size, null);
    }

    @Override
    public void setAll(@NotNull T[] items) {
        int newSize = items.length;
        if (newSize == 0) { clear(); return; }
        if (newSize != size) {
            if (newSize < size)
                ArrayUtils.unsafeFill(data, newSize, size, null); else
                if (newSize > data.length) data = ArrayUtils.unsafeEmptyCopy(data, max(newSize));
            size = newSize;
        }
        System.arraycopy(items, 0, data, 0, size);
    }

    @Override
    public boolean removeReference(Object e) {
        for (int i = 0; i < size; i++) {
            if (data[i] == e) {
                data[i] = data[--size];
                data[size] = null;
                return true;
            }
        }
        return false;
    }



    @Override
    public boolean add(T e) {
        ensureAdd(); data[size++] = e; return true;
    }

    @Override
    public void clear() {
        while (size > 0) data[--size] = null;
    }

    @Override
    public boolean remove(Object e) {
        if (e == null) {
            for (int i = 0; i < size; i++) {
                if (data[i] == null) {
                    data[i] = data[--size];
                    data[size] = null;
                    return true;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (e.equals(data[i])) {
                    data[i] = data[--size];
                    data[size] = null;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        if (c.isEmpty()) return false;
        grow(c.size() + size);
        for (T e : c) data[size++] = e;
        return true;
    }



    protected void outOfBounds(int index) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    protected void rangeCheck(int index) {
        if (index >= size) outOfBounds(index);
    }



    @Override
    @NotNull
    public Object[] toArray() {
        return Arrays.copyOf(data, size);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <E> E[] toArray(@NotNull E[] a) {
        if (a.length < size) return (E[]) Arrays.copyOf(data, size, a.getClass());
        System.arraycopy(data, 0, a, 0, size);
        if (a.length > size) a[size] = null;
        return a;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T last() {
        if (size < 1) outOfBounds(size - 1);
        return (T) data[size - 1];
    }

    @SuppressWarnings("unchecked")
    @Override
    public T first() {
        if (size < 1) outOfBounds(0);
        return (T) data[0];
    }

    @Override
    public int indexOf(Object e) {
        if (e == null) {
            for (int i = 0; size > i; i++) if (data[i] == null) return i; } else {
            for (int i = 0; size > i; i++) if (e.equals(data[i])) return i; }
        return -1;
    }

    @Override
    public int indexOfNull() {
        for (int i = 0; size > i; i++) if (data[i] == null) return i;
        return -1;
    }

    @Override
    public boolean contains(Object e) {
        int i = 0;
        if (e == null) {
            for (; size > i; i++) if (data[i] == null) return true; } else {
            for (; size > i; i++) if (e.equals(data[i])) return true; }
        return false;
    }

    @Override
    public boolean containsReference(Object e) {
        for(int i = 0; size > i; i++) if(e == data[i]) return true;
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(int index) {
        rangeCheck(index);
        return (T) data[index];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean isNotEmpty() {
        return size > 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ObjectStorage(");
        if (size > 0) {
            sb.append(data[0]);
            for (int i = 1; i < size; i++)
                sb.append(", ").append(data[i]);
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectStorage<?> list = (ObjectStorage<?>) o;
        if (size != list.size()) return false;
        for (int i = 0; size > i; i++) if (!data[i].equals(list.data[i])) return false;
        return true;
    }

    @Override
    public int hashCode() {
        if (size == 0) return 0;
        int hash = data[0].hashCode();
        for (int i = 1; size > i; i++)
            hash = 127 * hash + data[i].hashCode();
        return hash;
    }
}
