package hu.tothlp.sshanyi.feature

import com.github.ajalt.clikt.core.CliktCommand

class Useless : CliktCommand(hidden = true, name = "easteregg") {

	override fun run() {
		val easter = """
     .-.            .-.
    /   \          /   \
   |   _ \        / _   |
   ;  | \ \      / / |  ;
    \  \ \ \_.._/ / /  /
     '. '.;'    ';,' .'
       './ _    _ \.'
       .'  a __ a  '.
  '--./ _,   \/   ,_ \.--'
 ----|   \   /\   /   |----
  .--'\   '-'  '-'    /'--.
      _>.__  -- _.-  `;
    .' _     __/     _/
   /    '.,:".-\    /:,
   |      \.'   `""`'.\\
    '-,.__/  _   .-.  ;|_
    /` `|| _/ `\/_  \_|| `\
   |    ||/ \-./` \ / ||   |
    \   ||__/__|___|__||  /
     \_ |_            _| /
    .'  \ =  _= _ = _= /`\
   /     `-;----=--;--'   \
   \    _.-'        '.    /
    `""`              `""`
	
		""".trimIndent()
		echo(easter)
		echo("Well done!!")
	}
}