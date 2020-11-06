package no.nav.helse.sparkel.personinfo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.RuntimeException

internal class PdlInterpreterTest {

    val errorPdlResponse = "pdl-error-response.json".loadFromResources()

    val pdlInterpreter = PdlInterpreter()

    val om = ObjectMapper()

    @Test
    fun test(){
        val thrown = assertThrows( RuntimeException::class.java, {pdlInterpreter.interpret(om.readValue(errorPdlResponse)) } )
        assertEquals("1 error(s)", thrown.message)
    }


}


fun String.loadFromResources() : String {
    return ClassLoader.getSystemResource(this).readText()
}
