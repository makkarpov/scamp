package ru.makkarpov.scamp.misc

import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import ru.makkarpov.scamp.Packet

class DummyFlow[I, O] extends GraphStage[FlowShape[I, O]] {
  val in = Inlet[I]("in")
  val out = Outlet[O]("out")

  override def shape: FlowShape[I, O] = FlowShape.of(in, out)
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      println("dummy: instantiated")

      override def preStart(): Unit = {
        println("dummy: initially pulling `in`")
        pull(in)
      }

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          println(s"dummy: push: ${grab(in)}")
          pull(in)
        }

        override def onUpstreamFinish(): Unit = {
          println("dummy: upstream finished")
          super.onUpstreamFinish()
        }

        override def onUpstreamFailure(ex: Throwable): Unit = {
          println(s"dummy: upstream failure: $ex")
          super.onUpstreamFailure(ex)
        }
      })

      setHandler(out, new OutHandler {
        override def onPull(): Unit =
          println("dummy: pulled")

        override def onDownstreamFinish(): Unit = {
          println("dummy: downstream finished")
          super.onDownstreamFinish()
        }
      })
    }
}
