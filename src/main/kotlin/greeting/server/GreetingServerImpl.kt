package greeting.server

import com.proto.greeting.GreetingRequest
import com.proto.greeting.GreetingResponse
import com.proto.greeting.GreetingServiceGrpc.GreetingServiceImplBase
import io.grpc.stub.StreamObserver

class GreetingServerImpl : GreetingServiceImplBase() {

    override fun greet(request: GreetingRequest?, responseObserver: StreamObserver<GreetingResponse>?) {
        responseObserver!!.onNext(
            GreetingResponse
                .newBuilder()
                .setResult("Hello ${request?.firstName ?: "no name"}")
                .build()
        )
        responseObserver.onCompleted()
    }

    override fun greetManyTimes(request: GreetingRequest?, responseObserver: StreamObserver<GreetingResponse>?) {
        val responseProvider = { index: Int ->
            GreetingResponse
                .newBuilder()
                .setResult("Hello ${request?.firstName ?: "no name"} Nro. $index")
                .build()
        }

        for (i in 0..10) {
            responseObserver!!.onNext(responseProvider.invoke(i))
        }

        responseObserver!!.onCompleted()
    }
}