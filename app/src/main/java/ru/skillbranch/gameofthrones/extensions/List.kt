package ru.skillbranch.gameofthrones.extensions

// удаляет элементы списка с конца, включая удовлетворяющий предикату
inline fun <T> List<T>.dropLastUntil(predicate: (T) -> Boolean): List<T> {
    if (!isEmpty()) {
        val iterator = listIterator(size)
        while (iterator.hasPrevious()) {
            if (predicate(iterator.previous())) {
                return take(iterator.nextIndex())
            }
        }
    }
    return emptyList()
}