package ru.gavarent

@ExperimentalStdlibApi
class EmailApplication {

   val guiFields = GuiFields()
   private val validator = Validator(guiFields)

   @ExperimentalStdlibApi
   suspend fun process() {
      println("Начали парсить файлы")
      validator.execute()


   }

}
