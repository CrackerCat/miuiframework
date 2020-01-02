package android.util;

import android.annotation.UnsupportedAppUsage;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import libcore.util.EmptyArray;

public final class ArraySet<E> implements Collection<E>, Set<E> {
    private static final int BASE_SIZE = 4;
    private static final int CACHE_SIZE = 10;
    private static final boolean DEBUG = false;
    private static final String TAG = "ArraySet";
    static Object[] sBaseCache;
    static int sBaseCacheSize;
    static Object[] sTwiceBaseCache;
    static int sTwiceBaseCacheSize;
    @UnsupportedAppUsage(maxTargetSdk = 28)
    Object[] mArray;
    MapCollections<E, E> mCollections;
    @UnsupportedAppUsage(maxTargetSdk = 28)
    int[] mHashes;
    final boolean mIdentityHashCode;
    @UnsupportedAppUsage(maxTargetSdk = 28)
    int mSize;

    @UnsupportedAppUsage(maxTargetSdk = 28)
    private int indexOf(Object key, int hash) {
        int N = this.mSize;
        if (N == 0) {
            return -1;
        }
        int index = ContainerHelpers.binarySearch(this.mHashes, N, hash);
        if (index < 0 || key.equals(this.mArray[index])) {
            return index;
        }
        int end = index + 1;
        while (end < N && this.mHashes[end] == hash) {
            if (key.equals(this.mArray[end])) {
                return end;
            }
            end++;
        }
        int i = index - 1;
        while (i >= 0 && this.mHashes[i] == hash) {
            if (key.equals(this.mArray[i])) {
                return i;
            }
            i--;
        }
        return ~end;
    }

    @UnsupportedAppUsage(maxTargetSdk = 28)
    private int indexOfNull() {
        int N = this.mSize;
        if (N == 0) {
            return -1;
        }
        int index = ContainerHelpers.binarySearch(this.mHashes, N, 0);
        if (index < 0 || this.mArray[index] == null) {
            return index;
        }
        int end = index + 1;
        while (end < N && this.mHashes[end] == 0) {
            if (this.mArray[end] == null) {
                return end;
            }
            end++;
        }
        int i = index - 1;
        while (i >= 0 && this.mHashes[i] == 0) {
            if (this.mArray[i] == null) {
                return i;
            }
            i--;
        }
        return ~end;
    }

    @UnsupportedAppUsage(maxTargetSdk = 28)
    private void allocArrays(int size) {
        String str;
        StringBuilder stringBuilder;
        Object[] array;
        if (size == 8) {
            synchronized (ArraySet.class) {
                if (sTwiceBaseCache != null) {
                    array = sTwiceBaseCache;
                    try {
                        this.mArray = array;
                        sTwiceBaseCache = (Object[]) array[0];
                        this.mHashes = (int[]) array[1];
                        array[1] = null;
                        array[0] = null;
                        sTwiceBaseCacheSize--;
                        return;
                    } catch (ClassCastException e) {
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Found corrupt ArraySet cache: [0]=");
                        stringBuilder.append(array[0]);
                        stringBuilder.append(" [1]=");
                        stringBuilder.append(array[1]);
                        Slog.wtf(str, stringBuilder.toString());
                        sTwiceBaseCache = null;
                        sTwiceBaseCacheSize = 0;
                        this.mHashes = new int[size];
                        this.mArray = new Object[size];
                    }
                }
            }
        } else if (size == 4) {
            synchronized (ArraySet.class) {
                if (sBaseCache != null) {
                    array = sBaseCache;
                    try {
                        this.mArray = array;
                        sBaseCache = (Object[]) array[0];
                        this.mHashes = (int[]) array[1];
                        array[1] = null;
                        array[0] = null;
                        sBaseCacheSize--;
                        return;
                    } catch (ClassCastException e2) {
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Found corrupt ArraySet cache: [0]=");
                        stringBuilder.append(array[0]);
                        stringBuilder.append(" [1]=");
                        stringBuilder.append(array[1]);
                        Slog.wtf(str, stringBuilder.toString());
                        sBaseCache = null;
                        sBaseCacheSize = 0;
                        this.mHashes = new int[size];
                        this.mArray = new Object[size];
                    }
                }
            }
        }
        this.mHashes = new int[size];
        this.mArray = new Object[size];
    }

    @UnsupportedAppUsage(maxTargetSdk = 28)
    private static void freeArrays(int[] hashes, Object[] array, int size) {
        int i;
        if (hashes.length == 8) {
            synchronized (ArraySet.class) {
                if (sTwiceBaseCacheSize < 10) {
                    array[0] = sTwiceBaseCache;
                    array[1] = hashes;
                    for (i = size - 1; i >= 2; i--) {
                        array[i] = null;
                    }
                    sTwiceBaseCache = array;
                    sTwiceBaseCacheSize++;
                }
            }
        } else if (hashes.length == 4) {
            synchronized (ArraySet.class) {
                if (sBaseCacheSize < 10) {
                    array[0] = sBaseCache;
                    array[1] = hashes;
                    for (i = size - 1; i >= 2; i--) {
                        array[i] = null;
                    }
                    sBaseCache = array;
                    sBaseCacheSize++;
                }
            }
        }
    }

    public ArraySet() {
        this(0, false);
    }

    public ArraySet(int capacity) {
        this(capacity, false);
    }

    public ArraySet(int capacity, boolean identityHashCode) {
        this.mIdentityHashCode = identityHashCode;
        if (capacity == 0) {
            this.mHashes = EmptyArray.INT;
            this.mArray = EmptyArray.OBJECT;
        } else {
            allocArrays(capacity);
        }
        this.mSize = 0;
    }

    public ArraySet(ArraySet<E> set) {
        this();
        if (set != null) {
            addAll((ArraySet) set);
        }
    }

    public ArraySet(Collection<? extends E> set) {
        this();
        if (set != null) {
            addAll((Collection) set);
        }
    }

    public void clear() {
        int i = this.mSize;
        if (i != 0) {
            freeArrays(this.mHashes, this.mArray, i);
            this.mHashes = EmptyArray.INT;
            this.mArray = EmptyArray.OBJECT;
            this.mSize = 0;
        }
    }

    public void ensureCapacity(int minimumCapacity) {
        if (this.mHashes.length < minimumCapacity) {
            int[] ohashes = this.mHashes;
            Object[] oarray = this.mArray;
            allocArrays(minimumCapacity);
            int i = this.mSize;
            if (i > 0) {
                System.arraycopy(ohashes, 0, this.mHashes, 0, i);
                System.arraycopy(oarray, 0, this.mArray, 0, this.mSize);
            }
            freeArrays(ohashes, oarray, this.mSize);
        }
    }

    public boolean contains(Object key) {
        return indexOf(key) >= 0;
    }

    public int indexOf(Object key) {
        if (key == null) {
            return indexOfNull();
        }
        return indexOf(key, this.mIdentityHashCode ? System.identityHashCode(key) : key.hashCode());
    }

    public E valueAt(int index) {
        if (index < this.mSize || !UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            return valueAtUnchecked(index);
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    public E valueAtUnchecked(int index) {
        return this.mArray[index];
    }

    public boolean isEmpty() {
        return this.mSize <= 0;
    }

    public boolean add(E value) {
        int hash;
        int index;
        if (value == null) {
            hash = 0;
            index = indexOfNull();
        } else {
            hash = this.mIdentityHashCode ? System.identityHashCode(value) : value.hashCode();
            index = indexOf(value, hash);
        }
        if (index >= 0) {
            return false;
        }
        index = ~index;
        int i = this.mSize;
        if (i >= this.mHashes.length) {
            int i2 = 4;
            if (i >= 8) {
                i2 = (i >> 1) + i;
            } else if (i >= 4) {
                i2 = 8;
            }
            i = i2;
            int[] ohashes = this.mHashes;
            Object[] oarray = this.mArray;
            allocArrays(i);
            int[] iArr = this.mHashes;
            if (iArr.length > 0) {
                System.arraycopy(ohashes, 0, iArr, 0, ohashes.length);
                System.arraycopy(oarray, 0, this.mArray, 0, oarray.length);
            }
            freeArrays(ohashes, oarray, this.mSize);
        }
        int i3 = this.mSize;
        if (index < i3) {
            int[] iArr2 = this.mHashes;
            System.arraycopy(iArr2, index, iArr2, index + 1, i3 - index);
            Object[] objArr = this.mArray;
            System.arraycopy(objArr, index, objArr, index + 1, this.mSize - index);
        }
        this.mHashes[index] = hash;
        this.mArray[index] = value;
        this.mSize++;
        return true;
    }

    public void append(E value) {
        int index = this.mSize;
        int hash = value == null ? 0 : this.mIdentityHashCode ? System.identityHashCode(value) : value.hashCode();
        int[] iArr = this.mHashes;
        if (index >= iArr.length) {
            throw new IllegalStateException("Array is full");
        } else if (index <= 0 || iArr[index - 1] <= hash) {
            this.mSize = index + 1;
            this.mHashes[index] = hash;
            this.mArray[index] = value;
        } else {
            add(value);
        }
    }

    public void addAll(ArraySet<? extends E> array) {
        int N = array.mSize;
        ensureCapacity(this.mSize + N);
        if (this.mSize != 0) {
            for (int i = 0; i < N; i++) {
                add(array.valueAt(i));
            }
        } else if (N > 0) {
            System.arraycopy(array.mHashes, 0, this.mHashes, 0, N);
            System.arraycopy(array.mArray, 0, this.mArray, 0, N);
            this.mSize = N;
        }
    }

    public boolean remove(Object object) {
        int index = indexOf(object);
        if (index < 0) {
            return false;
        }
        removeAt(index);
        return true;
    }

    private boolean shouldShrink() {
        int[] iArr = this.mHashes;
        return iArr.length > 8 && this.mSize < iArr.length / 3;
    }

    private int getNewShrunkenSize() {
        int i = this.mSize;
        return i > 8 ? (i >> 1) + i : 8;
    }

    public E removeAt(int index) {
        if (index < this.mSize || !UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            Object old = this.mArray[index];
            int n;
            if (this.mSize <= 1) {
                clear();
            } else if (shouldShrink()) {
                n = getNewShrunkenSize();
                int[] ohashes = this.mHashes;
                Object[] oarray = this.mArray;
                allocArrays(n);
                this.mSize--;
                if (index > 0) {
                    System.arraycopy(ohashes, 0, this.mHashes, 0, index);
                    System.arraycopy(oarray, 0, this.mArray, 0, index);
                }
                int i = this.mSize;
                if (index < i) {
                    System.arraycopy(ohashes, index + 1, this.mHashes, index, i - index);
                    System.arraycopy(oarray, index + 1, this.mArray, index, this.mSize - index);
                }
            } else {
                this.mSize--;
                n = this.mSize;
                if (index < n) {
                    int[] iArr = this.mHashes;
                    System.arraycopy(iArr, index + 1, iArr, index, n - index);
                    Object[] objArr = this.mArray;
                    System.arraycopy(objArr, index + 1, objArr, index, this.mSize - index);
                }
                this.mArray[this.mSize] = null;
            }
            return old;
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    public boolean removeAll(ArraySet<? extends E> array) {
        int N = array.mSize;
        int originalSize = this.mSize;
        for (int i = 0; i < N; i++) {
            remove(array.valueAt(i));
        }
        return originalSize != this.mSize;
    }

    public boolean removeIf(Predicate<? super E> filter) {
        if (this.mSize == 0) {
            return false;
        }
        int i;
        Object[] objArr;
        int replaceIndex = 0;
        int numRemoved = 0;
        int i2 = 0;
        while (true) {
            i = this.mSize;
            if (i2 >= i) {
                break;
            }
            if (filter.test(this.mArray[i2])) {
                numRemoved++;
            } else {
                if (replaceIndex != i2) {
                    objArr = this.mArray;
                    objArr[replaceIndex] = objArr[i2];
                    int[] iArr = this.mHashes;
                    iArr[replaceIndex] = iArr[i2];
                }
                replaceIndex++;
            }
            i2++;
        }
        if (numRemoved == 0) {
            return false;
        }
        if (numRemoved == i) {
            clear();
            return true;
        }
        this.mSize = i - numRemoved;
        if (!shouldShrink()) {
            int i3 = this.mSize;
            while (true) {
                objArr = this.mArray;
                if (i3 >= objArr.length) {
                    break;
                }
                objArr[i3] = null;
                i3++;
            }
        } else {
            i = getNewShrunkenSize();
            int[] ohashes = this.mHashes;
            Object[] oarray = this.mArray;
            allocArrays(i);
            System.arraycopy(ohashes, 0, this.mHashes, 0, this.mSize);
            System.arraycopy(oarray, 0, this.mArray, 0, this.mSize);
        }
        return true;
    }

    public int size() {
        return this.mSize;
    }

    public Object[] toArray() {
        int i = this.mSize;
        Object[] result = new Object[i];
        System.arraycopy(this.mArray, 0, result, 0, i);
        return result;
    }

    public <T> T[] toArray(T[] array) {
        if (array.length < this.mSize) {
            array = (Object[]) Array.newInstance(array.getClass().getComponentType(), this.mSize);
        }
        System.arraycopy(this.mArray, 0, array, 0, this.mSize);
        int length = array.length;
        int i = this.mSize;
        if (length > i) {
            array[i] = null;
        }
        return array;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Set)) {
            return false;
        }
        Set<?> set = (Set) object;
        if (size() != set.size()) {
            return false;
        }
        int i = 0;
        while (i < this.mSize) {
            try {
                if (!set.contains(valueAt(i))) {
                    return false;
                }
                i++;
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e2) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int[] hashes = this.mHashes;
        int result = 0;
        for (int i = 0; i < this.mSize; i++) {
            result += hashes[i];
        }
        return result;
    }

    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder buffer = new StringBuilder(this.mSize * 14);
        buffer.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            ArraySet value = valueAt(i);
            if (value != this) {
                buffer.append(value);
            } else {
                buffer.append("(this Set)");
            }
        }
        buffer.append('}');
        return buffer.toString();
    }

    private MapCollections<E, E> getCollection() {
        if (this.mCollections == null) {
            this.mCollections = new MapCollections<E, E>() {
                /* Access modifiers changed, original: protected */
                public int colGetSize() {
                    return ArraySet.this.mSize;
                }

                /* Access modifiers changed, original: protected */
                public Object colGetEntry(int index, int offset) {
                    return ArraySet.this.mArray[index];
                }

                /* Access modifiers changed, original: protected */
                public int colIndexOfKey(Object key) {
                    return ArraySet.this.indexOf(key);
                }

                /* Access modifiers changed, original: protected */
                public int colIndexOfValue(Object value) {
                    return ArraySet.this.indexOf(value);
                }

                /* Access modifiers changed, original: protected */
                public Map<E, E> colGetMap() {
                    throw new UnsupportedOperationException("not a map");
                }

                /* Access modifiers changed, original: protected */
                public void colPut(E key, E e) {
                    ArraySet.this.add(key);
                }

                /* Access modifiers changed, original: protected */
                public E colSetValue(int index, E e) {
                    throw new UnsupportedOperationException("not a map");
                }

                /* Access modifiers changed, original: protected */
                public void colRemoveAt(int index) {
                    ArraySet.this.removeAt(index);
                }

                /* Access modifiers changed, original: protected */
                public void colClear() {
                    ArraySet.this.clear();
                }
            };
        }
        return this.mCollections;
    }

    public Iterator<E> iterator() {
        return getCollection().getKeySet().iterator();
    }

    public boolean containsAll(Collection<?> collection) {
        for (Object contains : collection) {
            if (!contains(contains)) {
                return false;
            }
        }
        return true;
    }

    public boolean addAll(Collection<? extends E> collection) {
        ensureCapacity(this.mSize + collection.size());
        boolean added = false;
        for (E value : collection) {
            added |= add(value);
        }
        return added;
    }

    public boolean removeAll(Collection<?> collection) {
        boolean removed = false;
        for (Object value : collection) {
            removed |= remove(value);
        }
        return removed;
    }

    public boolean retainAll(Collection<?> collection) {
        boolean removed = false;
        for (int i = this.mSize - 1; i >= 0; i--) {
            if (!collection.contains(this.mArray[i])) {
                removeAt(i);
                removed = true;
            }
        }
        return removed;
    }
}
