package ru.skillbranch.skillarticles.data.repositories

import java.lang.StringBuilder
import java.util.regex.Pattern

object MarkdownParser {

    private val LINE_SEPARATOR = System.getProperty("line.separator") ?: "\n"

    //group regex
    private const val UNORDERED_LIST_ITEM_GROUP = "(^[*+-] .+$)"
    private const val HEADER_GROUP = "(^#{1,6} .+?$)"
    private const val QUOTE_GROUP = "(^> .+?$)"
    private const val ITALIC_GROUP =
        "((?<!\\*)\\*[^*].*?[^*]?\\*(?!\\*)|(?<!_)_[^_].*?[^_]?_(?!_))"
    private const val BOLD_GROUP =
        "((?<!\\*)\\*{2}[^*].*?[^*]?\\*{2}(?!\\*)|(?<!_)_{2}[^_].*?[^_]?_{2}(?!_))"
    private const val STRIKE_GROUP = "((?<!~)~{2}[^~].*?[^~]?~{2}(?!~))"
    private const val RULE_GROUP = "(^[-_*]{3}$)"
    private const val INLINE_GROUP = "((?<!`)`[^`\\s].*?[^`\\s]?`(?!`))"
    private const val LINK_GROUP = "(\\[[^\\[\\]]*?]\\(.+?\\)|^\\[*?]\\(.*?\\))"
    private const val ORDERED_LIST_ITEM_GROUP = "(^[0-9]+?\\. .+$)"
    private const val CODE_BLOCK_GROUP = "^`{3}\\S([^`{3}]*|\\n)`{3}$"

    //result regex
    private const val MARKDOWN_GROUPS =
        "$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP|$QUOTE_GROUP" +
                "|$ITALIC_GROUP|$BOLD_GROUP|$STRIKE_GROUP|$RULE_GROUP|$INLINE_GROUP" +
                "|$LINK_GROUP|$ORDERED_LIST_ITEM_GROUP|$CODE_BLOCK_GROUP"

    private val elementsPattern by lazy { Pattern.compile(MARKDOWN_GROUPS, Pattern.MULTILINE) }

    /**
     * parse markdown text to elements
     */
    fun parse(string: String): MarkdownText {
        val elements = mutableListOf<Element>()
        elements.addAll(
            findElements(
                string
            )
        )
        return MarkdownText(
            elements
        )
    }

    /**
     * clear markdown text to string without markdown characters
     */
    fun clear(string: String?): String? {
        string ?: return null
        val stringBuilder = StringBuilder()
        parse(
            string
        ).elements.map {
            if (it.elements.isEmpty())
                stringBuilder.append(it.text)
            else
                stringBuilder.append(
                    clear(
                        it.text.toString()
                    )
                )
        }
        return stringBuilder.toString()
    }

    /**
     * find markdown elements in markdown text
     */
    private fun findElements(string: CharSequence): List<Element> {
        val parents = mutableListOf<Element>()
        val matcher = elementsPattern.matcher(string)
        var lastStartIndex = 0

        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()

            //if something is found then everything before - TEXT
            if (lastStartIndex < startIndex) {
                parents.add(
                    Element.Text(
                        string.subSequence(
                            lastStartIndex,
                            startIndex
                        )
                    )
                )
            }

            //found text
            var text: CharSequence

            //groups range or iterate by groups
            val groups = 1..11
            var group = -1
            for (gr in groups) {
                if (matcher.group(gr) != null) {
                    group = gr
                    break
                }
            }

            when (group) {

                //not found -> break
                -1 -> break@loop

                //UNORDERED_LIST_ITEM_GROUP
                1 -> {
                    //text without "*. "
                    text = string.subSequence(startIndex.plus(2), endIndex)

                    //find inner elements
                    val subs =
                        findElements(
                            text
                        )
                    val element =
                        Element.UnorderedListItem(
                            text,
                            subs
                        )
                    parents.add(element)

                    //next frind start from position "endIndex" (last regex character)
                    lastStartIndex = endIndex
                }

                //HEADER_GROUP
                2 -> {
                    val reg = "^#{1,6}".toRegex().find(string.subSequence(startIndex, endIndex))
                    val level = reg!!.value.length

                    //text without "{#} "
                    text = string.subSequence(startIndex.plus(level.inc()), endIndex)

                    val element =
                        Element.Header(
                            level,
                            text
                        )
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //QUOTE_GROUP
                3 -> {
                    //text without "> "
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    val subelements =
                        findElements(
                            text
                        )
                    val element =
                        Element.Quote(
                            text,
                            subelements
                        )
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //ITALIC_GROUP
                4 -> {
                    //text without "*{}*"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val subelements =
                        findElements(
                            text
                        )
                    val element =
                        Element.Italic(
                            text,
                            subelements
                        )
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //BOLD_GROUP
                5 -> {
                    //text without "**{}**"
                    text = string.subSequence(startIndex.plus(2), endIndex.plus(-2))
                    val subelements =
                        findElements(
                            text
                        )
                    val element =
                        Element.Bold(
                            text,
                            subelements
                        )
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //STRIKE_GROUP
                6 -> {
                    //text without "~~{}~~"
                    text = string.subSequence(startIndex.plus(2), endIndex.plus(-2))
                    val subelements =
                        findElements(
                            text
                        )
                    val element =
                        Element.Strike(
                            text,
                            subelements
                        )
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //RULE_GROUP
                7 -> {
                    //text without "***" insert empty character
                    val element =
                        Element.Rule()
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //INLINE_GROUP
                8 -> {
                    //text without "`{}`"
                    text = string.substring(startIndex.inc(), endIndex.dec())
                    val element =
                        Element.InlineCode(
                            text
                        )
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //LINK_GROUP
                9 -> {
                    //full text from regex
                    text = string.subSequence(startIndex, endIndex)
                    val (title: String, link: String) =
                        "\\[(.*)]\\((.*)\\)".toRegex().find(text)!!.destructured
                    val element =
                        Element.Link(
                            link,
                            title
                        )
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //ORDERED_LIST_ITEM_GROUP
                10 -> {
                    val reg = "^[0-9]+\\.".toRegex().find(string.subSequence(startIndex, endIndex))
                    val order = reg!!.value

                    //text without "{1. } "
                    text = string.subSequence(startIndex.plus(order.length.inc()), endIndex)
                    val subs =
                        findElements(
                            text
                        )

                    val element =
                        Element.OrderedListItem(
                            order,
                            text,
                            subs
                        )
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                11 -> {
                    //text without "```{}```}
                    val text = string.subSequence(startIndex.plus(3), endIndex.plus(-3))
                    val rows = text.split("\n".toRegex())
                    if (rows.size == 1) {
                        val element =
                            Element.BlockCode(
                                Element.BlockCode.Type.SINGLE,
                                rows.first()
                            )
                        parents.add(element)
                    } else {
                        for (i in rows.indices) {
                            val element =
                                Element.BlockCode(
                                    when (i) {
                                        0 -> Element.BlockCode.Type.START
                                        rows.size - 1 -> Element.BlockCode.Type.END
                                        else -> Element.BlockCode.Type.MIDDLE
                                    },
                                    rows[i] + if (i < rows.size - 1) "\n" else ""
                                )
                            parents.add(element)
                        }
                    }

                    lastStartIndex = endIndex
                }
            }
        }

        if (lastStartIndex < string.length) {
            val text = string.subSequence(lastStartIndex, string.length)
            parents.add(
                Element.Text(
                    text
                )
            )
        }

        return parents
    }
}

data class MarkdownText(val elements: List<Element>)

sealed class Element() {

    abstract val text: CharSequence
    abstract val elements: List<Element>

    data class Text(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class UnorderedListItem(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Header(
        val level: Int = 1,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Quote(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Italic(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Bold(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Strike(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Rule(
        override val text: CharSequence = " ", //for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class InlineCode(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Link(
        val link: String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class OrderedListItem(
        val order: String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class BlockCode(
        val type: Type = Type.MIDDLE,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element() {
        enum class Type { START, END, MIDDLE, SINGLE }
    }

}