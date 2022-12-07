package com.jaikeerthick.composable_graphs.helper

import kotlin.math.roundToInt

object GraphHelper {

    private val IllegalGraphArgumentException = IllegalArgumentException("Graph currently does not support negative values")

    fun getAbsoluteMax(list: List<Number?>, maxValue: Number?): Number {
        validateArguments(list, maxValue)
        return if(maxValue != null) {
            maxValue
        }
        else {
            list.maxByOrNull {
                it?.toFloat()?.roundToInt() ?: 0
            } ?: 0
        }
    }

    fun getAbsoluteMin(list: List<Number?>, minValue: Number?): Number{
        validateArguments(list, minValue)
        return if(minValue != null) {
            minValue
        }
        else {
            list.minByOrNull {
                it?.toFloat()?.roundToInt() ?: 0
            } ?: 0
        }
    }

    fun roundMaxYPoint(point: Number): Number{

        // maxYPoint - the point gonna be rounded
        var maxYPoint = point

        if (maxYPoint.toInt() > 100) {
            maxYPoint = (maxYPoint.toFloat() / 50.0).roundToInt() * 50
            maxYPoint += 50

        }else{
            maxYPoint = (maxYPoint.toFloat() / 10.0).roundToInt() * 10
            maxYPoint += 10
        }

        return maxYPoint

    }

    private fun validateArguments(list: List<Number?>, maxOrMinValue: Number?) {
        list.forEach {
            if(isNegative(it)) throw IllegalGraphArgumentException
        }
        if(isNegative(maxOrMinValue)) throw IllegalGraphArgumentException
    }

    private fun isNegative(number: Number?): Boolean {
        return if(number != null && number.toInt() < 0 ) true
        else false
    }

}
