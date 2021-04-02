package ru.gavarent

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.StandardOpenOption

fun File.saveContent(emailList: List<String>) {
   Files.writeString(
      this.absoluteFile.toPath(),
      emailList.joinToString(separator = System.lineSeparator()),
      UTF_8,
      StandardOpenOption.CREATE, StandardOpenOption.WRITE
   )
}


