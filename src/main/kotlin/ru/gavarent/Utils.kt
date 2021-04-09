package ru.gavarent

import java.awt.Desktop
import java.net.URI

class Utils {
   companion object {
      const val PROJECT_WEBSITE = "https://github.com/Trinion/Validation-Emails-List"
      fun openBrowser(link: String) {
         val os = System.getProperty("os.name").toLowerCase()
         when {
            os.startsWith("mac") -> Runtime.getRuntime().exec("open $link")
            os.contains("nix") || os.contains("nux") -> {
               val browsers = arrayOf(
                  "epiphany", "firefox", "mozilla", "konqueror",
                  "netscape", "opera", "links", "lynx"
               )
               val cmd = StringBuffer()
               for (i in browsers.indices) cmd.append("""${if (i == 0) "" else " || "}${browsers[i]} "$link" """)
               Runtime.getRuntime().exec(arrayOf("sh", "-c", cmd.toString()))
            }
            os.startsWith("windows") -> Desktop.getDesktop().browse(URI(link))
         }
      }
   }
}