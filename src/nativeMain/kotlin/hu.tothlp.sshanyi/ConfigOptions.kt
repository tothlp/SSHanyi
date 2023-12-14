package hu.tothlp.sshanyi

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

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
}
