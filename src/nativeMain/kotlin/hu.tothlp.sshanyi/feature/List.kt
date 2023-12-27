package hu.tothlp.sshanyi.feature

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.table.table
import hu.tothlp.sshanyi.feature.dto.ConfigName
import hu.tothlp.sshanyi.config.ConfigOptions
import hu.tothlp.sshanyi.feature.dto.SSHConfig
import hu.tothlp.sshanyi.feature.dto.toStringOrEmpty
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
        val configEntries = readLines(configOptions.config)
        if(legacy) legacyPrintTable(configEntries) else printTable(configEntries)
    }

    fun readLines(path: Path): MutableList<SSHConfig> {
        val configEntries = mutableListOf<SSHConfig>()
        var currentConfig: SSHConfig? = null
        var commentedHost: Boolean = false

        val keyValueRegex = Regex("\\S*")
        val commentedHostRegex = Regex("#\\s*Host\\s*")
        val hostRegex = Regex("^\\s*Host\\s*")

        FileSystem.SYSTEM.source(path).use { fileSource ->
            fileSource.buffer().use { bufferedFileSource ->
                while (true) {
                    val line = bufferedFileSource.readUtf8Line()?.trim() ?: break

                    if(commentedHostRegex.containsMatchIn(line)) {
                        commentedHost = true
                        continue
                    }
                    if(hostRegex.containsMatchIn(line)) commentedHost = false
                    if(line.startsWith('#') || commentedHost) continue

                    val lineParts = keyValueRegex.findAll(line).map { it.value }.filter { it.isNotBlank() }.toList()
                    echo(lineParts)
                    if (lineParts.size < 2) continue
                    var configKey: String?
                    var configValue: String?
                    if (lineParts.size > 2 && lineParts[0].contains('#')) {
                         configKey = lineParts[1]
                         configValue = lineParts[2]
                    } else {
                         configKey = lineParts[0]
                         configValue = lineParts[1]
                    }
                    when (configKey) {
                        ConfigName.HOST.value -> {
                            currentConfig = SSHConfig(configValue)
                            configEntries.add(currentConfig!!)
                        }

                        ConfigName.HOSTNAME.value -> currentConfig?.hostName = configValue
                        ConfigName.USER.value -> currentConfig?.user = configValue
                        ConfigName.PORT.value -> currentConfig?.port = configValue.toIntOrNull()
                        else -> currentConfig?.misc?.put(configKey, configValue)
                    }

                }
            }
        }
        echo(configEntries)
        return configEntries
    }

    private fun printTable(entries: List<SSHConfig>) {
        val terminal = Terminal()
        val miscDataKeys = entries.flatMap { it.misc.keys }.distinct()
        terminal.println(table {
            header {  row(*ConfigName.entries.map { it.value }.toTypedArray(), *miscDataKeys.toTypedArray()) }
            body {
                column(3) {
                    cellBorders = Borders.ALL
                    borderType = 
                }
                entries.forEach {
                    val miscData = miscDataKeys.map { key -> it.misc[key] }
                    row(it.host, it.hostName, it.user, it.port, *miscData.toTypedArray())
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