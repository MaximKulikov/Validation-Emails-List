package ru.gavarent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.PrintStream
import java.net.Socket
import kotlin.random.Random

class TalkWithSMTP(private val guiFields: GuiFields) {

   @Suppress("BlockingMethodInNonBlockingContext")
   suspend fun execute(domain: Domain, emails: List<String>) {
      // TODO: 18.03.2021 добавить на гуи прогрес бар с 0 отметкой
      withContext(Dispatchers.Main) {
         guiFields.onAddDomainProgress?.invoke(domain.name)
      }
      val chunkedEmails: List<List<String>> = emails.chunked(10)
      val host = domain.attribute.get().toString().split(" ")[1]
      val ehlo = guiFields.ehlo
      val mailFrom = "MAIL FROM:< ${guiFields.realEmail}>"

      val times = Random.nextInt(10, 500)
      repeat(times) {
         val value = it / times.toFloat()
         withContext(Dispatchers.Main) {
            guiFields.valueDomainProgressMap[domain.name]?.invoke(value)
         }
         delay(20)
      }

      delay(1500)
      withContext(Dispatchers.Main) {
         println("remove 1 ${domain.name}")
         guiFields.onRemoveDomainProgress?.invoke(domain.name)
      }


      chunkedEmails.forEach { partOfEmails ->

         Resources(Socket(host, SMTP_PORT))
            .useMe {
               it.bufferedReader.readLine()
               it.printStream.sendMessage(ehlo)
               delay(5)
               it.bufferedReader.readLine()

               it.printStream.sendMessage(mailFrom)

               delay(5)
               it.bufferedReader.readLine()
               // TODO: 18.03.2021 сервер может оборвать соединение в любо момент. обработать
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
