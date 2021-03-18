package ru.gavarent

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.io.File
import java.util.function.Consumer
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

   var onChange: (property: KProperty<*>, oldValue: Boolean, newValue: Boolean) -> Unit = { _, _, newValue ->
      println("Что-то пошло не по плану")
   }
   var jobFinish: Boolean by Delegates.observable(false, onChange)
}
