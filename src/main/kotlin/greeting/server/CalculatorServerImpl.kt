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

    override fun avg(responseObserver: StreamObserver<AVGResponse>): StreamObserver<AVGRequest> {
        val numbers = mutableListOf<Int>()

        class CustomStreamObserver : StreamObserver<AVGRequest> {
            override fun onNext(request: AVGRequest) {
                numbers.add(request.number)
            }

            override fun onError(t: Throwable) {
                responseObserver.onError(t)
            }

            override fun onCompleted() {
                responseObserver.onNext(AVGResponse.newBuilder().setAvgResult(numbers.average()).build())
                responseObserver.onCompleted()
            }
        }

        return CustomStreamObserver()
    }

    override fun max(responseObserver: StreamObserver<MaxResponse>): StreamObserver<MaxRequest> {
        val numbers = mutableListOf<Int>()

        class CustomStreamObserver : StreamObserver<MaxRequest> {
            override fun onNext(request: MaxRequest) {
                numbers.add(request.number)
                responseObserver.onNext(MaxResponse.newBuilder().addAllResult(numbers.sorted()).build())
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