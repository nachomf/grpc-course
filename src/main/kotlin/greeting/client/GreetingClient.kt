package greeting.client

import com.proto.greeting.GreetingRequest
import com.proto.greeting.GreetingServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder

fun main(args: Array<String>) {

    if (args.isEmpty()) {
        println("Need one argument to work")
        return
    }

    val channel = ManagedChannelBuilder
        .forAddress("localhost", 50051)
        .usePlaintext()
        .build()

    when (args[0]) {
        "greet" -> doGreet(channel)
        "greet_many_times" -> doGreetManyTimes(channel)
        else -> println("Invalid Keyword ${args[0]}")
    }

    println("Shutting down client")
    channel.shutdown()
}

fun doGreet(channel: ManagedChannel?) {
    println("Enter in doGreet function")
    val result = GreetingServiceGrpc
        .newBlockingStub(channel)
        .greet(
            GreetingRequest
                .newBuilder()
                .setFirstName("Nacho")
                .build()
        )

    println("Response is $result")
}

fun doGreetManyTimes(channel: ManagedChannel?) {
    println("Enter in doGreetManyTimes function")
    GreetingServiceGrpc
        .newBlockingStub(channel)
        .greetManyTimes(
            GreetingRequest
                .newBuilder()
                .setFirstName("Nacho")
                .build()
        )
        .forEachRemaining { response -> println("Response is ${response.result}") }
}
