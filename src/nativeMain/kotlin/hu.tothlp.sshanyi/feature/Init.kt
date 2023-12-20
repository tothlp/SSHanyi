package hu.tothlp.sshanyi.feature

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.rendering.TextStyles.dim
import com.github.ajalt.mordant.rendering.TextStyles.italic
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.YesNoPrompt
import hu.tothlp.sshanyi.getDefaultConfig
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import okio.use

/**
 * Subcommand to create empty config file.
 *
 * It checks if the SSH config file exists and creates it if needed.
 *
 */
class Init : CliktCommand(help = "Create empty configuration file") {

	override fun run() {
		val terminal = Terminal()
		terminal.println(
			italic("Checking if ${dim(getDefaultConfig())} exist..")
		)
		if (FileSystem.SYSTEM.exists(getDefaultConfig().toPath()) ) terminal.println("The config exists!")
		else if (YesNoPrompt("Creating the ${getDefaultConfig()} file. Continue?",terminal).ask() == true) {
			FileSystem.SYSTEM.appendingSink(getDefaultConfig().toPath()).buffer().use { it.writeUtf8("") }
			terminal.println("File successfully created.")
		}
	}
}