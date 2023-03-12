package hu.tothlp.sshanyi

import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import kotlinx.cinterop.toKString
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.posix.getenv

class ConfigOptions : OptionGroup("Config file options") {
	val config: Path by option(help = "Path for the configuration file.")
		.convert("FILE") {
			it.toPath().takeIf { FileSystem.SYSTEM.exists(it) } ?: fail("An existing file is required.")
		}
		.default(getDefaultConfig().toPath()).validate {
			if (!FileSystem.SYSTEM.exists(it)) fail("The default config file ($it) does not exist. Create it, or enter a different file. For more info, see --help")
		}

	private fun getDefaultConfig(): String = when (Platform.osFamily) {
		OsFamily.WINDOWS -> getenv("USERPROFILE")?.toKString()?.plus("\\.ssh\\config")
		else -> getenv("HOME")?.toKString()?.plus("/.ssh/config")
	}.orEmpty()
}
