package ru.gavarent

import kotlinx.coroutines.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.naming.directory.Attribute
import javax.naming.directory.InitialDirContext

@ExperimentalStdlibApi
class Validator(
   private val guiFields: GuiFields
) {

   fun execute() {
      val checkList = guiFields.checkList ?: return
      val loadEmailsToProcess = loadEmailsToProcess(checkList)
      val unsubscribedList = guiFields.blackList?.let { loadEmailsToProcess } ?: listOf()
      val whiteList = guiFields.whiteList?.let { loadEmailsToProcess } ?: listOf()

      val nonValidationFinalEmails = buildList {
         addAll(loadEmailsToProcess)
         removeAll(unsubscribedList)
         removeAll(whiteList)
      }
      guiFields.goodEmails.addAll(whiteList)


      //Простая проверка паттерна адреса
      val domainNameEmailsMap = filterListFirstStage(nonValidationFinalEmails)
      val domainEmailsMap = filterListSecondStage(domainNameEmailsMap)


      runBlocking(Dispatchers.Default) {


/*         val jobs = mutableListOf<Job>()
         domainEmailsMap.entries.forEach {
            val job = GlobalScope.launch(Dispatchers.IO) {
               TalkWithSMTP(guiFields).run(it.key, it.value)
            }
            jobs.add(job)
         }

         jobs.joinAll()*/
         println("Ждем 3 сек")
         delay(TimeUnit.SECONDS.toMillis(4))
         println("Подождали")
         guiFields.jobFinish = true
      }
   }

   private fun filterListSecondStage(map: Map<String, List<String>>): Map<Domain, List<String>> {
      for (entry in map) {
         doLookup(entry.key)?.let {

         } ?: run {
            guiFields.badEmails.addAll(entry.value.map { "$it;501;Mail server not exist" })
         }
      }

      val attributeMap = map.mapKeys {
         doLookup(it.key)
      }

      attributeMap[null]?.let {
         guiFields.badEmails.addAll(it.map { email -> "$email;501;Mail server not exist" })
      }

      return attributeMap
         .filter {
            it.key != null
         }
         .mapKeys {
            it.key!!
         }
   }

   private fun doLookup(domainName: String): Domain? {
      val attribute: Attribute? = InitialDirContext(
         Hashtable<String, String>().apply {
            put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory")
         })
         .getAttributes(domainName, arrayOf(MX_RECORD))
         .get(MX_RECORD)
      return attribute?.let { Domain(domainName, it) }
   }

   private fun filterListFirstStage(emails: List<String>): Map<String, List<String>> {
      val map = emails
         .groupBy { it.contains('@') }

      map[false]?.also {
         guiFields.badEmails.addAll(it)
      }

      val domainEmailMap = map[true]?.run {
         groupBy { it.split('@')[1] }
      }
      return domainEmailMap ?: mapOf()
   }

   private fun loadEmailsToProcess(file: File): List<String> {
      return file.readLines().asSequence()
         .map { it.trim() }
         .filter { it.isNotEmpty() }
         .distinct()
         .toList()
   }

   private fun saveAllToFile(file: File, lines: List<String>) {
      file.writeText(lines.joinToString(separator = System.lineSeparator()))
   }

   companion object {
      private const val MX_RECORD = "MX"
   }
}