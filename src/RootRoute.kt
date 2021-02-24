package com.redeyemedia.io

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.root() {
        get("/") {
            call.respondText("Hello ktor", ContentType.Text.Plain)
        }
    }

fun Routing.postRoot() {
    post ("/"){
        val post = call.receive<String>()
        call.respondText("Received $post Yo!", ContentType.Text.Plain)
    }
}

