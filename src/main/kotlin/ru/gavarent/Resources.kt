package ru.gavarent;

import java.io.*
import java.net.Socket

class Resources(
   private val socket: Socket,
   private val outputStream: OutputStream = socket.getOutputStream(),
   val printStream: PrintStream = PrintStream(outputStream),
   private val inputStream: InputStream = socket.getInputStream(),
   val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
) {
   private val array: Array<Closeable> = arrayOf(socket, outputStream, printStream, inputStream, bufferedReader)

   suspend fun useMe(resources: suspend (Resources) -> Unit) {
      var exception: Throwable? = null
      try {
         return resources(this)
      } catch (e: Throwable) {
         exception = e
         throw e
      } finally {
         when (exception) {
            null -> array.forEach { it.close() }
            else -> array.forEach {
               try {
                  it.close()
               } catch (closeException: Throwable) {
                  exception.addSuppressed(closeException)
               }
            }
         }
      }
   }
}
