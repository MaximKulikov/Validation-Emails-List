package ru.gavarent

import java.io.File

fun File.saveContent(lines: List<String>) {
   this.writeText(lines.joinToString(separator = System.lineSeparator()))
}


