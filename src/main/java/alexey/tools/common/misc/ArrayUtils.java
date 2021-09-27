package alexey.tools.common.misc;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ArrayUtils {

    public static <T> T[] minus(T[] source, Object element) {
        int index = indexOf(source, element);
        if (index == -1) return source;
        return unsafeClearAt(source, index);
    }

    public static <T> T[] unsafeClearAt(T[] source, int index) {
        int length = source.length - 1;
        T[] result = Arrays.copyOf(source, length);
        if (index == length) return result;
        result[index] = source[length];
        return result;
    }

    @Contract(pure = true)
    public static <T> int indexOf(@NotNull T[] source, T element) {
        for (int i = 0; i < source.length; i++) if (source[i] == element) return i;
        return -1;
    }

    @NotNull
    public static <T> T[] plus(@NotNull T[] source, T element) {
        int index = source.length;
        T[] result = Arrays.copyOf(source, index + 1);
        result[index] = element;
        return result;
    }

    public static void unsafeFill(Object[] a, int fromIndex, int toIndex, Object val) {
        for (int i = fromIndex; i < toIndex; i++) a[i] = val;
    }

    @NotNull
    public static int[] unsafeCopyOf(int[] source, int newLength, int amount) {
        int[] copy = new int[newLength];
        System.arraycopy(source, 0, copy, 0, amount);
        return copy;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] unsafeEmptyCopy(@NotNull T[] source, int newLength) {
        return (T[]) Array.newInstance(source.getClass().getComponentType(), newLength);
    }
}