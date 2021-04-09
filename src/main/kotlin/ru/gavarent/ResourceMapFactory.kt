package ru.gavarent

import androidx.compose.ui.text.intl.Locale
import ru.gavarent.StringResources.*

@ExperimentalStdlibApi
class ResourceMapFactory {

   private val stringResource: Map<StringResources, String> = when (Locale.current.language) {
      "ru" -> ruMap()
      else -> mapOf()
   }

   fun string(res: StringResources): String = stringResource.getOrDefault(res, res.name)

   private fun ruMap(): Map<StringResources, String> {
      return buildMap {
         put(APP_TITLE, "Инструмент проверки email")
         put(USER_EMAIL, "*Email пользователя")
         put(
            USER_EMAIL_DESCRIPTION,
            "Email адрес с которого предполагается делать расслку или любой действительный адрес из вашего домена"
         )
         put(EHLO, "*ehlo приветствие")
         put(
            EHLO_DESCRIPTION,
            "Приветственное сообщение почтового сервера. Обычно состоит из двух частей. Первая сама команда EHLO, вторая часть - это ваш почтовый домен. Например 'EHLO mail.com'"
         )
         put(EMAILS_LIST_INCOMING, "*Проверяемые адреса")
         put(EMAILS_LIST_INCOMING_DESCRIPTION, "Текстовый файл со списком почтовых адресов для проверки. Каждый email начинается на новой строчке")
         put(EMAILS_DIALOG_INCOMING, "Файл с проверяемыми адресами")
         put(EMAILS_LIST_UNSUB, "Отписавшиеся адреса")
         put(EMAILS_LIST_UNSUB_DESCRIPTION, "Текстовый файл с адресами, которые требуется удалить из проверяемого списка без проверки")
         put(EMAILS_DIALOG_UNSUB, "Файл с списком отписавшихся")
         put(EMAILS_LIST_IGNORE, "Игнорировать проверку")
         put(EMAILS_LIST_IGNORE_DESCRIPTION, "Текстовый файл с адресами, которые, будут добавлены в результат без фактической проверки")
         put(EMAILS_DIALOG_IGNORE, "Файл со списками для игнорирования проверки")

         put(BUTTON_BEGIN, "Начать")

         put(FINISH_DIALOG_FILTERED, "Файл с отфильтрованными адресами")
         put(FINISH_DIALOG_FAILED, "Файл с неудачными адресами")
         put(FINISH_DIALOG_ERROR, "Файл с адресами, которые не удалось проверить")

         put(FINISH_BUTTON_FILTERED, "Отфильтрованные")
         put(FINISH_BUTTON_FAILED, "С ошибками")
         put(FINISH_BUTTON_ERROR, "Не проверенные")
         put(FINISH_FILE_PREFIX_FILTERED, "Отфильтрованные")
         put(FINISH_FILE_PREFIX_FAILED, "Ошибочные")
         put(FINISH_FILE_PREFIX_ERROR, "Игнорированные")

         put(WARN_REQUIRE_FIELDS, "Заполнены не все необходимые поля")

         put(MENU_FILE, "Файл")
         put(MENU_FILE_EXIT, "Выход")
         put(MENU_ABOUT, "О программе")
         put(MENU_ABOUT_WEBSITE, "Веб сайт")
      }
   }

}

enum class StringResources {
   APP_TITLE,
   USER_EMAIL,
   USER_EMAIL_DESCRIPTION,
   EHLO,
   EHLO_DESCRIPTION,
   EMAILS_LIST_INCOMING,
   EMAILS_LIST_INCOMING_DESCRIPTION,
   EMAILS_DIALOG_INCOMING,
   EMAILS_LIST_UNSUB,
   EMAILS_LIST_UNSUB_DESCRIPTION,
   EMAILS_DIALOG_UNSUB,
   EMAILS_LIST_IGNORE,
   EMAILS_LIST_IGNORE_DESCRIPTION,
   EMAILS_DIALOG_IGNORE,

   FINISH_DIALOG_FILTERED,
   FINISH_DIALOG_FAILED,
   FINISH_DIALOG_ERROR,
   FINISH_BUTTON_FILTERED,
   FINISH_BUTTON_FAILED,
   FINISH_BUTTON_ERROR,
   FINISH_FILE_PREFIX_FILTERED,
   FINISH_FILE_PREFIX_FAILED,
   FINISH_FILE_PREFIX_ERROR,

   BUTTON_BEGIN,

   WARN_REQUIRE_FIELDS,

   MENU_FILE,
   MENU_FILE_EXIT,
   MENU_ABOUT,
   MENU_ABOUT_WEBSITE

}
