package com.jetbrains.handson.httpapi.routes

import com.jetbrains.handson.httpapi.models.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.customerRouting() {
    route("/customer") {
        get {
            if(customerStorage.isNotEmpty()) {
                call.respond(customerStorage)
            } else {
                call.respondText("no customers found", status = HttpStatusCode.NotFound)
            }
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val customer =
                customerStorage.find {it.id == id} ?: return@get call.respondText(
                    "No customer with id $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(customer)
        }
        post {
            val customer = call.receive<Customer>()
            customerStorage.add(customer)
            call.respondText("Customer successfully added", status = HttpStatusCode.Accepted)
        }
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if(customerStorage.removeIf { it.id == id}) {
                call.respondText("Customer successfully removed", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not found", status = HttpStatusCode.NotFound)
            }
        }
    }
}
fun Application.registerCustomerRoutes() {
    routing {
        customerRouting()
    }
}