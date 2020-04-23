package io.github.martinschneider.juvavum.utils;

import java.util.LinkedList;

public class Stack<E> extends LinkedList<E> {
    public E peekSecond()
    {
        E first = pop();
        E second = peekFirst();
        push(first);
        return second;
    }
}
