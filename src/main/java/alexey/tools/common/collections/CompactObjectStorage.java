package alexey.tools.common.collections;

import alexey.tools.common.misc.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class CompactObjectStorage<T> extends AbstractCollection<T> implements ImmutableObjectCollection<T> {

    protected Object[] data;



    public CompactObjectStorage(Object[] data) {
        this.data = data;
    }

    public CompactObjectStorage(Class<T> type, int capacity) {
        data = (Object[]) Array.newInstance(type, capacity);
    }

    public CompactObjectStorage(int capacity) {
        data = (Object[]) Array.newInstance(Object.class, capacity);
    }

    public CompactObjectStorage() {
        this(16);
    }

    public CompactObjectStorage(Class<T> type) {
        this(type, 16);
    }



    public void makeFirst(int index) {
        if (index == 0) return;
        Object e = data[index];
        int i = index - 1;
        while (i > -1) {
            data[i + 1] = data[i];
            i--;
        }
        data[0] = e;
    }

    public void set(int index, T e) {
        data[index] = e;
    }

    public void extendSet(int index, T e) {
        if (index >= data.length) unsafeSetCapacity(index + 1);
        data[index] = e;
    }

    public void unsafeSetCapacity(int newCapacity) {
        data = Arrays.copyOf(data, newCapacity);
    }

    @SuppressWarnings("unchecked")
    public T removeAt(int index) {
        Object e = data[index];
        unsafeClearAt(index);
        return (T) e;
    }

    public void unsafeClearAt(int index) {
        data = ArrayUtils.unsafeClearAt(data, index);
    }

    @SuppressWarnings("unchecked")
    public T removeLast() {
        int last = data.length - 1;
        Object e = data[last];
        unsafeSetCapacity(last);
        return (T) e;
    }

    public void addNull() {
        unsafeSetCapacity(data.length + 1);
    }

    @SuppressWarnings("unchecked")
    public T replace(int index, T e) {
        Object old = data[index];
        data[index] = e;
        return (T) old;
    }

    public void addAll(@NotNull CompactObjectStorage<? extends T> items) {
        int itemsSize = items.size();
        if (itemsSize < 1) return;
        int size = data.length;
        unsafeSetCapacity(size + itemsSize);
        System.arraycopy(items.data, 0, data, size, itemsSize);
    }

    public void addAll(@NotNull T[] items) {
        int itemsSize = items.length;
        if (itemsSize == 0) return;
        int size = data.length;
        unsafeSetCapacity(size + itemsSize);
        System.arraycopy(items, 0, data, size, itemsSize);
    }

    public void clearData() {
        Arrays.fill(data, null);
    }

    public void setAll(@NotNull T[] items) {
        data = items.clone();
    }

    public Object[] data() {
        return data;
    }

    public boolean removeReference(Object e) {
        Object[] copy = ArrayUtils.minus(data, e);
        if (copy == data) return false;
        data = copy;
        return true;
    }



    @Override
    public void clear() {
        data = (Object[]) Array.newInstance(data.getClass().getComponentType(), 0);
    }

    @Override
    public boolean add(T t) {
        data = ArrayUtils.plus(data, t);
        return true;
    }

    @Override
    public boolean remove(Object e) {
        int index = indexOf(e);
        if (index == -1) return false;
        unsafeClearAt(index);
        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        if (c.isEmpty()) return false;
        int size = data.length;
        unsafeSetCapacity(c.size() + size);
        for (T e : c) data[size++] = e;
        return true;
    }



    @Override
    @NotNull
    public Object[] toArray() {
        return Arrays.copyOf(data, data.length, Object[].class);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <E> E[] toArray(@NotNull E[] a) {
        int size = data.length;
        if (a.length < size) return (E[]) Arrays.copyOf(data, size, a.getClass());
        System.arraycopy(data, 0, a, 0, size);
        if (a.length > size) a[size] = null;
        return a;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T last() {
        return (T) data[data.length - 1];
    }

    @SuppressWarnings("unchecked")
    @Override
    public T first() {
        return (T) data[0];
    }

    @Override
    public int indexOf(Object e) {
        int size = data.length;
        if (e == null) {
            for (int i = 0; size > i; i++) if (data[i] == null) return i; } else {
            for (int i = 0; size > i; i++) if (e.equals(data[i])) return i; }
        return -1;
    }

    @Override
    public int indexOfNull() {
        int size = data.length;
        for (int i = 0; size > i; i++) if (data[i] == null) return i;
        return -1;
    }

    @Override
    public boolean contains(Object e) {
        if (e == null) {
            for (Object d : data) if (d == null) return true; } else {
            for (Object d : data) if (e.equals(d)) return true; }
        return false;
    }

    @Override
    public boolean containsReference(Object e) {
        for (Object d : data) if (e == d) return true;
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(int index) {
        return (T) data[index];
    }

    @SuppressWarnings("unchecked")
    @Override
    public T unsafeGet(int index) {
        return (T) data[index];
    }

    @Override
    public int capacity() {
        return data.length;
    }

    @Override
    public boolean isEmpty() {
        return data.length == 0;
    }

    @Override
    public boolean isNotEmpty() {
        return data.length > 0;
    }

    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("iterator");
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CompactObjectStorage(");
        int size = data.length;
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
        CompactObjectStorage<?> list = (CompactObjectStorage<?>) o;
        int size = data.length;
        if (size != list.data.length) return false;
        for (int i = 0; size > i; i++) if (!data[i].equals(list.data[i])) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (Object e : data) hash = (127 * hash) + e.hashCode();
        return hash;
    }

}
