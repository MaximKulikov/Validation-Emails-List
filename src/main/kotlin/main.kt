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
import androidx.compose.ui.input.pointer.pointerMoveFilter
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
import ru.gavarent.StringResources.*
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
   val appRes = emailApplication.appRes
   Window(
      title = appRes.string(APP_TITLE),
      size = IntSize(700, 500),
      /*icon = ,*/
      menuBar = getMenuBar(appRes),
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
                        text = appRes.string(USER_EMAIL),
                        modifier = Modifier
                           .width(100.dp)
                           .pointerMoveFilter(onEnter = {
                              snackBar.value = appRes.string(USER_EMAIL_DESCRIPTION)
                              false
                           },
                              onExit = {
                                 snackBar.value = ""
                                 false
                              }
                           )
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
                        appRes.string(EHLO),
                        modifier = Modifier
                           .width(100.dp)
                           .pointerMoveFilter(onEnter = {
                              snackBar.value = appRes.string(EHLO_DESCRIPTION)
                              false
                           },
                              onExit = {
                                 snackBar.value = ""
                                 false
                              }
                           )
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
                        appRes.string(EMAILS_LIST_INCOMING),
                        modifier = Modifier
                           .width(100.dp)
                           .pointerMoveFilter(onEnter = {
                              snackBar.value = appRes.string(EMAILS_LIST_INCOMING_DESCRIPTION)
                              false
                           },
                              onExit = {
                                 snackBar.value = ""
                                 false
                              }
                           )
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
                           FileDialog(currentWindow.window, appRes.string(EMAILS_DIALOG_INCOMING)).apply {
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
                        appRes.string(EMAILS_LIST_UNSUB),
                        modifier = Modifier
                           .width(100.dp)
                           .pointerMoveFilter(onEnter = {
                              snackBar.value = appRes.string(EMAILS_LIST_UNSUB_DESCRIPTION)
                              false
                           },
                              onExit = {
                                 snackBar.value = ""
                                 false
                              }
                           )
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
                           FileDialog(currentWindow.window, appRes.string(EMAILS_DIALOG_UNSUB)).apply {
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
                        appRes.string(EMAILS_LIST_IGNORE),
                        modifier = Modifier
                           .width(100.dp)
                           .pointerMoveFilter(onEnter = {
                              snackBar.value = appRes.string(EMAILS_LIST_IGNORE_DESCRIPTION)
                              false
                           },
                              onExit = {
                                 snackBar.value = ""
                                 false
                              }
                           )
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
                           FileDialog(currentWindow.window, appRes.string(EMAILS_DIALOG_IGNORE), LOAD).apply {
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
                              snackBar.value = appRes.string(WARN_REQUIRE_FIELDS)
                           }
                        }) {
                           Text(text = appRes.string(BUTTON_BEGIN))
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
                                 FileDialog(currentWindow.window, appRes.string(FINISH_DIALOG_FILTERED), SAVE).apply {
                                    this.file = "${appRes.string(FINISH_FILE_PREFIX_FILTERED)}_${Date().time}.txt"
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
                                 text = appRes.string(FINISH_BUTTON_FILTERED),
                                 textAlign = TextAlign.Center
                              )
                           }
                           Spacer(Modifier.width(5.dp).height(5.dp))
                           Button(
                              onClick = {
                                 FileDialog(currentWindow.window, appRes.string(FINISH_DIALOG_ERROR), SAVE).apply {
                                    this.file = "${appRes.string(FINISH_FILE_PREFIX_FAILED)}_${Date().time}.csv"
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
                                 text = appRes.string(FINISH_BUTTON_FAILED),
                                 textAlign = TextAlign.Center
                              )
                           }
                           Spacer(Modifier.width(5.dp).height(5.dp))
                           Button(
                              onClick = {
                                 FileDialog(currentWindow.window, appRes.string(FINISH_DIALOG_FAILED), SAVE).apply {
                                    this.file = "${appRes.string(FINISH_FILE_PREFIX_ERROR)}_${Date().time}.txt"
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
                                 text = appRes.string(FINISH_BUTTON_ERROR),
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

@OptIn(ExperimentalStdlibApi::class)
fun getMenuBar(appRes: ResourceMapFactory): MenuBar {
   val fileMenu = Menu(
      name = appRes.string(MENU_FILE),
      item = arrayOf(
         MenuItem(
            name = appRes.string(MENU_FILE_EXIT),
            onClick = { exitProcess(0) },
            shortcut = KeyStroke(Key.X)
         )
      )
   )
   val helpMenu = Menu(
      name = appRes.string(MENU_ABOUT),
      item = arrayOf(
         MenuItem(
            name = appRes.string(MENU_ABOUT_WEBSITE),
            onClick = { Utils.openBrowser(PROJECT_WEBSITE) }
         )
      )
   )
   return MenuBar(fileMenu, helpMenu)
}
