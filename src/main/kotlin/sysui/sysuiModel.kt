package sysui

import isAllNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import mem.RunShell

class SysModel {
    var logLine = 1
    val sysApkPath =
        MutableStateFlow("/home/dun/code.mi/MiuiSystemUI/packages/SystemUI/build/outputs/apk/overlayMiuiCnTablet/debug/MiuiSystemUI.apk")
    val logText = MutableStateFlow("")
    fun updateApkPath(path: String) {
        sysApkPath.tryEmit(path)
    }

    fun pushSystemUiPad() {
        GlobalScope.launch(Dispatchers.IO) {
            //从桌面启动的path还不对 需要从终端/opt/composedeskdemo/bin/Com.. 启动 才可以正常运行 TODO
//            listOf("HOME","PATH").forEach {
//                val v = System.getenv(it)
//                appendLog("$it : $v\n")
//            }
            //1 adb root , remount
            val r1 = RunShell.runCmd("adb root && adb wait-for-device && adb remount && adb wait-for-device")
            appendLog(r1)
            //2 push apk file
            val r2 = RunShell.runCmd(
                "adb push ", arrayOf(
                    sysApkPath.value,
                    "/system_ext/priv-app/MiuiSystemUI/MiuiSystemUI.apk"
                )
            )
            appendLog(r2)
            //3. check apk time
            val r3 = RunShell.runCmd(
                "adb shell ", arrayOf(
                    " ls /system_ext/priv-app/MiuiSystemUI/MiuiSystemUI.apk -lsh"
                )
            )
            appendLog(r3)
        }

    }

    fun killSystemUI() {
        GlobalScope.launch(Dispatchers.IO) {
            //1 check pid
            val r1 = RunShell.runCmd("adb shell ps -A|grep systemui")
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
            }
            appendLog("check, pid is $pid")

            //2 push apk file
            val r2 = RunShell.runCmd(
                "adb shell ", arrayOf(
                    "kill", "-9", " $pid"
                )
            )
            if (pid == Int.MAX_VALUE) {
                appendLog("未找到进程!")
            } else if (r2.isEmpty()) {
                appendLog("kill $pid done")
            } else {
                appendLog(r2)
            }
        }
    }

    fun clearLog() {
        GlobalScope.launch(Dispatchers.IO) {
            logLine = 1
            logText.emit("")
        }
    }

    private fun appendLog(msg: String) {
        val old = logText.value
        logText.tryEmit("$old ${logLine++}:$msg")
    }
}