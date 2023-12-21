package adbtool

import cn.netdiscovery.adbd.device.AdbDevice
import cn.netdiscovery.adbd.device.DeviceListener
import cn.netdiscovery.adbd.device.SocketAdbDevice
import cn.netdiscovery.adbd.utils.AuthUtil
import isAllNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import logg
import java.io.File
import java.nio.charset.Charset

class ToolModel {
    private val urlA = MutableStateFlow("10.220.44.77:5555")
    val url: StateFlow<String> = urlA
    val state = MutableStateFlow("init")

    var mDevice: AdbDevice? = null

    fun updateUrl(u: String) {
        MyCoroutineScope.launch(Dispatchers.IO) {
//            val r = urlA.tryEmit(u)
            urlA.emit(u)
        }
    }

    fun connect() {
        val ipPort = url.value.split(":")
        if (ipPort.size != 2) {
            logg("error ip port, $ipPort")
            return
        }
        val pri = try {
            AuthUtil.loadPrivateKey()
        } catch (e: Exception) {
            logg("load private key fail. $e")
//            null
            return
        }
        val device = SocketAdbDevice(
            ipPort[0], ipPort[1].toInt(), pri,
            AuthUtil.generatePublicKey(pri).toByteArray(Charset.defaultCharset())
        )
        val listener = object : DeviceListener {
            override fun onConnected(device: AdbDevice) {
                state.tryEmit("connected ${device.device()},${device.model()}")
                mDevice = device
            }

            override fun onDisconnected(device: AdbDevice) {
                state.tryEmit("disconnected! ${device.device()}")
                mDevice = null
            }
        }
        device.addListener(listener)
    }

    val pushTo = "/system_ext/priv-app/MiuiSystemUI/MiuiSystemUI.apk"
    val sysApkPath =
        MutableStateFlow("/home/dun/code.mi/MiuiSystemUI/packages/SystemUI/build/outputs/apk/overlayMiuiCnTablet/debug/MiuiSystemUI.apk")
    val logText = MutableStateFlow("")

    fun updateApkPath(path: String) {
        sysApkPath.tryEmit(path)
        logText.tryEmit("")
    }

    fun pushKillSystemUiPad() {
        MyCoroutineScope.launch(Dispatchers.IO) {
            val device = mDevice ?: return@launch
//            device.root().addListener {
//                if (it.cause() != null) {
//                    appendLog("root fail, ${it.cause()}")
//                } else {
//                    appendLog("root done")
//                }
//            }

            val file = File(sysApkPath.value)
            if (!file.exists()) {
                appendLog("not exist : $file")
                return@launch
            } else {
                appendLog("start push file.")
            }
            device.wrapLet {
                it.push(file, pushTo).addListener { f ->
                    if (f.cause() != null) {
                        appendLog("push fail. ${f.cause()}")
                    } else {
                        logg("push done")
                        appendLog("push done. then kill")
                        killSystemUI()
                    }
                }
            }
        }

    }

    fun killSystemUI() {
        MyCoroutineScope.launch(Dispatchers.IO) {
            val device = mDevice ?: return@launch
            val future = device.shell("ps -A|grep systemui").sync()
            val err = future.cause()
            if (err != null) {
                appendLog(err.toString())
            } else {
                val r1 = future.now.toString()
                appendLog(r1)
                var pid = Int.MAX_VALUE
                if (r1.length > 1) {
                    val arr = r1.split(" ").filter {
                        it.length > 1 && it.isAllNumber()
                    }
                    pid = try {
                        arr[0].toInt()
                    } catch (e: NumberFormatException) {
                        Int.MAX_VALUE
                    }
                    appendLog("check pid is $pid")

                    //2 kill pid
                    val f2 = device.shell("kill", "-9", " $pid").sync()
                    val r2 = f2.now
                    if (pid == Int.MAX_VALUE) {
                        appendLog("未找到进程!")
                    } else if (r2.isEmpty()) {
                        appendLog("kill $pid done")
                    } else {
                        appendLog(r2)
                    }
                }
            }
        }
    }

    private fun appendLog(msg: String) {
        val old = logText.value
        logText.tryEmit("$msg\n$old")
    }
}

