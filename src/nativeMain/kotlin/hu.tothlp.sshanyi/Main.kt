package hu.tothlp.sshanyi

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class SSHanyi : CliktCommand() {

    override fun run() {
        SSHanyi().commandHelp
    }
}

fun main(args: Array<String>) = SSHanyi().subcommands(
    List()
).main(args)