package io.github.martinschneider.juvavum.utils

import java.util.*

class Stack<E> : LinkedList<E>() {
    fun peekSecond(): E? {
        val first = pop()
        val second = peekFirst()
        push(first)
        return second
    }
}