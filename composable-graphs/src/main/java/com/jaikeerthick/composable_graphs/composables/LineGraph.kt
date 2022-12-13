package com.jaikeerthick.composable_graphs.composables

import android.graphics.Paint
import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaikeerthick.composable_graphs.color.LinearGraphColors
import com.jaikeerthick.composable_graphs.data.GraphData
import com.jaikeerthick.composable_graphs.helper.DataInterpolationHelper
import com.jaikeerthick.composable_graphs.helper.GraphHelper
import com.jaikeerthick.composable_graphs.style.LineGraphStyle
import com.jaikeerthick.composable_graphs.style.LinearGraphVisibility
import com.jaikeerthick.composable_graphs.style.YValueRange
import com.jaikeerthick.composable_graphs.style.YThreshHoldValueLine
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun LineGraph(
    xAxisData: List<GraphData>,
    yAxisData: List<Number?>,
    header: @Composable() () -> Unit = {},
    style: LineGraphStyle = LineGraphStyle(),
    onPointClicked: (pair: Pair<Any, Any>) -> Unit = {},
    isPointValuesVisible: Boolean = false,
    yValueRange: YValueRange? = null,
    yThreshHoldValueLine: YThreshHoldValueLine? = null
) {

    val paddingRight: Dp = if (style.visibility.isYAxisLabelVisible) 20.dp else 0.dp
    val paddingBottom: Dp = if (style.visibility.isXAxisLabelVisible) 20.dp else 0.dp

    val offsetList = remember { mutableListOf<Offset>() }
    val isPointClicked = remember { mutableStateOf(false) }
    val clickedPoint: MutableState<Offset?> = remember { mutableStateOf(null) }


    Column(
        modifier = Modifier
            .background(
                color = style.colors.backgroundColor
            )
            .fillMaxWidth()
            .padding(style.paddingValues)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {

        if (style.visibility.isHeaderVisible) {
            header()
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(style.height)
                .padding(horizontal = 10.dp)
                .pointerInput(true) {

                    detectTapGestures { p1: Offset ->

                        val shortest = offsetList.find { p2: Offset ->

                            /** Pythagorean Theorem
                             * Using Pythagorean theorem to calculate distance between two points :
                             * p1 =  p1(x,y) which is the touch point
                             * p2 =  p2(x,y)) which is the point plotted on graph
                             * Formula: c = sqrt(a² + b²), where a = (p1.x - p2.x) & b = (p1.y - p2.y),
                             * c is the distance between p1 & p2
                            Pythagorean Theorem */

                            val distance = sqrt(
                                (p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2)
                            )
                            val pointRadius = 15.dp.toPx()

                            distance <= pointRadius
                        }

                        shortest?.let {

                            clickedPoint.value = it
                            isPointClicked.value = true

                            //
                            val index = offsetList.indexOf(it)
                            onPointClicked(Pair(xAxisData[index].text, yAxisData[index] ?: 0))
                        }

                    }

                },
        ) {


            //println("Entered scope")
            /**
             * xItemSpacing, yItemSpacing => space between each item that lies on the x and y axis
             * (size.width - 16.dp.toPx())
             *               ~~~~~~~~~~~~~ => padding saved for the end of the axis
             */

            val gridHeight = (size.height) - paddingBottom.toPx()
            val gridWidth = size.width - paddingRight.toPx()

            // the maximum points for x and y axis to plot (maintain uniformity)
            val maxPointsSize: Int = minOf(xAxisData.size, yAxisData.size)

            // maximum of the y data list
            val absMaxY = GraphHelper.getAbsoluteMax(yAxisData, yValueRange?.maximumValue)

            val verticalStep = if(yValueRange?.yInterval == null ) {
                (absMaxY.toInt() / maxPointsSize.toFloat()).toInt()
            }
            else {
                yValueRange.yInterval
            }


            // generate y axis label
            val yAxisLabelList = mutableListOf<String>()

            if(yValueRange == null) {
                for (i in 0..maxPointsSize) {
                    val intervalValue = (verticalStep * i)
                    println("interval - $intervalValue")
                    yAxisLabelList.add(intervalValue.toString())
                }
            }
            else {
                var yAxisLabel = yValueRange.minimumValue
                while(yAxisLabel <= yValueRange.maximumValue) {
                    yAxisLabelList.add(yAxisLabel.toString())
                    yAxisLabel += yValueRange.yInterval
                }
            }

            val xItemSpacing = gridWidth / (maxPointsSize - 1)
            val yItemSpacing = gridHeight / (yAxisLabelList.size - 1)


            /**
             * Drawing Grid lines inclined towards x axis
             */
            if (style.visibility.isGridVisible) {
                for (i in 0 until maxPointsSize) {

                    // lines inclined towards x axis
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(xItemSpacing * (i), 0f),
                        end = Offset(xItemSpacing * (i), gridHeight),
                    )
                }

                for (i in 0 until yAxisLabelList.size) {
                    // lines inclined towards y axis
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, gridHeight - yItemSpacing * (i)),
                        end = Offset(gridWidth, gridHeight - yItemSpacing * (i)),
                    )
                }
            }

            //draw yAxisThreshhold lines

            if(yThreshHoldValueLine != null &&
                yValueRange != null &&
                yThreshHoldValueLine.thresholdValue >= yValueRange.minimumValue &&
                yThreshHoldValueLine.thresholdValue <= yValueRange.maximumValue
            ) {
                val offset = (yThreshHoldValueLine.thresholdValue.toFloat() - yValueRange.minimumValue)
                val yVal = gridHeight - (yItemSpacing * (offset / yValueRange.yInterval))

                drawLine(
                    color = yThreshHoldValueLine.lineColor,
                    start = Offset(0f, yVal),
                    end = Offset(gridWidth, yVal),
                    strokeWidth = 5f
                )
            }

            /**
             * Drawing text labels over the x- axis
             */
            if (style.visibility.isXAxisLabelVisible) {
                for (i in 0 until maxPointsSize) {

                    drawContext.canvas.nativeCanvas.drawText(
                        "${xAxisData[i].text}",
                        xItemSpacing * (i), // x
                        size.height, // y
                        Paint().apply {
                            color = android.graphics.Color.GRAY
                            textAlign = Paint.Align.CENTER
                            textSize = 12.sp.toPx()
                        }
                    )
                }
            }

            /**
             * Drawing text labels over the y- axis
             */
            if (style.visibility.isYAxisLabelVisible) {
                for (i in 0 until yAxisLabelList.size) {
                    drawContext.canvas.nativeCanvas.drawText(
                        "${yAxisLabelList[i]}",
                        size.width, //x
                        gridHeight - yItemSpacing * (i + 0), //y
                        Paint().apply {
                            color = android.graphics.Color.GRAY
                            textAlign = Paint.Align.CENTER
                            textSize = 12.sp.toPx()
                        }
                    )
                }
            }


            // plotting points
            /**
             * Plotting points on the Graph
             */

            offsetList.clear() // clearing list to avoid data duplication during recomposition
            val interpolatedData = DataInterpolationHelper.getInterpolatedData(yAxisData)
            for (i in 0 until maxPointsSize) {
                val yAxisPoint = interpolatedData[i]

                if(yAxisPoint is DataInterpolationHelper.PlotInfo.Regular ||
                    yAxisPoint is DataInterpolationHelper.PlotInfo.Interpolated) {

                    val pointValue = when(yAxisPoint) {
                        is DataInterpolationHelper.PlotInfo.Regular -> yAxisPoint.value
                        is DataInterpolationHelper.PlotInfo.Interpolated -> yAxisPoint.value
                        else -> throw IllegalStateException("Illegal state when getting plot point")
                    }

                    val x1 = xItemSpacing * i
                    val y1 = if(yValueRange == null) {
                        gridHeight - (yItemSpacing * (pointValue.toFloat() / verticalStep))
                    }
                    else {
                        val offset = (pointValue.toFloat() - yValueRange.minimumValue)
                        gridHeight - (yItemSpacing * (offset / yValueRange.yInterval))
                    }


                    offsetList.add(
                        Offset(
                            x = x1,
                            y = y1
                        )
                    )

                    /**
                     * Draws point value above the point
                     */
                    if (isPointValuesVisible) {
                        val pointTextSize = 12.sp.toPx()
                        val formattedValue = String.format("%02d", yAxisData[i])
                        drawContext.canvas.nativeCanvas.drawText(
                            formattedValue,
                            x1, // x
                            y1 - pointTextSize, // y
                            Paint().apply {
                                color = android.graphics.Color.GRAY
                                textAlign = Paint.Align.CENTER
                                textSize = pointTextSize
                            }
                        )
                    }
                }
            }

            if(style.bazierCurveEnabled) {
                //calculate bazier curve
                val controlPointsOne = mutableListOf<PointF>()
                val controlPointsTwo = mutableListOf<PointF>()

                for(i in 1 until offsetList.size) {
                    val cpXCoordinate = (offsetList[i].x + offsetList[i-1].x) / 2f
                    val cpOneYCoordinate = offsetList[i-1].y/.98f
                    val cpTwoYCoordinate = offsetList[i].y
                    controlPointsOne.add(PointF(cpXCoordinate, cpOneYCoordinate))
                    controlPointsTwo.add(PointF(cpXCoordinate, cpTwoYCoordinate))
                }

                //draw bazier curve
                val stroke = Path().apply {
                    reset()
                    moveTo(offsetList.first().x, offsetList.first().y)
                    for(i in 0 until offsetList.size - 1) {
                        cubicTo(
                            controlPointsOne[i].x, controlPointsOne[i].y,
                            controlPointsTwo[i].x, controlPointsTwo[i].y,
                            offsetList[i+1].x, offsetList[i+1].y
                        )
                    }
                }

                drawPath(
                    stroke,
                    color = Color.Black,
                    style = Stroke(
                        width = 5f,
                        cap = StrokeCap.Round
                    )
                )
            }

            else {
                /**
                 * drawing line connecting all circles/points
                 */
                drawPoints(
                    points = offsetList.subList(
                        fromIndex = 0,
                        toIndex = offsetList.size
                    ),
                    color = style.colors.lineColor,
                    pointMode = PointMode.Polygon,
                    strokeWidth = 2.dp.toPx(),
                )
            }

            //draw plot points
            offsetList.forEachIndexed {index, offset ->
                val yAxisPoint = interpolatedData[index]
                val pointColor = when(yAxisPoint) {
                    is DataInterpolationHelper.PlotInfo.Regular -> {
                        if(yThreshHoldValueLine != null && yAxisPoint.value.toInt() >= yThreshHoldValueLine.thresholdValue) {
                            style.colors.aboveThreshHoldPointColor
                        }
                        else {
                            style.colors.lowerThreshHoldPointColor
                        }
                    }
                    else -> Color.Transparent
                }
                drawCircle(
                    color = pointColor,
                    radius = 5.dp.toPx(),
                    center = offset
                )
            }


            /**
             * Drawing Gradient fill for the plotted points
             * Create Path from the offset list with start and end point to complete the path
             * then draw path using brush
             */
            val path = Path().apply {
                // starting point for gradient
                moveTo(
                    x = 0f,
                    y = gridHeight
                )

                for (i in 0 until offsetList.size) {
                    lineTo(offsetList[i].x, offsetList[i].y)
                }

                // ending point for gradient
                lineTo(
                    x = xItemSpacing * (yAxisData.size - 1),
                    y = gridHeight
                )

            }

            drawPath(
                path = path,
                brush = style.colors.fillGradient ?: Brush.verticalGradient(
                    listOf(Color.Transparent, Color.Transparent)
                )
            )

            /**
             * highlighting clicks when user clicked on the canvas
             */
            clickedPoint.value?.let {
                drawCircle(
                    color = style.colors.clickHighlightColor,
                    center = it,
                    radius = 12.dp.toPx()
                )
                if (style.visibility.isCrossHairVisible) {
                    drawLine(
                        color = style.colors.crossHairColor,
                        start = Offset(it.x, 0f),
                        end = Offset(it.x, gridHeight),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(15f, 15f)
                        )
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun LineGraphPreview() {
    val style = LineGraphStyle(
        visibility = LinearGraphVisibility(
            isYAxisLabelVisible = true,
            isGridVisible = true,
        ),
        colors = LinearGraphColors(
            lowerThreshHoldPointColor = Color.Red,
            aboveThreshHoldPointColor = Color.Blue
        ),
        bazierCurveEnabled = true
    )


    LineGraph(
        xAxisData = listOf("Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat").map {
            GraphData.String(it)
        }, // xAxisData : List<GraphData>, and GraphData accepts both Number and String types
        yAxisData = listOf(3,20, 6, 7, 8, 9, 10),
        style = style,
        yValueRange = YValueRange(3, 24, 3),
        yThreshHoldValueLine = YThreshHoldValueLine(10, Color.Gray)
    )
}


