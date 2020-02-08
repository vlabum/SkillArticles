package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(query: String, ignoreCase: Boolean = true): List<Int> {
    val list = arrayListOf<Int>()

    if (this == null) return list

    var index: Int
    var startIndex = 0
    do {
        index = this.indexOf(query, startIndex, ignoreCase)
        if (index >= 0) {
            list.add(index)
            startIndex = index.inc()
        }
    } while (index > -1 && startIndex < this.length)

    return list
}