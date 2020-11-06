package no.nav.helse.sparkel.personinfo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

internal class PdlInterpreter {

    fun interpret(pdlReply: JsonNode): JsonNode {

        if (pdlReply["errors"] == null || pdlReply["errors"].isArray || !pdlReply["errors"].isEmpty) {
            val errors = pdlReply["errors"].map { it["message"]?.textValue() ?: "no message" }
            throw RuntimeException(errors.joinToString())
        }

        val resultNode = ObjectMapper().createObjectNode()
        //if(pdlReply["data"]?
        return resultNode
         /*pdlReply["data"].ifNotJsonNull()?.jsonObject?.let { data ->

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
*/
    }

}

