package ru.skillbranch.skillarticles.extensions

fun List<Pair<Int, Int>>.groupByBounds(bounds: List<Pair<Int, Int>>): List<List<Pair<Int, Int>>> {

    var res = mutableListOf<List<Pair<Int, Int>>>()

    bounds.forEach { (boundStart, boundEnd) ->

        val resultList = mutableListOf<Pair<Int, Int>>()

        val filteredList =
            this.filter { (start, end) ->
                when {
                    start in boundStart.inc()..boundEnd -> true
                    end in boundStart.inc()..boundEnd -> true
                    boundStart in start..end.dec() -> true
                    else -> false
                }
            }

        when (filteredList.size) {

            0 -> {}

            1 -> {
                if (filteredList.first().first < boundStart || filteredList.last().second > boundEnd)
                    resultList.add(Pair(boundStart, boundEnd))
                else resultList.addAll(filteredList)
            }

            else -> {
                if (filteredList.first().first < boundStart)
                    resultList.add(Pair(boundStart, filteredList.first().second))
                else resultList.add(filteredList.first())

                if (filteredList.size > 2)
                    resultList.addAll(filteredList.subList(1, filteredList.lastIndex))

                if (filteredList.last().second > boundEnd)
                    resultList.add(Pair(filteredList.last().first, boundEnd))
                else resultList.add(filteredList.last())
            }
        }
        res.add(resultList)
    }
    res
    return res
}