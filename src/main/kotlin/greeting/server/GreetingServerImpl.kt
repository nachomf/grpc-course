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

    override fun longGreet(responseObserver: StreamObserver<GreetingResponse>): StreamObserver<GreetingRequest> {
        val stringBuilder = StringBuilder()

        class CustomStreamObserver : StreamObserver<GreetingRequest> {
            override fun onNext(request: GreetingRequest) {
                stringBuilder.append("Hello ")
                stringBuilder.append(request.firstName)
                stringBuilder.append("!\n")
            }

            override fun onError(t: Throwable) {
                responseObserver.onError(t)
            }

            override fun onCompleted() {
                responseObserver.onNext(GreetingResponse.newBuilder().setResult(stringBuilder.toString()).build())
                responseObserver.onCompleted()
            }
        }

        return CustomStreamObserver()
    }

    override fun greetEveryone(responseObserver: StreamObserver<GreetingResponse>): StreamObserver<GreetingRequest> {
        class CustomStreamObserver : StreamObserver<GreetingRequest> {
            override fun onNext(request: GreetingRequest) {
                responseObserver.onNext(GreetingResponse.newBuilder().setResult("Hello ${request.firstName}").build())
            }

            override fun onError(t: Throwable) {
                responseObserver.onError(t)
            }

            override fun onCompleted() {
                responseObserver.onCompleted()
            }
        }

        return CustomStreamObserver()
    }
}