package greeting.server

import io.grpc.ServerBuilder

fun main() {
    val port = 50051

    val server = ServerBuilder
        .forPort(port)
        .addService(GreetingServerImpl())
        .addService(CalculatorServerImpl())
        .build()

    server.start()
    println("Server started")
    println("Listening on port $port")

    Runtime
        .getRuntime()
        .addShutdownHook(
            Thread {
                println("Received shutdown request")
                server.shutdown()
                println("Server stopped")
            }
        )

    server.awaitTermination()
}
