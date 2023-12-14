package hu.tothlp.sshanyi

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.options.versionOption
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

/** Main application class.
 *
 * It is responsible for setting the `--version` information, and running the application.
 *
 * @since v0.1.0
 */
class SSHanyi : CliktCommand(name = "SSHanyi") {

	init {
		val properties = readLines("version.properties".toPath())
		val version = properties["version"] ?: "n/a"
		versionOption(version, names = setOf("-v", "--version"))
		context {
			helpFormatter = { MordantHelpFormatter(it) }
		}
	}

	override fun run() {}

	private fun readLines(path: Path): MutableMap<String, String> {
		val properties = mutableMapOf<String, String>()
		FileSystem.SYSTEM.read(path) {
			while (true) {
				val line = readUtf8Line()?.trim() ?: break
				val data = line.split("=").map { it.trim() }
				data.takeIf { it.isNotEmpty() }?.let {
					properties.put(it[0], data.getOrNull(1).toString())
				}
			}
		}
		println(properties)
		return properties
	}
}

/**
 *
 * The entry point of the application.
 * It in instantiates the [SSHanyi] main class, sets up the subcommands and runs the application.
 *
 * @since 0.1.0
 */
fun main(args: Array<String>) = SSHanyi().subcommands(
	Init(),
	Add(),
	List(),
	Useless(),
).main(args)