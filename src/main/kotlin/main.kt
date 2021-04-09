import androidx.compose.desktop.AppWindowAmbient
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.KeyStroke
import androidx.compose.ui.window.Menu
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.MenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.gavarent.*
import ru.gavarent.Utils.Companion.PROJECT_WEBSITE
import java.awt.FileDialog
import java.awt.FileDialog.LOAD
import java.awt.FileDialog.SAVE
import java.io.File
import java.util.*
import kotlin.system.exitProcess

enum class JobStates {
   PREPARATION,
   IN_PROGRESS,
   FINISHED
}


@OptIn(ExperimentalStdlibApi::class)
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
      val currentWindow = AppWindowAmbient.current!!
      val jobState = remember { mutableStateOf(JobStates.PREPARATION) }
      emailApplication.guiFields.onTotalFinish = { value ->
         if (value) {
            jobState.value = JobStates.FINISHED
         }
      }
      val totalProgress = remember { mutableStateOf(0f) }
      emailApplication.guiFields.onTotalProgress = { it ->
         totalProgress.value = it
         println("${totalProgress.value}%")
      }

      GavarentTheme {
         val snackBar = remember { mutableStateOf("") }
         Column(
            modifier = Modifier.fillMaxSize()
         ) {
            Row(modifier = Modifier.height(400.dp)) {
               Column(
                  modifier = Modifier
                     .width(450.dp)
                     .height(400.dp)
                     .padding(start = 10.dp, end = 10.dp)
               ) {

                  val realEmail = remember { mutableStateOf(TextFieldValue()) }
                  val ehlo = remember { mutableStateOf(TextFieldValue()) }
                  val checkList = remember { mutableStateOf(TextFieldValue()) }
                  val blackList = remember { mutableStateOf(TextFieldValue()) }
                  val whiteList = remember { mutableStateOf(TextFieldValue()) }
                  //realEmail.value = TextFieldValue("demo@localhosh.local")
                  //ehlo.value = TextFieldValue("ehlo localhost")

                  Row(
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier.fillMaxWidth()
                  ) {
                     Text(
                        "Real email (*)",
                        modifier = Modifier.width(100.dp)
                     )
                     TextField(
                        value = realEmail.value,
                        onValueChange = {
                           realEmail.value = it
                           emailApplication.guiFields.realEmail = it.text
                        },
                        singleLine = true,
                     )
                  }
                  Spacer(Modifier.width(8.dp).height(8.dp))
                  Row(
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier.fillMaxWidth()
                  ) {
                     Text(
                        "HELO/EHLO answer (*)",
                        modifier = Modifier.width(100.dp)
                     )
                     TextField(
                        value = ehlo.value,
                        onValueChange = {
                           ehlo.value = it
                           emailApplication.guiFields.ehlo = it.text
                        },
                        singleLine = true,
                     )
                  }
                  Spacer(Modifier.width(8.dp).height(8.dp))
                  Row(
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier.fillMaxWidth()
                  ) {
                     Text(
                        "Check List (*)",
                        modifier = Modifier.width(100.dp)
                     )
                     TextField(
                        value = checkList.value,
                        onValueChange = {
                           //checkList.value = it
                        },
                        singleLine = true,
                     )
                     Spacer(Modifier.width(8.dp).height(8.dp))
                     Button(
                        onClick = {
                           FileDialog(currentWindow.window, "Файл с проверяемыми адресами").apply {
                              this.isVisible = true
                              val file: String? = this.file
                              file?.let {
                                 emailApplication.guiFields.checkList = File(this.directory, it)
                                 checkList.value = TextFieldValue(it)
                              }
                           }
                        },
                        enabled = jobState.value == JobStates.PREPARATION,
                        modifier = Modifier.width(24.dp).height(24.dp)
                     ) {
                        Text("...")
                     }
                  }
                  Spacer(Modifier.width(8.dp).height(8.dp))
                  Row(
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier.fillMaxWidth()
                  ) {
                     Text(
                        "Black List",
                        modifier = Modifier.width(100.dp)
                     )
                     TextField(
                        value = blackList.value,
                        onValueChange = {
                           //   blackList.value = it
                        },
                        singleLine = true,
                        modifier = Modifier
                     )
                     Spacer(Modifier.width(8.dp).height(8.dp))
                     Button(
                        onClick = {
                           FileDialog(currentWindow.window, "Файл с черным списком").apply {
                              this.isVisible = true
                              val file: String? = this.file
                              file?.let {
                                 emailApplication.guiFields.blackList = File(this.directory, it)
                                 blackList.value = TextFieldValue(it)
                              }
                           }
                        },
                        enabled = jobState.value == JobStates.PREPARATION,
                        modifier = Modifier.width(24.dp).height(24.dp)
                     ) {
                        Text("...")
                     }
                  }
                  Spacer(Modifier.width(8.dp).height(8.dp))
                  Row(
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier.fillMaxWidth()
                  ) {
                     Text(
                        "White List",
                        modifier = Modifier.width(100.dp)
                     )
                     TextField(
                        value = whiteList.value,
                        onValueChange = {
                           //whiteList.value = it
                        },
                        singleLine = true,
                        modifier = Modifier
                     )
                     Spacer(Modifier.width(8.dp).height(8.dp))
                     Button(
                        onClick = {
                           FileDialog(currentWindow.window, "Файл с белыми списками", LOAD).apply {
                              this.isVisible = true
                              val file: String? = this.file
                              file?.let {
                                 emailApplication.guiFields.whiteList = File(this.directory, it)
                                 whiteList.value = TextFieldValue(it)
                              }

                           }
                        },
                        enabled = jobState.value == JobStates.PREPARATION,
                        modifier = Modifier.width(24.dp).height(24.dp)
                     ) {
                        Text("...")
                     }
                  }
                  Spacer(Modifier.width(8.dp).height(8.dp))
                  Row {

                     if (jobState.value == JobStates.PREPARATION) {
                        Button(onClick = {
                           snackBar.value = ""
                           if (emailApplication.guiFields.checkList != null &&
                              emailApplication.guiFields.checkList!!.exists() &&
                              emailApplication.guiFields.realEmail.isNotEmpty() &&
                              emailApplication.guiFields.ehlo.isNotEmpty()
                           ) {
                              jobState.value = JobStates.IN_PROGRESS
                              GlobalScope.launch(Dispatchers.IO) {
                                 emailApplication.process()
                              }
                           } else {
                              snackBar.value = "Заполнены не все необходимые поля"
                           }
                        }) {
                           Text(text = "Начать")
                        }

                     }
                     if (jobState.value == JobStates.IN_PROGRESS) {
                        LinearProgressIndicator(
                           progress = totalProgress.value,
                           modifier = Modifier.fillMaxWidth()
                        )

                     }

                     if (jobState.value == JobStates.FINISHED) {
                        Row {
                           Button(
                              onClick = {
                                 FileDialog(currentWindow.window, "Файл с хорошими адресами", SAVE).apply {
                                    this.file = "Filtered_${Date().time}.txt"
                                    this.isVisible = true
                                    val file: String? = this.file
                                    file?.let {
                                       GlobalScope.launch(Dispatchers.IO) {
                                          File(
                                             this@apply.directory,
                                             it
                                          ).saveContent(emailApplication.guiFields.goodEmails)
                                       }
                                    }
                                 }
                              },
                              modifier = Modifier.width(110.dp)
                           ) {
                              Text(
                                 text = "Отфильтрованные",
                                 textAlign = TextAlign.Center
                              )
                           }
                           Spacer(Modifier.width(5.dp).height(5.dp))
                           Button(
                              onClick = {
                                 FileDialog(currentWindow.window, "Файл с плохими адресами", SAVE).apply {
                                    this.file = "Blocked_${Date().time}.csv"
                                    this.isVisible = true
                                    val file: String? = this.file
                                    file?.let {
                                       GlobalScope.launch(Dispatchers.IO) {
                                          File(
                                             this@apply.directory,
                                             it
                                          ).saveContent(emailApplication.guiFields.goodEmails)
                                       }

                                    }
                                 }
                              },
                              modifier = Modifier.width(110.dp)
                           )
                           {
                              Text(
                                 text = "С ошибкой",
                                 textAlign = TextAlign.Center
                              )
                           }
                           Spacer(Modifier.width(5.dp).height(5.dp))
                           Button(
                              onClick = {
                                 FileDialog(currentWindow.window, "Не обработанные", SAVE).apply {
                                    this.file = "NotProcessed${Date().time}.txt"
                                    this.isVisible = true
                                    val file: String? = this.file
                                    file?.let {
                                       GlobalScope.launch(Dispatchers.IO) {
                                          File(
                                             this@apply.directory,
                                             it
                                          ).saveContent(emailApplication.guiFields.noProcessedEmails)
                                       }
                                    }
                                 }
                              },
                              modifier = Modifier.width(110.dp)
                           )
                           {
                              Text(
                                 text = "Не обработанные",
                                 textAlign = TextAlign.Center
                              )
                           }
                        }
                     }
                  }
               }
               Column(
                  modifier = Modifier
                     .width(250.dp)
                     .height(400.dp),
               ) {

                  val domainsMap = remember { mutableMapOf<String, DomainProgress>() }
                  val progressBars = remember { mutableStateListOf<DomainProgress>() }

                  emailApplication.guiFields.onAddDomainProgress = { domainName ->
                     DomainProgress(domainName, emailApplication.guiFields).also {
                        domainsMap[domainName] = it
                        progressBars.add(it)
                     }
                  }

                  emailApplication.guiFields.onRemoveDomainProgress = { domainName ->
                     domainsMap[domainName]?.also {
                        progressBars.remove(it)
                     }
                  }

                  LazyColumn {
                     items(items = progressBars) { domainProgress ->
                        Column(
                           modifier = Modifier
                              .padding(4.dp)
                              .fillMaxWidth()
                        ) {
                           Text(
                              text = domainProgress.name,
                              modifier = Modifier
                           )
                           LinearProgressIndicator(
                              progress = domainProgress.progressValue,
                           )
                        }
                     }
                  }
               }
            }
            Row(
               modifier = Modifier.fillMaxSize(),
               verticalAlignment = Alignment.Bottom
            ) {
               if (snackBar.value.isNotEmpty()) {
                  Snackbar {
                     Text(text = snackBar.value)
                  }
               }
            }
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
            onClick = { Utils.openBrowser(PROJECT_WEBSITE) }
         )
      )
   )
   return MenuBar(fileMenu, helpMenu)
}
