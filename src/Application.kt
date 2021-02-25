package com.redeyemedia.io

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.jackson.jackson
import io.ktor.request.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

data class Response (val status: String)

object todos : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 255)

    // override val primaryKey = PrimaryKey(id, name="PK_Todo_ID")

    fun toTodo(row: ResultRow): Todo =
        Todo(
            id = row[id],
            name = row[name]
        )
}

data class Todo(
    val id: Int? = null,
    val name: String
)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    Database.connect("jdbc:mysql://localhost:3306/todobd?useSSL=false", driver = "com.mysql.cj.jdbc.Driver",
        user = "root", password = "test1234")

    transaction {
        todos.insert {
            it[id] = (100000..999999).random()
            it[name] = "Random number generator"
        }
    }
    //Gradle
    // compile("mysql:mysql-connector-java:5.1.48")

    install(StatusPages) {
        exception<Throwable> { e ->
            call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
        }
    }
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(Routing) {
        route("/todo") {
            get("/") {
                val todos = transaction {
                    todos.selectAll().map {todos.toTodo(it)}
                }
                call.respond(todos)
            }

            post("/") {

            }
        }
    }

/*    routing {
        this.root()
        this.postRoot()

        get("/todos") {
            //call.respond(Response (status = "OK"))
            call.respond(TodoList(userList))
        }

        post("/todos") {
            val request = call.receive<Todo>()
            call.respond(request)
        }
    }*/
}

