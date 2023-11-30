package mem

import isAllNumber
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

val flag = AtomicBoolean(false)

// 通过adb检测android设备上某进程的内存
suspend fun readMemory(packageName: String, flow: MutableStateFlow<MutableList<MemInfo>>): Boolean {
    mylog("env path is " + System.getenv("PATH"))
    //0. find adb
    var adbPath = RunShell.runCmd("which adb")
    if (adbPath.isEmpty()) {
        mylog("adb is not in PATH")
        adbPath = "/home/dun/pro/sdk/platform-tools/adb"
//        return
    }
    mylog("adb : $adbPath")
    //1. 查找进程pid
    val pidResult = RunShell.runCmd("$adbPath shell ps -A|grep $packageName")
    val lines = pidResult.split("\n").filter { it.contains(packageName) }
    if (lines.isEmpty()) {
        mylog("not find process for $packageName")
        return false
    }
    val words = lines[0].split(' ').filter { it.isNotEmpty() }
    //USER           PID  PPID     VSZ    RSS WCHAN            ADDR S NAME
    //theme        31253  1160 36236284 362136 do_epoll_wait      0 S com.android.thememanager
//  words[0/1/2]  uid , pid, ppid
    if (words.size < 4) return false
    mylog("get .$words")
    val pid = words[1].toInt()
    // 2. dump pid 内存
    var count = 100 //Short.MAX_VALUE
    var memInfo: MemInfo?
    val list = mutableListOf<MemInfo>()
    flag.set(true)
    val file = File("~/temp/$packageName-meminfo.log")
    if (file.exists()) {
        file.delete()
    }
    val r = try {
        file.createNewFile()
    } catch (e: Exception) {
        mylog("create file fail. $e")
        false
    }
    do {
        memInfo = readMemForPid(adbPath, pid, if (r) file else null)
        if (memInfo != null) {
            val list2 = mutableListOf<MemInfo>()
            list2.add(memInfo)
            list2.addAll(list)
            flow.tryEmit(list2)
            list.add(0, memInfo)
            mylog("${memInfo.totalPss}")
        } else {
            mylog("meminfo null.. break")
            break
        }
        delay(500)
    } while (count-- > 0 && flag.get())
    return flag.get()
}

fun stopRead() {
    flag.set(false)
}

fun mylog(msg: String) {
    println("pid= ${ProcessHandle.current().pid()}, tid=${Thread.currentThread()} $msg")
}

fun readMemForPid(adbPath: String, pid: Int, file: File?): MemInfo? {
    val memResult = RunShell.runCmd("$adbPath shell dumpsys meminfo $pid")
    val timeline = System.currentTimeMillis() // 使用android设备的时间 ?
    if (memResult.length < 10) return null
    var nativeHeap = 0
    var javaHeap = 0
    var code = 0
    var stack = 0
    var graphics = 0
    var privateOther = 0
    var system = 0
    var totalPss = 0
//    val objects = mutableMapOf<String, Int>()
    if (file != null)
        file.appendText(memResult + "\n\n")
    else
        mylog(memResult)
    memResult.split('\n').forEach {
        val line = it.trim()
        if (line.length > 1) {
            if (line.startsWith("Java Heap:")) {
                javaHeap = firstNumber(line)
            } else if (line.startsWith("Native Heap:")) {
                nativeHeap = firstNumber(line)
            } else if (line.startsWith("Code:")) {
                code = firstNumber(line)
            } else if (line.startsWith("Stack:")) {
                stack = firstNumber(line)
            } else if (line.startsWith("Graphics:")) {
                graphics = firstNumber(line)
            } else if (line.startsWith("Private Other:")) {
                privateOther = firstNumber(line)
            } else if (line.startsWith("System:")) {
                system = firstNumber(line)
            } else if (line.startsWith("TOTAL PSS:")) {
                totalPss = firstNumber(line)
            }
        }
    }
    return MemInfo(
        timeline,
        nativeHeap / 1024,
        javaHeap / 1024,
        code / 1024,
        stack / 1024,
        graphics / 1024,
        privateOther / 1024,
        system / 1024,
        totalPss / 1024,
    )
}

private fun firstNumber(line: String): Int {
    val words = line.split(' ').filter { it.isNotEmpty() && it.isNotBlank() && it.isAllNumber() }
    return if (words.isNotEmpty()) {
        words[0].toInt()
    } else {
        -1
    }
}

data class MemInfo(
    val timeline: Long,
    val nativeHeap: Int = 0,
    val javaHeap: Int = 0,
    val code: Int = 0,
    val stack: Int = 0,
    val graphics: Int = 0,
    val privateOther: Int = 0,
    val system: Int = 0,
    val totalPss: Int = 0,
//    val objects: Map<String, Int>,
)
/* ---内存的dumpsys 信息如下
dun@dun-OptiPlex-7050:~/下载$ adb shell dumpsys meminfo com.android.systemui
Applications Memory Usage (in Kilobytes):
Uptime: 128920828 Realtime: 756579279

** MEMINFO in pid 4485 [com.android.systemui] **
                   Pss  Private  Private  SwapPss      Rss     Heap     Heap     Heap
                 Total    Dirty    Clean    Dirty    Total     Size    Alloc     Free
                ------   ------   ------   ------   ------   ------   ------   ------
  Native Heap   110425   110412        0    28128   112764   262588   109367   146924
  Dalvik Heap    37967    37928        0     1169    41248    62934    38358    24576
 Dalvik Other    10438     8884        4      244    13616
        Stack     2412     2412        0      536     2420
       Ashmem      174      112        0        0     1752
      Gfx dev     7732     7464      268        0     7736
    Other dev       46        0       40        0      548
     .so mmap     4218      468       16      420    55420
    .jar mmap     2131        0       72        0    41780
    .apk mmap    25183        0    18020        0    41992
    .ttf mmap     3157        0      200        0    16432
    .dex mmap     5884     3692     2076     3768     6852
    .oat mmap      296        0        0        0    13572
    .art mmap     3241     3048        8       62    18320
   Other mmap      427        8      292        0     1584
   EGL mtrack     6592     6592        0        0     6592
    GL mtrack     1452     1452        0        0     1452
      Unknown      678      676        0      505     1240
        TOTAL   257285   183148    20996    34832   385320   325522   147725   171500

 App Summary
                       Pss(KB)                        Rss(KB)
                        ------                         ------
           Java Heap:    40984                          59568
         Native Heap:   110412                         112764
                Code:    24544                         179196
               Stack:     2412                           2420
            Graphics:    15776                          15780
       Private Other:    10016
              System:    53141
             Unknown:                                   15592

           TOTAL PSS:   257285            TOTAL RSS:   385320       TOTAL SWAP PSS:    34832

 Objects
               Views:     7857         ViewRootImpl:       26
         AppContexts:       94           Activities:        0
              Assets:       50        AssetManagers:        0
       Local Binders:      541        Proxy Binders:      155
       Parcel memory:      122         Parcel count:      198
    Death Recipients:       16      OpenSSL Sockets:        0
            WebViews:        0

 SQL
         MEMORY_USED:       89
  PAGECACHE_OVERFLOW:       29          MALLOC_SIZE:       46

 DATABASES
      pgsz     dbsz   Lookaside(b)          cache  Dbname
         4       20             27         1/22/4  :memory:
**/