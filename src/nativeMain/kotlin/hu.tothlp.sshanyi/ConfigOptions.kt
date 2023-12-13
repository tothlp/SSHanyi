package hu.tothlp.sshanyi

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.posix.getenv
import kotlin.experimental.ExperimentalNativeApi

/**
 * Common configuration options for subcommands.
 *
 * @property config Path for the config file. *(e.g.: /home/user/.ssh/config)*. If not given, then the User's default config will be used.
 */
class ConfigOptions() : OptionGroup("Config file options") {
	val config: Path by option(help = "Path for the configuration file.")
		// metavar: FILE sets the parameter type to print in help.
		// Check if the given file exists. If none given, then use default.
		.convert("FILE") {
			it.toPath().takeIf { FileSystem.SYSTEM.exists(it) } ?: fail("An existing file is required.")
		}
		// Get the default config file, and check if it exists.
		.default(getDefaultConfig().toPath()).validate {
			if (!FileSystem.SYSTEM.exists(it)) fail("The default config file ($it) does not exist. Create it, or enter a different file. For more info, see --help")
		}

	/**
	 * Gets the path for the default SSH config. It reads the current user folder from the proper environment variable, based on the current [Platform.osFamily]
	 */
	@OptIn(ExperimentalNativeApi::class, ExperimentalForeignApi::class)
	private fun getDefaultConfig(): String = when (Platform.osFamily) {
		OsFamily.WINDOWS -> getenv("USERPROFILE")?.toKString()?.plus("\\.ssh\\config")
		else -> getenv("HOME")?.toKString()?.plus("/.ssh/config")
	}.orEmpty()
}
