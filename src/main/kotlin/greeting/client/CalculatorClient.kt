package greeting.client

import com.proto.calculator.AVGRequest
import com.proto.calculator.AVGResponse
import com.proto.calculator.CalculatorServiceGrpc
import com.proto.calculator.MaxRequest
import com.proto.calculator.MaxResponse
import com.proto.calculator.PrimeRequest
import com.proto.calculator.SumRequest
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
        "sum" -> doSum(channel)
        "prime" -> doPrime(channel)
        "avg" -> doAvg(channel)
        "max" -> doMax(channel)
        else -> println("Invalid Keyword ${args[0]}")
    }

    println("Shutting down client")
    channel.shutdown()
}

fun doMax(channel: ManagedChannel?) {
    println("Enter in max function")

    val numbers = listOf(1, 5, 3, 6, 2,20)
    val latch = CountDownLatch(1)

    class CustomClientStreamObserver : StreamObserver<MaxResponse> {
        override fun onNext(response: MaxResponse) {
            println("on next")
            println(response.resultList)
        }

        override fun onError(t: Throwable?) {}

        override fun onCompleted() {
            latch.countDown()
        }
    }

    val stream = CalculatorServiceGrpc
        .newStub(channel)
        .max(CustomClientStreamObserver())

    numbers.forEach { number ->
        stream.onNext(MaxRequest.newBuilder().setNumber(number).build())
    }

    stream.onCompleted()
    latch.await(3, TimeUnit.SECONDS)
}

fun doSum(channel: ManagedChannel?) {
    println("Enter in doSum function")
    val result = CalculatorServiceGrpc
        .newBlockingStub(channel)
        .sum(
            SumRequest
                .newBuilder()
                .setFirstArg(4)
                .setSecondArg(6)
                .build()
        )

    println("Response is $result")
}

fun doPrime(channel: ManagedChannel?) {
    println("Enter in doPrime function")
    CalculatorServiceGrpc
        .newBlockingStub(channel)
        .prime(
            PrimeRequest
                .newBuilder()
                .setNumber(120)
                .build()
        ).forEachRemaining { response ->
            println("Response is $response")
        }
}

fun doAvg(channel: ManagedChannel?) {
    println("Enter in avg function")

    val numbers = listOf(1, 2, 3, 4, 5,843,235)
    val latch = CountDownLatch(1)

    class CustomClientStreamObserver : StreamObserver<AVGResponse> {
        override fun onNext(response: AVGResponse) {
            println(response.avgResult)
        }

        override fun onError(t: Throwable?) {}

        override fun onCompleted() {
            latch.countDown()
        }
    }

    val stream = CalculatorServiceGrpc
        .newStub(channel)
        .avg(CustomClientStreamObserver())

    numbers.forEach { number ->
        stream.onNext(AVGRequest.newBuilder().setNumber(number).build())
    }

    stream.onCompleted()
    latch.await(3, TimeUnit.SECONDS)
}