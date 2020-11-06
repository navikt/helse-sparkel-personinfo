package no.nav.helse.sparkel.personinfo

import com.fasterxml.jackson.databind.JsonNode
import org.slf4j.LoggerFactory

internal class PdlInterpreter {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun interpret(pdlReply: JsonNode): JsonNode {

        if (pdlReply["errors"] == null || pdlReply["errors"].isArray || !pdlReply["errors"].isEmpty) {
            pdlReply["errors"].forEach {
                log.error(it["message"]?.textValue())
            }
            throw RuntimeException("${pdlReply["errors"].size()} errors")
        }

        mutableMapOf<String, JsonElement>().also { elements ->
            pdlReply["errors"].ifNotJsonNull()?.jsonArray?.let { errors ->
                elements["errors"] = errorMsgs(errors)
            }

            pdlReply["data"].ifNotJsonNull()?.jsonObject?.let { data ->

                data["hentPerson"].ifNotJsonNull()?.jsonObject?.let { hentPerson ->
                    hentPerson["navn"].ifNotJsonNull()?.jsonArray?.let { navn ->
                        elements["navn"] = names(navn)
                    }

                    hentPerson["familierelasjoner"].ifNotJsonNull()?.jsonArray?.let { relasjoner ->
                        elements["familierelasjoner"] = familierelasjoner(relasjoner)
                    }
                }
            }
        }

    }

}

