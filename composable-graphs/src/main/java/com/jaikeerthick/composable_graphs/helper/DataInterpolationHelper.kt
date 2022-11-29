package com.jaikeerthick.composable_graphs.helper

object DataInterpolationHelper {

    fun getInterpolatedData(numbers: List<Number?>): List<PlotInfo> {
        return List(numbers.size) { index ->
            getPointValue(numbers, index)
        }
    }

    private fun getPointValue(numbers: List<Number?>, currentIndex: Int): PlotInfo  {
        return when {
            numbers[currentIndex] != null -> PlotInfo.Regular(numbers[currentIndex] ?: 0)
            currentIndex == 0 || currentIndex == numbers.size -1 -> {
                PlotInfo.InterpolatedEndPoint
            }
            else -> {
                var leftIndex = currentIndex - 1
                var rightIndex = currentIndex + 1
                var leftValue: Number? = null
                var rightValue: Number? = null

                while(leftIndex >= 0 && leftValue == null) {
                    if(numbers[leftIndex] != null) leftValue = numbers[leftIndex]
                    leftIndex--
                }

                while(rightIndex < numbers.size && rightValue == null) {
                    if(numbers[rightIndex] != null) rightValue = numbers[rightIndex]
                    rightIndex++
                }
                rightIndex--
                leftIndex++
                if(rightValue != null && leftValue != null) {
                    val top = (rightValue.toFloat()) - (leftValue.toFloat())
                    val bottom = rightIndex - leftIndex
                    val slope = top/bottom
                    val yIntercept = rightValue.toFloat() - (slope*rightIndex)
                    PlotInfo.Interpolated((slope * (currentIndex)) + yIntercept)
                }
                else PlotInfo.InterpolatedEndPoint
            }

        }
    }

    sealed class PlotInfo {
        data class Regular(val value: Number): PlotInfo()
        data class Interpolated(val value: Number): PlotInfo()
        object InterpolatedEndPoint: PlotInfo()
    }
}