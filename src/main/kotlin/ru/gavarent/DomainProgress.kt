package ru.gavarent

class DomainProgress(val name: String, guiFields: GuiFields) {
   var progressValue = 0f

   init {
      guiFields.valueDomainProgressMap[name] = {
         progressValue = it
      }
   }
}