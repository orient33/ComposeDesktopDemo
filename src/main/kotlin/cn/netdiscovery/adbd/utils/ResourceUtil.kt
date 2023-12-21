package cn.netdiscovery.adbd.utils

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

@Throws(IOException::class)
fun readAdbKey(): ByteArray {
//    val classLoader = Thread.currentThread().contextClassLoader
     //classLoader.getResourceAsStream(name)?:
    val ins = (File("/home/dun/.android/adbkey").inputStream())//TODO df .read adbkey path
    val os = ByteArrayOutputStream()
    val bytes = ByteArray(8192)
    var size: Int
    while (ins.read(bytes).also { size = it } != -1) {
        os.write(bytes, 0, size)
    }
    os.close()
    return os.toByteArray()
}