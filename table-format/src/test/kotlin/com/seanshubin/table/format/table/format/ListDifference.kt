package com.seanshubin.table.format.table.format

object ListDifference {
    fun <T> compare(first: List<T>, second: List<T>): Difference = compare("a", first, "b", second)

    fun <T> compare(aCaption: String, a: List<T>, bCaption: String, b: List<T>): Difference {
        var index = 0
        val messageLines = mutableListOf<String>()
        var done = false
        var isSame = true
        while (!done) {
            val p = Pair(index < a.size, index < b.size)
            when (p) {
                Pair(false, false) -> {
                    done = true
                }
                Pair(false, true) -> {
                    messageLines.add("different-$aCaption[$index]: <missing>")
                    messageLines.add("different-$bCaption[$index]: ${b[index]}")
                    done = true
                    isSame = false
                }
                Pair(true, false) -> {
                    messageLines.add("different-$aCaption[$index]: ${a[index]}")
                    messageLines.add("different-$bCaption[$index]: <missing>")
                    done = true
                    isSame = false
                }
                Pair(true, true) -> {
                    if (a[index] == b[index]) {
                        messageLines.add("same[$index]: ${a[index]}")
                        index++
                    } else {
                        messageLines.add("different-$aCaption[$index]: ${a[index]}")
                        messageLines.add("different-$bCaption[$index]: ${b[index]}")
                        done = true
                        isSame = false
                    }
                }
            }
        }
        val firstLine = if (isSame) "no differences" else "different at index $index"
        val lastLines = if (isSame) listOf() else listOf("remaining elements skipped")
        return Difference(isSame, listOf(firstLine) + messageLines + lastLines)
    }

    fun compare(aCaption: String, a: String, bCaption: String, b: String): Difference =
            compare(aCaption, a.toLines(), bCaption, b.toLines())

    fun compare(a: String, b: String): Difference = compare("a", a, "b", b)

    private fun String.toLines(): List<String> = this.split("\r\n", "\r", "\n")

    data class Difference(val isSame: Boolean, val messageLines: List<String>)
}
