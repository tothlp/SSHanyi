package hu.tothlp.sshanyi

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import platform.posix.*
import kotlin.collections.List
import kotlin.math.ceil
import kotlinx.cinterop.*

class List: CliktCommand(help="List configuration entries") {
    private val defaultPadding = 10
    private val config: String by option(help = "Path for the configuration file.").default(getDefaultConfig())

    init {
        context { helpFormatter = CliktHelpFormatter(showDefaultValues = true) }
    }

    override fun run() {
        val path = config.toPath()
        if(FileSystem.SYSTEM.exists(path)) readLines(path)
        else echo("The given file does not exist.", err = true)
    }

    private fun getDefaultConfig(): String = when(Platform.osFamily) {
            OsFamily.WINDOWS -> getenv("USERPROFILE")?.toKString()?.plus("\\.ssh\\config")
            else -> getenv("HOME")?.toKString()?.plus("/.ssh/config")
        } ?: ""

    private fun readLines(path: Path) {
        var configEntries = mutableListOf<SSHConfig>()
        var currentConfig: SSHConfig? = null

        FileSystem.SYSTEM.source(path).use { fileSource ->
            fileSource.buffer().use { bufferedFileSource ->
                while (true) {
                    val line = bufferedFileSource.readUtf8Line() ?: break
                    val lineParts = line.trim().replace(Regex("\\s{2,}"), "").split(Regex("\\s"))
                    if(lineParts.size < 2) continue
                    val configKey = lineParts[0]
                    val configValue = lineParts[1]

                    when(configKey) {
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
        printTable(configEntries)
    }

    private fun printTable(entries: List<SSHConfig>) {
        val cellWidthData = calculateCellWidthData(entries)
        printHeaders(cellWidthData)
        printEntries(entries, cellWidthData)
    }

    private fun calculateCellWidthData(entries: List<SSHConfig>): Map<ConfigName, Int> {
        val data = mapOf(
            ConfigName.HOST to entries.mapNotNull { it.host }.plus(ConfigName.HOST.value).map{ it.length }.maxBy { it },
            ConfigName.HOSTNAME to entries.mapNotNull { it.hostName }.plus(ConfigName.HOSTNAME.value).map{ it.length }.maxBy { it },
            ConfigName.USER to entries.mapNotNull { it.user }.plus(ConfigName.USER.value).map{ it.length }.maxBy { it },
            ConfigName.PORT to entries.mapNotNull { it.port.toStringOrEmpty() }.plus(ConfigName.PORT.value).map{ it.length }.maxBy { it },
        )
        return data
    }

    private fun printHeaders(cellWidthData: Map<ConfigName, Int>) {
        val headers = ConfigName.values().map { it.value.leftText(cellWidthData[it]) }
        val preHeader = ConfigName.values().map { "".leftText(cellWidthData[it], '-') }

        echo(preHeader.joinToString("+","+", postfix = "+"))
        echo(headers.joinToString("|","|", postfix = "|"))
        echo(preHeader.joinToString("+","+", postfix = "+"))
    }

    private fun printEntries(entries: List<SSHConfig>, cellWidthData: Map<ConfigName, Int>) {
        val hostPadSize = cellWidthData[ConfigName.HOST]
        val hostNamePadSize = cellWidthData[ConfigName.HOSTNAME]
        val userPadSize = cellWidthData[ConfigName.USER]
        val portPadSize = cellWidthData[ConfigName.USER]
        val formattedEntries = entries.map {
            "|${it.host.leftText(hostPadSize)}|${it.hostName.leftText(hostNamePadSize)}|${it.user.leftText(userPadSize)}|${it.port.toStringOrEmpty().rightText(portPadSize)}|"
        }
        formattedEntries.forEach { echo(it) }

        val footer = ConfigName.values().map { "".leftText(cellWidthData[it], '-') }
        echo(footer.joinToString("+","+", postfix = "+"))
    }

    private fun String?.leftText(padSize: Int?, padChar: Char? = ' '): String = this.orEmpty().let {"$padChar$it$padChar".padEnd(ceil((padSize?.takeIf { it > defaultPadding } ?: defaultPadding) *1.3).toInt(), padChar ?: ' ')}
    private fun String?.rightText(padSize: Int?, padChar: Char? = ' '): String = this.orEmpty().let {"$padChar$it$padChar".padStart(ceil((padSize?.takeIf { it > defaultPadding } ?: defaultPadding) *1.3).toInt(), padChar ?: ' ')}

}