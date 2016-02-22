package com.sdi.ge.entity;

import java.util.*;

/**
 * Represents a list of entities.
 */
public class EntityList implements List<Entity> {

    /**
     * The entities stored in this EntityList.
     */
    private Entity[] entities;

    /**
     * The number of entities currently stored.
     */
    private int size = 0;

    public EntityList(int capacity) {
        entities = new Entity[capacity];
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
    public boolean contains(Object o) {
        for (Entity entity : entities) {
            if (entity == o)
                return true;
        }

        return false;
    }

    @Override
    public Iterator<Entity> iterator() {
        return listIterator();
    }

    @Override
    public Entity[] toArray() {
        Entity[] array = new Entity[size];
        int index = 0;

        for (Entity entity : entities) {
            if (entity != null)
                array[index++] = entity;

            if (index == size)
                break;
        }

        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        Object[] array = toArray();

        return (T[]) Arrays.copyOf(array, array.length, a.getClass());
    }

    @Override
    public boolean add(Entity entity) {
        if (entity == null)
            return false;

        int id = indexOf(null);

        if (id == -1)
            return false;

        entities[id] = entity;
        size++;

        entity.setId(id);

        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null)
            return false;

        for (int i = 0; i < entities.length; i++) {
            if (entities[i] == o) {
                entities[i] = null;
                size--;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o))
                return false;
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Entity> c) {
        boolean changed = false;

        for (Entity entity : c) {
            if (add(entity))
                changed = true;
        }

        return changed;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Entity> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;

        for (Object o : c) {
            if (remove(o))
                changed = true;
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;

        for (int i = 0; i < entities.length; i++) {
            if (entities[i] != null && !c.contains(entities[i])) {
                entities[i] = null;
                size--;
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public void clear() {
        for (int i = 0; i < entities.length; i++) {
            entities[i] = null;
        }

        size = 0;
    }

    @Override
    public Entity get(int index) {
        if (index < 0 || index >= entities.length)
            throw new IndexOutOfBoundsException();

        return entities[index];
    }

    @Override
    public Entity set(int index, Entity element) {
        if (index < 0 || index >= entities.length)
            throw new IndexOutOfBoundsException();

        Entity oldValue = entities[index];

        if (element != null && oldValue == null)
            size++;
        else if (element == null && oldValue != null)
            size--;

        entities[index] = element;

        return oldValue;
    }

    @Override
    public void add(int index, Entity element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entity remove(int index) {
        if (index < 0 || index >= entities.length)
            throw new IndexOutOfBoundsException();

        Entity oldValue = entities[index];

        if (oldValue != null) {
            entities[index] = null;
            size--;
        }

        return oldValue;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < entities.length; i++) {
            if (entities[i] == o)
                return i;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = entities.length - 1; i >= 0; i--) {
            if (entities[i] == o)
                return i;
        }

        return -1;
    }

    @Override
    public ListIterator<Entity> listIterator() {
        return new EntityListIterator();
    }

    @Override
    public ListIterator<Entity> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Entity> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    /**
     * Represents an iterator over a list of entities.
     */
    private class EntityListIterator implements ListIterator<Entity> {

        /**
         * The entities being iterated over.
         */
        private Entity[] entities;

        int cursor = 0, lastIndex;

        public EntityListIterator() {
            entities = toArray();
        }

        @Override
        public boolean hasNext() {
            return cursor < entities.length;
        }

        @Override
        public Entity next() {
            if (!hasNext())
                throw new NoSuchElementException();

            lastIndex = cursor++;
            return entities[lastIndex];
        }

        @Override
        public boolean hasPrevious() {
            return cursor > 0;
        }

        @Override
        public Entity previous() {
            if (!hasPrevious())
                throw new NoSuchElementException();

            lastIndex = --cursor;
            return entities[lastIndex];
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void remove() {
            EntityList.this.remove(entities[lastIndex]);
        }

        @Override
        public void set(Entity entity) {
            EntityList.this.set(indexOf(entities[lastIndex]), entity);
        }

        @Override
        public void add(Entity entity) {
            throw new UnsupportedOperationException();
        }
    }
}
