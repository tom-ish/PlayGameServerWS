package utils

import java.awt.Color

import models._

import scala.util.Random

object Tools {

  def getRandomColor = {
    val r = new Random()
    val red = r.nextInt(256).toHexString
    val green = r.nextInt(256).toHexString
    val blue = r.nextInt(256).toHexString
    var redStr = "" + red
    var greenStr = "" + green
    var blueStr = "" + blue
    if(redStr.length == 1) redStr = "0" + redStr
    if(greenStr.length == 1) greenStr = "0" + greenStr
    if(blueStr.length == 1) blueStr = "0" + blueStr
    "#"+ redStr + greenStr + blueStr
  }

  def getColor(color: Color) = {
    "#" + color.getRed.toHexString + color.getGreen.toHexString + color.getBlue.toHexString
  }

  def getRandomPosition = {
    val r = new Random()
    val x = r.nextInt(GameArea.width)
    val y = r.nextInt(GameArea.height)
    Position(x, y)
  }
}
