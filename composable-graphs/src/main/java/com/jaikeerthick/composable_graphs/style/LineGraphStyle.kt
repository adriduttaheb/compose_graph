package com.jaikeerthick.composable_graphs.style

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jaikeerthick.composable_graphs.color.LinearGraphColors


data class LineGraphStyle(

    val paddingValues: PaddingValues = PaddingValues(
        all = 12.dp
    ),
    val height: Dp = 300.dp,
    val colors: LinearGraphColors = LinearGraphColors(),
    val visibility: LinearGraphVisibility = LinearGraphVisibility(),
    val bazierCurveEnabled: Boolean = false
    //val yAxisLabelPosition: LabelPosition = LabelPosition.LEFT
)


data class LinearGraphVisibility(
    val isCrossHairVisible: Boolean = false,
    val isYAxisLabelVisible: Boolean = false,
    val isXAxisLabelVisible: Boolean = true,
    val isGridVisible: Boolean = false,

    val isHeaderVisible: Boolean = false,
)

data class YValueRange(
    val minimumValue: Int,
    val maximumValue: Int,
    val yInterval: Int
)

data class YThreshHoldValueLine(
    val thresholdValue: Int,
    val lineColor: Color
)
