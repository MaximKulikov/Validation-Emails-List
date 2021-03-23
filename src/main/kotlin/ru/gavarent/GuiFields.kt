package ru.gavarent

import java.io.File
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

//@Singleton
class GuiFields {
   val goodEmails: MutableList<String> = mutableListOf()
   val badEmails: MutableList<String> = mutableListOf()
   var realEmail = ""
   var ehlo = ""
   var checkList: File? = null
   var blackList: File? = null
   var whiteList: File? = null

   var onChange: ((Boolean) -> Unit)? = null
   var jobFinish: Boolean by Delegates.observable(false) { _, _, newValue ->
      println("Call jobFinish")
      onChange?.invoke(newValue)
   }
}
