package no.nav.helse.sparkel.personinfo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

internal class PdlInterpreter {

    fun interpret(pdlReply: JsonNode): JsonNode {

        if (pdlReply["errors"] != null && pdlReply["errors"].isArray && !pdlReply["errors"].isEmpty) {
            val errors = pdlReply["errors"].map { it["message"]?.textValue() ?: "no message" }
            throw RuntimeException(errors.joinToString())
        }

        pdlReply["data"]["hentPerson"].let { hentPerson ->
            hentPerson["doedsfall"]?.let { dødsfall ->
                if (dødsfall.size() == 0) return buildResponse(null)
                if (dødsfall.size() > 1) return buildResponse(håndterFlereMastere(dødsfall))
                return buildResponse(dødsfall[0]["doedsdato"].asText())
            }
        }
        return buildResponse(null)
    }

    private fun buildResponse(tmp: String?) = ObjectMapper().createObjectNode().put("dødsdato", tmp)

    private fun håndterFlereMastere(dødsfall: JsonNode): String {
        val dødsdatoer: Set<String> =
            dødsfall.fold(mutableSetOf()) { acc, jsonNode ->
                acc.also { it.add(jsonNode["doedsdato"].asText()) }
            }

        return if (dødsdatoer.size > 1)
            throw java.lang.RuntimeException(
                "Masterdata er uenige om dødsårsak, trenger funksjonell avklaring. Mottok ${dødsdatoer.joinToString(" og ")}."
            )
        else
            dødsdatoer.first()
    }

    private fun JsonNode?.ifNotJsonNull() = if (this?.isNull == false) this else null
}

