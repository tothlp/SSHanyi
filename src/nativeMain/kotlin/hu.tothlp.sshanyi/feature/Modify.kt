package hu.tothlp.sshanyi.feature

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import hu.tothlp.sshanyi.config.ConfigOptions
import hu.tothlp.sshanyi.feature.List
import okio.FileSystem

/**
 * Subcommand to create empty config file.
 *
 * It checks if the SSH config file exists and creates it if needed.
 *
 */
class Modify : CliktCommand(help = "Update configuration entry") {
	private val configOptions by ConfigOptions()
	val name by option().prompt()

	override fun run() {
		val path = configOptions.config
		yolo()
	}

	fun yolo(){
		val content = List().readLines(configOptions.config)
		echo(content.map { it.host }.joinToString())

//		val updatedContent = content.replace("red", "blue")
//
//		FileSystem.SYSTEM.write(path) {
//			writeUtf8(updatedContent)
//		}
	}
}