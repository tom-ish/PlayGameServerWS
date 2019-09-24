package models

case class Msg[T](msgType: String, obj: T)
