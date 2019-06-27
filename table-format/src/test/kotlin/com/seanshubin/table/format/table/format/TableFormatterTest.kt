package com.seanshubin.table.format.table.format

import org.junit.Test
import kotlin.test.assertTrue
import com.seanshubin.table.format.table.format.TableFormatter.Justify.Left
import com.seanshubin.table.format.table.format.TableFormatter.Justify.Right


class TableFormatterTest {
    val boxDrawing = RowStyleTableFormatter.boxDrawing
    val plainText = RowStyleTableFormatter.plainText
    val minimal = RowStyleTableFormatter.minimal

    @Test
    fun fancyTable() {
        val tableFormatter = boxDrawing
        val input = listOf(
                listOf("Alice", "Bob", "Carol"),
                listOf("Dave", "Eve", "Mallory"),
                listOf("Peggy", "Trent", "Wendy")
        )
        val expected = listOf(
                "╔═════╤═════╤═══════╗",
                "║Alice│Bob  │Carol  ║",
                "╟─────┼─────┼───────╢",
                "║Dave │Eve  │Mallory║",
                "╟─────┼─────┼───────╢",
                "║Peggy│Trent│Wendy  ║",
                "╚═════╧═════╧═══════╝"
        )
        val actual = tableFormatter.format(input)
        assertLinesEqual(expected, actual)
    }

    @Test
    fun plainTextTable() {
        val tableFormatter = plainText
        val input = listOf(
                listOf("Alice", "Bob", "Carol"),
                listOf("Dave", "Eve", "Mallory"),
                listOf("Peggy", "Trent", "Wendy")
        )
        val expected = listOf(
                "/-----+-----+-------\\",
                "|Alice|Bob  |Carol  |",
                "+-----+-----+-------+",
                "|Dave |Eve  |Mallory|",
                "+-----+-----+-------+",
                "|Peggy|Trent|Wendy  |",
                "\\-----+-----+-------/"
        )
        val actual = tableFormatter.format(input)
        assertLinesEqual(expected, actual)
    }

    @Test
    fun minimalTable() {
        val tableFormatter = minimal
        val input = listOf(
                listOf("Alice", "Bob", "Carol"),
                listOf("Dave", "Eve", "Mallory"),
                listOf("Peggy", "Trent", "Wendy")
        )
        val expected = listOf(
                "Alice Bob   Carol  ",
                "Dave  Eve   Mallory",
                "Peggy Trent Wendy  "
        )
        val actual = tableFormatter.format(input)
        assertLinesEqual(expected, actual)
    }

    @Test
    fun leftAndRightJustify() {
        val tableFormatter = boxDrawing
        val input = listOf(
                listOf("left justify column name", "default justification column name", "right justify column name"),
                listOf(Left("left"), "default", Right("right")),
                listOf(Left(null), null, Right(null)),
                listOf(Left(1), 1, Right(1)),
                listOf(Left(2), 2, Right(2)),
                listOf(Left(3), 3, Right(3))
        )
        val expected = listOf(
                "╔════════════════════════╤═════════════════════════════════╤═════════════════════════╗",
                "║left justify column name│default justification column name│right justify column name║",
                "╟────────────────────────┼─────────────────────────────────┼─────────────────────────╢",
                "║left                    │default                          │                    right║",
                "╟────────────────────────┼─────────────────────────────────┼─────────────────────────╢",
                "║null                    │                             null│                     null║",
                "╟────────────────────────┼─────────────────────────────────┼─────────────────────────╢",
                "║1                       │                                1│                        1║",
                "╟────────────────────────┼─────────────────────────────────┼─────────────────────────╢",
                "║2                       │                                2│                        2║",
                "╟────────────────────────┼─────────────────────────────────┼─────────────────────────╢",
                "║3                       │                                3│                        3║",
                "╚════════════════════════╧═════════════════════════════════╧═════════════════════════╝"
        )
        val actual = tableFormatter.format(input)
        assertLinesEqual(expected, actual)
    }

    @Test
    fun leftAndRightJustifySomethingFar() {
        val tableFormatter = boxDrawing
        val expected = listOf("╔═╗", "║a║", "╚═╝")
        val actualNoJustify = tableFormatter.format(listOf(listOf("a")))
        val actualJustifyLeft = tableFormatter.format(listOf(listOf(Left("a"))))
        val actualJustifyRight = tableFormatter.format(listOf(listOf(Right("a"))))
        assertLinesEqual(actualNoJustify, expected)
        assertLinesEqual(actualJustifyLeft, expected)
        assertLinesEqual(actualJustifyRight, expected)
    }

    @Test
    fun testNoColumns() {
        val tableFormatter = boxDrawing
        assertLinesEqual(tableFormatter.format(listOf(listOf())), listOf("╔╗", "║║", "╚╝"))
    }

    @Test
    fun testNoRows() {
        val tableFormatter = boxDrawing
        assertLinesEqual(tableFormatter.format(listOf()), listOf("╔╗", "╚╝"))
    }

    @Test
    fun replaceEmptyCellsWithBlankCells() {
        val tableFormat = boxDrawing
        val input = listOf(
                listOf("Alice", "Bob", "Carol"),
                listOf("Dave", "Eve"),
                listOf("Peggy", "Trent", "Wendy")
        )
        val expected = listOf(
                "╔═════╤═════╤═════╗",
                "║Alice│Bob  │Carol║",
                "╟─────┼─────┼─────╢",
                "║Dave │Eve  │     ║",
                "╟─────┼─────┼─────╢",
                "║Peggy│Trent│Wendy║",
                "╚═════╧═════╧═════╝"
        )
        val actual = tableFormat.format(input)
        assertLinesEqual(expected, actual)
    }

    @Test
    fun escapeCells() {
        val tableFormat = boxDrawing.copy(cellToString = TableFormatter.escapeString)
        val input = listOf(listOf("foo\nbar"))
        val expected = listOf(
                """╔════════╗""",
                """║foo\nbar║""",
                """╚════════╝"""
        )
        val actual = tableFormat.format(input)
        assertLinesEqual(expected, actual)
    }

    @Test
    fun truncateCells() {
        val tableFormat = boxDrawing.copy(cellToString = TableFormatter.escapeAndTruncateString(10))
        val input = listOf(listOf("a".repeat(100)))
        val expected = listOf(
                "╔═════════════════════════════════════════════╗",
                "║<100 characters, showing first 10> aaaaaaaaaa║",
                "╚═════════════════════════════════════════════╝"
        )
        val actual = tableFormat.format(input)
        assertLinesEqual(expected, actual)
    }

    private fun assertLinesEqual(expected: List<String>, actual: List<String>) {
        val difference = ListDifference.compare(
                "expected", expected,
                "actual  ", actual
        )
        assertTrue(difference.isSame, difference.messageLines.joinToString("\n"))
    }
}
