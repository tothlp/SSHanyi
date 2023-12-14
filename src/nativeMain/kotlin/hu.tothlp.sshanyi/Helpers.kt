package hu.tothlp.sshanyi

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv
import kotlin.experimental.ExperimentalNativeApi

/**
 * Gets the path for the default SSH config. It reads the current user folder from the proper environment variable, based on the current [Platform.osFamily]
 */
@OptIn(ExperimentalNativeApi::class, ExperimentalForeignApi::class)
fun getDefaultConfig(): String = when (Platform.osFamily) {
    OsFamily.WINDOWS -> getenv("USERPROFILE")?.toKString()?.plus("\\.ssh\\config")
    else -> getenv("HOME")?.toKString()?.plus("/.ssh/config")
}.orEmpty()