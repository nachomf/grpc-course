package greeting.server

import com.proto.calculator.*
import io.grpc.stub.StreamObserver

class CalculatorServerImpl : CalculatorServiceGrpc.CalculatorServiceImplBase() {
    override fun sum(request: SumRequest?, responseObserver: StreamObserver<SumResponse>?) {
        responseObserver!!.onNext(
            SumResponse
                .newBuilder()
                .setResult(request!!.firstArg + request.secondArg)
                .build()
        )
        responseObserver.onCompleted()
    }

    override fun prime(request: PrimeRequest?, responseObserver: StreamObserver<PrimeResponse>?) {
        var k = 2
        var number = request!!.number

        while (number > 1) {
            if (number % k == 0) {
                responseObserver!!.onNext(
                    PrimeResponse
                        .newBuilder()
                        .setPrimeNumber(k)
                        .build()
                )

                number /= k
            } else {
                k += 1
            }
        }

        responseObserver!!.onCompleted()
    }
}