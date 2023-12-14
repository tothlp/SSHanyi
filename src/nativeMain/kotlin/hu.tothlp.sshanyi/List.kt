package hu.tothlp.sshanyi

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.table.table
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.use
import kotlin.collections.List
import kotlin.math.ceil

class List : CliktCommand(help = "List configuration entries") {
    private val defaultPadding = 10
    private val configOptions by ConfigOptions()
    private val legacy by option("--legacy", "-l", help = "Use legacy printing").flag( default = false)

    init {
        context { helpFormatter = {MordantHelpFormatter(it) }
        }
    }

    override fun run() {
        readLines(configOptions.config, legacy)
    }

    private fun readLines(path: Path, legacy: Boolean) {
        var configEntries = mutableListOf<SSHConfig>()
        var currentConfig: SSHConfig? = null

        FileSystem.SYSTEM.source(path).use { fileSource ->
            fileSource.buffer().use { bufferedFileSource ->
                while (true) {
                    val line = bufferedFileSource.readUtf8Line() ?: break
                    val lineParts = line.trim().replace(Regex("\\s{2,}"), "").split(Regex("\\s"))
                    if (lineParts.size < 2) continue
                    val configKey = lineParts[0]
                    val configValue = lineParts[1]

                    when (configKey) {
                        ConfigName.HOST.value -> {
                            currentConfig = SSHConfig(configValue)
                            configEntries.add(currentConfig!!)
                        }

                        ConfigName.HOSTNAME.value -> currentConfig?.hostName = configValue
                        ConfigName.USER.value -> currentConfig?.user = configValue
                        ConfigName.PORT.value -> currentConfig?.port = configValue.toIntOrNull()
                    }

                }
            }
        }
        if(legacy) legacyPrintTable(configEntries) else printTable(configEntries)
    }

    private fun printTable(entries: List<SSHConfig>) {
        val terminal = Terminal()
        terminal.println(table {
            header {  row(*ConfigName.entries.map { it.value }.toTypedArray()) }
            body {
                entries.forEach {
                    row(it.host, it.hostName, it.user, it.port)
                }
            }
        })
    }
    private fun legacyPrintTable(entries: List<SSHConfig>) {
        val cellWidthData = calculateCellWidthData(entries)
        printHeaders(cellWidthData)
        printEntries(entries, cellWidthData)
    }

    private fun calculateCellWidthData(entries: List<SSHConfig>): Map<ConfigName, Int> {
        return mapOf(
            ConfigName.HOST to entries.mapNotNull { it.host }.plus(ConfigName.HOST.value).map { it.length }
                .maxBy { it },
            ConfigName.HOSTNAME to entries.mapNotNull { it.hostName }.plus(ConfigName.HOSTNAME.value).map { it.length }
                .maxBy { it },
            ConfigName.USER to entries.mapNotNull { it.user }.plus(ConfigName.USER.value).map { it.length }
                .maxBy { it },
            ConfigName.PORT to entries.mapNotNull { it.port.toStringOrEmpty() }.plus(ConfigName.PORT.value)
                .map { it.length }.maxBy { it },
        )
    }

    private fun printHeaders(cellWidthData: Map<ConfigName, Int>) {
        val headers = ConfigName.values().map { it.value.leftText(cellWidthData[it]) }
        val preHeader = ConfigName.values().map { "".leftText(cellWidthData[it], '-') }

        echo(preHeader.joinToString("+", "+", postfix = "+"))
        echo(headers.joinToString("|", "|", postfix = "|"))
        echo(preHeader.joinToString("+", "+", postfix = "+"))
    }

    private fun printEntries(entries: List<SSHConfig>, cellWidthData: Map<ConfigName, Int>) {
        val hostPadSize = cellWidthData[ConfigName.HOST]
        val hostNamePadSize = cellWidthData[ConfigName.HOSTNAME]
        val userPadSize = cellWidthData[ConfigName.USER]
        val portPadSize = cellWidthData[ConfigName.PORT]
        val formattedEntries = entries.map {
            "|${it.host.leftText(hostPadSize)}|${it.hostName.leftText(hostNamePadSize)}|${it.user.leftText(userPadSize)}|${
                it.port.toStringOrEmpty().rightText(portPadSize)
            }|"
        }
        formattedEntries.forEach { echo(it) }

        val footer = ConfigName.values().map { "".leftText(cellWidthData[it], '-') }
        echo(footer.joinToString("+", "+", postfix = "+"))
    }

    private fun String?.leftText(padSize: Int?, padChar: Char? = ' '): String = this.orEmpty().let {
        "$padChar$it$padChar".padEnd(ceil((padSize?.takeIf { it > defaultPadding } ?: defaultPadding) * 1.3).toInt(),
            padChar ?: ' ')
    }

    private fun String?.rightText(padSize: Int?, padChar: Char? = ' '): String = this.orEmpty().let {
        "$padChar$it$padChar".padStart(ceil((padSize?.takeIf { it > defaultPadding } ?: defaultPadding) * 1.3).toInt(),
            padChar ?: ' ')
    }

}