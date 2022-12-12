package org.home

import java.io.File
import java.io.InputStream

fun readInput(file: String): List<String> {
    val inputStream: InputStream = File(file).inputStream()
   return inputStream.bufferedReader().readLines()
}