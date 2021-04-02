package ru.gavarent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.PrintStream
import java.net.Socket

class TalkWithSMTP(private val guiFields: GuiFields) {

   @Suppress("BlockingMethodInNonBlockingContext")
   suspend fun execute(domain: Domain, emails: List<String>) {
      // TODO: 18.03.2021 проверить на гуи прогрес бар с текущим положением
      withContext(Dispatchers.Main) {
         guiFields.onAddDomainProgress?.invoke(domain.name)
      }
      val chunkedSize = 10
      val chunkedEmails: List<List<String>> = emails.chunked(chunkedSize)
      val host = domain.attribute.get().toString().split(" ")[1]
      val ehlo = guiFields.ehlo
      val mailFrom = "MAIL FROM:< ${guiFields.realEmail}>"

      var counter = 0;
      chunkedEmails.forEach { partOfEmails ->
         val subProgress = counter * chunkedSize / emails.size.toFloat()
         guiFields.valueDomainProgressMap[domain.name]?.invoke(subProgress)
         counter++
         Resources(Socket(host, SMTP_PORT))
            .useMe {
               it.bufferedReader.readLine()
               it.printStream.sendMessage(ehlo)
               delay(5)
               it.bufferedReader.readLine()

               it.printStream.sendMessage(mailFrom)

               delay(5)
               it.bufferedReader.readLine()
               partOfEmails.forEach { email ->
                  it.printStream.sendMessage("RCPT TO:<$email>")
                  delay(100)
                  val answer = it.bufferedReader.readLine()

                  if (answer.startsWith("250 ")) {
                     guiFields.goodEmails.add(email)
                  } else {
                     guiFields.badEmails.add("$email;${answer.substring(0, 3)};$answer")
                  }
               }
            }
      }
   }

   companion object {
      const val SMTP_PORT = 25;
   }
}

private infix fun PrintStream.sendMessage(message: String) {
   println(message)
   flush()
}
