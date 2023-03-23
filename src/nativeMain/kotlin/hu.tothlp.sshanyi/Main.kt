package hu.tothlp.sshanyi

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.options.versionOption

class SSHanyi : CliktCommand(name = "SSHanyi") {

	init {
		versionOption("1.0", names = setOf("-v", "--version"))
		context {
			helpFormatter = CliktHelpFormatter(showDefaultValues = true, width = 120)
		}
	}

	override fun run() {}
}

fun main(args: Array<String>) = SSHanyi().subcommands(
	Add(),
	List(),
	Useless()
).main(args)