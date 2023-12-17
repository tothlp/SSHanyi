package hu.tothlp.sshanyi

import com.github.ajalt.clikt.testing.test
import kotlin.test.Test
import kotlin.test.assertEquals

class InitTest {

    @Test
    fun testInit() {
        val command = Init()
        val result = command.test("")
        // TODO: println cant be used for stdout check, use echo if needed.
        //assertEquals(result.stdout, "Checking if /Users/tothlp/.ssh/config exist..\nThe config exists!")
        assertEquals(result.statusCode, 0)
        assertEquals(command.commandName, "init")
    }

}