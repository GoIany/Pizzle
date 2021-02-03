package com.yjh.pizzle

import java.io.Serializable

class GameData: Serializable {

    var isContinue = false
    var puzzleArray: Array<IntArray>? = null
    var imgUri: String? = null
    var playSec: Int? = 0
    var xNum = 0
    var yNum = 0
    var orientationVertical = true
    var showNum = false

}



