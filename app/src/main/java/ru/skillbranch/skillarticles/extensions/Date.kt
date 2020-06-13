package ru.skillbranch.skillarticles.extensions

import java.text.SimpleDateFormat
import java.util.*

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.shortFormat(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time
    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val diff = date.time - this.time
    return getDiffStr(diff)
}

private fun getDiffStr(diff: Long): String {
    val diffAbsolute = Math.abs(diff)
    val isFuture = diff < 0
    val diffStr =
        when {
            diffAbsolute / SECOND <= 75 -> TimeUnits.SECOND to diffAbsolute / SECOND
            diffAbsolute / MINUTE <= 75 -> TimeUnits.MINUTE to diffAbsolute / MINUTE
            diffAbsolute / HOUR <= 26 -> TimeUnits.HOUR to diffAbsolute / HOUR
            else -> TimeUnits.DAY to diffAbsolute / DAY
        }.let {
            when {
                it.first == TimeUnits.SECOND && it.second <= 1 && isFuture -> "сейчас"
                it.first == TimeUnits.SECOND && it.second <= 1 -> "только что"
                it.first == TimeUnits.SECOND && it.second <= 45 -> "несколько секунд"
                it.first == TimeUnits.SECOND && it.second <= 75 -> "минуту"
                it.first == TimeUnits.MINUTE && it.second <= 45 -> TimeUnits.MINUTE.plural(it.second.toInt())
                it.first == TimeUnits.MINUTE && it.second <= 75 -> "час"
                it.first == TimeUnits.HOUR && it.second <= 22 -> TimeUnits.HOUR.plural(it.second.toInt())
                it.first == TimeUnits.HOUR && it.second <= 26 -> "день"
                it.first == TimeUnits.DAY && it.second <= 360 -> TimeUnits.DAY.plural(it.second.toInt())
                else -> if (isFuture) "более чем через год" else "более года назад"
            }
        }.let {
            if (diffAbsolute / SECOND <= 1 || diffAbsolute / DAY > 360) it
            else if (isFuture) "через $it"
            else "$it назад"
        }
    return diffStr
}

enum class QuantityDesignation {
    ONE,
    FEW,
    LOT;

    fun getValue(number: Long): QuantityDesignation {
        val lastDigit = number % 10
        val last2Digit = number % 100
        return when {
            last2Digit in 10..14 -> LOT
            lastDigit == 0L -> LOT
            lastDigit == 1L -> ONE
            lastDigit in 2..4 -> FEW
            else -> LOT
        }
    }
}


enum class TimeUnits {

    SECOND {
        override fun plural(value: Int): String {
            return when(QuantityDesignation.ONE.getValue(value.toLong())) {
                QuantityDesignation.ONE -> "$value секунду"
                QuantityDesignation.FEW -> "$value секунды"
                QuantityDesignation.LOT -> "$value секунд"
            }
        }
    },
    MINUTE {
        override fun plural(value: Int): String {
            return when(QuantityDesignation.ONE.getValue(value.toLong())) {
                QuantityDesignation.ONE -> "$value минуту"
                QuantityDesignation.FEW -> "$value минуты"
                QuantityDesignation.LOT -> "$value минут"
            }
        }
    },
    HOUR {
        override fun plural(value: Int): String {
            return when(QuantityDesignation.ONE.getValue(value.toLong())) {
                QuantityDesignation.ONE -> "$value час"
                QuantityDesignation.FEW -> "$value часа"
                QuantityDesignation.LOT -> "$value часов"
            }
        }
    },
    DAY {
        override fun plural(value: Int): String {
            return when(QuantityDesignation.ONE.getValue(value.toLong())) {
                QuantityDesignation.ONE -> "$value день"
                QuantityDesignation.FEW -> "$value дня"
                QuantityDesignation.LOT -> "$value дней"
            }
        }
    };

    abstract fun plural(value: Int): String

}
