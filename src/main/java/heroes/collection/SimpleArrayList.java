package heroes.collection;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.RandomAccess;

/** Уровень 2: собственная коллекция — «add, get, remove, size», автоматическое расширение массива. */
public final class SimpleArrayList<E> extends AbstractList<E> implements RandomAccess {
    private static final int INITIAL_CAPACITY = 10;
    private Object[] elements = new Object[INITIAL_CAPACITY];
    private int size;

    public SimpleArrayList() {
    }

    public SimpleArrayList(Collection<? extends E> source) {
        addAll(Objects.requireNonNull(source));
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        Objects.checkIndex(index, size);
        return (E) elements[index];
    }

    @Override
    public boolean add(E element) {
        add(size, element);
        return true;
    }

    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Индекс: " + index + ", размер: " + size);
        }
        ensureCapacity(size + 1);
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
        modCount++;
    }

    @Override
    public E set(int index, E element) {
        E previous = get(index);
        elements[index] = element;
        return previous;
    }

    @Override
    public E remove(int index) {
        E removed = get(index);
        int moved = size - index - 1;
        if (moved > 0) {
            System.arraycopy(elements, index + 1, elements, index, moved);
        }
        elements[--size] = null;
        modCount++;
        return removed;
    }

    private void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity > elements.length) {
            int newCapacity = Math.max(requiredCapacity, elements.length + elements.length / 2);
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }
}
