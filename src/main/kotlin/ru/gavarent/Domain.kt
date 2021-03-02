package ru.gavarent

import javax.naming.directory.Attribute

data class Domain(
   val name: String,
   val attribute: Attribute
)
