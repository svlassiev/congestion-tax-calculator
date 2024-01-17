package server

import congestion.calculator.CongestionTaxCalculator
import congestion.calculator.TaxableVehicle
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Instant
import java.time.ZoneOffset
import java.util.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    val taskResults = mutableMapOf<String, Int>()

    install(ContentNegotiation) { gson() }

    routing {
        // Example of POST request for Wednesday, August 14, 2013 1:46:01 PM, Wednesday, August 14, 2013 9:42:01 AM, Wednesday, August 14, 2013 4:04:13 PM
        // curl -X POST -H "Content-Type: application/json" -d '{"vehicleType": "car", "tollPasses": [1376487961000, 1376473321000, 1376496253000]}'
        post("/task") {
            val taskId = UUID.randomUUID().toString()
            val request = call.receive<TaskRequest>()
            val vehicle = TaxableVehicle.getVehicle(request.vehicleType)
            val tollPasses = request.tollPasses.map { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDateTime() }.toTypedArray()
            val taxInSEK = CongestionTaxCalculator().getTax(vehicle, tollPasses)
            taskResults[taskId] = taxInSEK
            call.respond(TaskIdResponse(taskId, request.vehicleType, request.tollPasses))
        }

        get("/task/{taskId}") {
            val taskId = call.parameters["taskId"]
            if (taskId == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                val taxInSEK = taskResults[taskId]
                if (taxInSEK == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(TaskResponse(taskId, taxInSEK))
                }
            }
        }

        get("/") {
            call.respondText("Congestion Tax Calculator Service")
        }
    }
}

data class TaskRequest(val vehicleType: String, val tollPasses: List<Long>)
data class TaskIdResponse(val taskId: String, val vehicleType: String, val tollPasses: List<Long>)
data class TaskResponse(val taskId: String, val taxInSEK: Int)
