package hu.tothlp.sshanyi

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.options.versionOption
import hu.tothlp.sshanyi.feature.Add
import hu.tothlp.sshanyi.feature.Init
import hu.tothlp.sshanyi.feature.Useless
import hu.tothlp.sshanyi.feature.List
import hu.tothlp.sshanyi.feature.Modify

/** Main application class.
 *
 * It is responsible for setting the `--version` information, and running the application.
 *
 * @since v0.1.0
 */
class SSHanyi : CliktCommand(name = "SSHanyi") {

	init {
		versionOption("1.0", names = setOf("-v", "--version"))
		context {
			helpFormatter = { MordantHelpFormatter(it) }
		}
	}

	override fun run() {}
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
	Modify()
).main(args)