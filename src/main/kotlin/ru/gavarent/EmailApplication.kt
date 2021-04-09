package ru.gavarent

import androidx.compose.ui.text.intl.Locale

@ExperimentalStdlibApi
class EmailApplication {

   val guiFields = GuiFields()
   private val validator = Validator(guiFields)
   val appRes = ResourceMapFactory()

   private val lang = Locale.current

   @ExperimentalStdlibApi
   suspend fun process() {
      println("Начали парсить файлы")
      validator.execute()


   }

}
