package ru.makkarpov.scamp

import akka.util.{ByteString, ByteStringBuilder}

class IteratorPlayground {
  val iter = ByteString().iterator

  iter.len

  val dst = new ByteStringBuilder
}
