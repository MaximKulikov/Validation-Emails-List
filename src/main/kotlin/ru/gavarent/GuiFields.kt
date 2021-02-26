package ru.gavarent

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.io.File

class GuiFields {
   val goodEmails: MutableList<String> = mutableListOf()
   val badEmails: MutableList<String> = mutableListOf()
   var realEmail = ""
   var ehlo = ""
   var checkList : File? = null
   var blackList : File? = null
   var whiteList : File? = null
}
