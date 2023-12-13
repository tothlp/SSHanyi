package hu.tothlp.sshanyi

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.YesNoPrompt
import okio.FileSystem
import okio.buffer
import okio.use

/**
 * Subcommand to Add entries to config.
 *
 * It opens the SSH config file provided by [config] (or default) then asks for [entryOptions] parameters.
 * After confirmation, it adds a new entry to config, as set entered in parameters.
 *
 * @property config Config file settings
 * @property entryOptions Setting values for the new entry
 */
class Add : CliktCommand(help = "Add entries to configuration") {

	private val config by ConfigOptions()
	private val entryOptions by EntryOptions()


	override fun run() {
		echo("New config entry:")
		echo("\tHost: ${entryOptions.host}")
		echo("\tHostName: ${entryOptions.hostName}")
		entryOptions.user?.let { echo("\tUser: $it") }
		entryOptions.port?.let { echo("\tPort: $it") }
		val terminal = Terminal()
		if (YesNoPrompt("A new entry will be added to your ${config.config} file. Continue?",terminal).ask() == true) {
			appendConfig()
			echo("Entry added. Now you can access your server with: ssh ${entryOptions.host}")
		}
	}

	/**
	 * Opens the config file for writing, then adds a new entry with the data provided in [entryOptions].
	 */
	private fun appendConfig() {
		FileSystem.SYSTEM.appendingSink(config.config).buffer().use {
			it.writeUtf8("\n")
			it.writeUtf8("Host ${entryOptions.host}\n")
			it.writeUtf8("HostName ${entryOptions.hostName}\n")
			if(entryOptions.user != null) it.writeUtf8("User ${entryOptions.user}\n")
			if(entryOptions.port != null) it.writeUtf8("Port ${entryOptions.port}\n")
		}
	}
}