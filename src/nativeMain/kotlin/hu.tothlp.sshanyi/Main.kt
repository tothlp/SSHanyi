package hu.tothlp.sshanyi

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.versionOption

class SSHanyi : CliktCommand(name = "SSHanyi") {

    init {
        versionOption("1.0")
    }
    override fun run() {
        echo("""
 _____ _____ _   _                   _ 
/  ___/  ___| | | |                 (_)
\ `--.\ `--.| |_| | __ _ _ __  _   _ _ 
 `--. \`--. \  _  |/ _` | '_ \| | | | |
/\__/ /\__/ / | | | (_| | | | | |_| | |
\____/\____/\_| |_/\__,_|_| |_|\__, |_|
                                __/ |  
                               |___/   
        """.trimIndent())
        SSHanyi().commandHelp
        SSHanyi().getFormattedHelp()
    }
}

fun main(args: Array<String>) = SSHanyi().subcommands(
    Add(),
    List()
).main(args)