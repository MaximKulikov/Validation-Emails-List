import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.KeyStroke
import androidx.compose.ui.window.Menu
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.MenuItem
import ru.gavarent.EmailApplication
import kotlin.system.exitProcess

fun main() {
   val emailApplication = EmailApplication()

   Window(
      title = "Email Validation Tool",
      size = IntSize(700, 500),
      /*icon = ,*/
      menuBar = getMenuBar(),
      onDismissRequest = {
         println("User close app")
      }
   ) {
      MaterialTheme {
         Row(
            modifier = Modifier
         ) {
            Column(
               modifier = Modifier
                  .width(450.dp)
                  .height(400.dp)
            ) {

               val realEmail = remember { mutableStateOf("") }
               val ehlo = remember { mutableStateOf("") }
               val checkList = remember { mutableStateOf("") }
               val blackList = remember { mutableStateOf("") }
               val whiteList = remember { mutableStateOf("") }

               Row(
                  verticalAlignment= Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth()) {
                  Text(
                     "Real email",
                     modifier = Modifier.width(100.dp)
                  )
                  TextField(
                     value = realEmail.value,
                     onValueChange = {
                        realEmail.value = it
                     },
                     singleLine = true
                  )
               }
               Spacer(Modifier.width(8.dp).height(8.dp))
               Row(
                  verticalAlignment= Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth()) {
                  Text(
                     "HELO/EHLO answer",
                     modifier = Modifier.width(100.dp)
                  )
                  TextField(
                     value = ehlo.value,
                     onValueChange = {
                        ehlo.value = it
                     },
                     singleLine = true
                  )
               }
               Spacer(Modifier.width(8.dp).height(8.dp))
               Row(verticalAlignment= Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth()) {
                  Text(
                     "Check List",
                     modifier = Modifier.width(100.dp)
                  )
                  TextField(
                     value = checkList.value,
                     onValueChange = {
                        checkList.value = it
                     },
                     singleLine = true
                  )
                  Spacer(Modifier.width(8.dp).height(8.dp))
                  Button(onClick = {},
                  modifier = Modifier.width(24.dp).height(24.dp)) {
                     Text("...")
                  }
               }
               Spacer(Modifier.width(8.dp).height(8.dp))
               Row(verticalAlignment= Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth()) {
                  Text(
                     "Black List",
                     modifier = Modifier.width(100.dp)
                  )
                  TextField(
                     value = blackList.value,
                     onValueChange = {
                        blackList.value = it
                     },
                     singleLine = true
                  )
                  Spacer(Modifier.width(8.dp).height(8.dp))
                  Button(onClick = {},
                     modifier = Modifier.width(24.dp).height(24.dp)) {
                     Text("...")
                  }
               }
               Spacer(Modifier.width(8.dp).height(8.dp))
               Row(
                  verticalAlignment= Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth()) {
                  Text(
                     "White List",
                     modifier = Modifier.width(100.dp)
                  )
                                  TextField(
                     value = whiteList.value,
                     onValueChange = {
                        whiteList.value = it
                     },
                     singleLine = true
                  )
                  Spacer(Modifier.width(8.dp).height(8.dp))
                  Button(onClick = {},
                     modifier = Modifier.width(24.dp).height(24.dp)) {
                     Text("...")
                  }
               }


            }
            Column(
               modifier = Modifier
                  .width(250.dp)
                  .height(400.dp)
                  .background(Color.Green)
            ) { }
         }

      }
   }
}

fun getMenuBar(): MenuBar {
   val fileMenu = Menu(
      name = "File",
      item = arrayOf(
         MenuItem(
            name = "Exit",
            onClick = { exitProcess(0) },
            shortcut = KeyStroke(Key.X)
         )
      )
   )
   val helpMenu = Menu(
      name = "Help",
      item = arrayOf(
         MenuItem(
            name = "Web Site",
            onClick = {}
         )
      )
   )
   return MenuBar(fileMenu, helpMenu)
}
