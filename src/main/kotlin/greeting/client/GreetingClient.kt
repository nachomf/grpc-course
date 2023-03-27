package greeting.client

import com.proto.greeting.GreetingRequest
import com.proto.greeting.GreetingResponse
import com.proto.greeting.GreetingServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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
        "long_greet" -> doLongGreet(channel)
        "greet_everyone" -> doGreetEveryOne(channel)
        else -> println("Invalid Keyword ${args[0]}")
    }

    println("Shutting down client")
    channel.shutdown()
}

fun doGreetEveryOne(channel: ManagedChannel?) {
    println("Enter Do greet everyOne")

    val latch = CountDownLatch(1)

    class CustomClientStreamObserver: StreamObserver<GreetingResponse>{
        override fun onNext(response: GreetingResponse) {
            println(response.result)
        }

        override fun onError(t: Throwable?) {}

        override fun onCompleted() {
            latch.countDown()
        }
    }

    val stream = GreetingServiceGrpc
        .newStub(channel)
        .greetEveryone(CustomClientStreamObserver())

    val names = listOf(
        "Name 1",
        "Name 2",
        "Name 3",
    )

    names.forEach { name ->
        stream.onNext(GreetingRequest.newBuilder().setFirstName(name).build())
    }

    stream.onCompleted()
    latch.await(3, TimeUnit.SECONDS)
}

fun doLongGreet(channel: ManagedChannel?) {
    println("Do long greet")

    val names = listOf(
        "Name 1",
        "Name 2",
        "Name 3",
    )
    val latch = CountDownLatch(1)

    class CustomClientStreamObserver: StreamObserver<GreetingResponse>{
        override fun onNext(response: GreetingResponse) {
            println(response.result)
        }

        override fun onError(t: Throwable?) {}

        override fun onCompleted() {
            latch.countDown()
        }
    }

    val stream = GreetingServiceGrpc
        .newStub(channel)
        .longGreet(CustomClientStreamObserver())

    names.forEach { name ->
        stream.onNext(GreetingRequest.newBuilder().setFirstName(name).build())
    }

    stream.onCompleted()
    latch.await(3, TimeUnit.SECONDS)
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
