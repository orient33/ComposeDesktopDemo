package adbtool

import cn.netdiscovery.adbd.device.AdbDevice
import logg


fun <T> AdbDevice.wrapLet(block: (AdbDevice) -> T) {

    this.let {
        block.invoke(it)
    } ?: run {
        logg("the device is not connected，please connect first")
    }
}