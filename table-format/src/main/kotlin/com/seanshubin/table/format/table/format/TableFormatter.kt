package com.seanshubin.table.format.table.format

interface TableFormatter {
    interface Justify {
        data class Left(val x: Any?) : Justify

        data class Right(val x: Any?) : Justify
    }

    fun format(originalRows: List<List<Any?>>): List<String>

    companion object {
        val escapeString: (Any?) -> String = { cell ->
            when (cell) {
                null -> "null"
                else -> cell.toString().escape()
            }
        }

        fun escapeAndTruncateString(max: Int): (Any?) -> String = { cell ->
            escapeString(cell).truncate(max)
        }
        private fun String.escape(): String = this.flatMap(::escapeCharToIterable).joinToString("")
        private fun escapeCharToIterable(target: Char): Iterable<Char> = escapeCharToString(target).asIterable()
        private fun escapeCharToString(target: Char): String =
                when (target) {
                    '\n' -> "\\n"
                    '\b' -> "\\b"
                    '\t' -> "\\t"
                    '\r' -> "\\r"
                    '\"' -> "\\\""
                    '\'' -> "\\\'"
                    '\\' -> "\\\\"
                    else -> target.toString()
                }

        private fun String.truncate(max: Int): String =
                if (this.length > max) "<${this.length} characters, showing first $max> ${this.substring(0, max)}"
                else this
    }
}
