package ru.gavarent

import java.io.File

//@Singleton
class GuiFields {
   val goodEmails: MutableList<String> = mutableListOf()
   val badEmails: MutableList<String> = mutableListOf()
   val noProcessedEmails: MutableList<String> = mutableListOf()
   var realEmail = ""
   var ehlo = ""
   var checkList: File? = null
   var blackList: File? = null
   var whiteList: File? = null

   var onTotalFinish: ((Boolean) -> Unit)? = null

   var onTotalProgress: ((Float) -> Unit)? = null

   var onAddDomainProgress: ((domainName: String) -> Unit)? = null
   var onRemoveDomainProgress: ((domainName: String) -> Unit)? = null
   var valueDomainProgressMap = mutableMapOf<String, ((value: Float) -> Unit)>()

}
