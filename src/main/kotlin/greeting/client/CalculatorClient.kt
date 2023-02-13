package greeting.client

import com.proto.calculator.CalculatorServiceGrpc
import com.proto.calculator.PrimeRequest
import com.proto.calculator.SumRequest
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
        "sum" -> doSum(channel)
        "prime" -> doPrime(channel)
        else -> println("Invalid Keyword ${args[0]}")
    }

    println("Shutting down client")
    channel.shutdown()
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