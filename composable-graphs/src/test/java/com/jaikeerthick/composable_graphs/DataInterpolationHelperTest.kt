package com.jaikeerthick.composable_graphs

import com.jaikeerthick.composable_graphs.helper.DataInterpolationHelper
import org.junit.Assert
import org.junit.Test

class DataInterpolationHelperTest {

    @Test
    fun testDataInterpolationBasic() {
        val input = listOf(5f, 10f, 15f)
        val output = DataInterpolationHelper.getInterpolatedData(input)
        Assert.assertTrue(output[0] is DataInterpolationHelper.PlotInfo.Regular &&
                (output[0] as DataInterpolationHelper.PlotInfo.Regular).value == 5f &&
                output[1] is DataInterpolationHelper.PlotInfo.Regular &&
                (output[1] as DataInterpolationHelper.PlotInfo.Regular).value == 10f &&
                output[2] is DataInterpolationHelper.PlotInfo.Regular &&
                (output[2] as DataInterpolationHelper.PlotInfo.Regular).value == 15f
        )

    }

    @Test
    fun testDataInterpolationInterPolateMiddle() {
        val input = listOf(5f, null, 15f)
        val output = DataInterpolationHelper.getInterpolatedData(input)
        Assert.assertTrue(output[0] is DataInterpolationHelper.PlotInfo.Regular &&
                (output[0] as DataInterpolationHelper.PlotInfo.Regular).value == 5f &&
                output[1] is DataInterpolationHelper.PlotInfo.Interpolated &&
                (output[1] as DataInterpolationHelper.PlotInfo.Interpolated).value == 10f &&
                output[2] is DataInterpolationHelper.PlotInfo.Regular &&
                (output[2] as DataInterpolationHelper.PlotInfo.Regular).value == 15f
        )
    }

    @Test
    fun testDataInterpolationInterpolateStart() {
        val input = listOf(null, 10f, 15f)
        val output = DataInterpolationHelper.getInterpolatedData(input)
        Assert.assertTrue(output[0] is DataInterpolationHelper.PlotInfo.InterpolatedEndPoint &&
                output[1] is DataInterpolationHelper.PlotInfo.Regular &&
                (output[1] as DataInterpolationHelper.PlotInfo.Regular).value == 10f &&
                output[2] is DataInterpolationHelper.PlotInfo.Regular &&
                (output[2] as DataInterpolationHelper.PlotInfo.Regular).value == 15f
        )
    }

    @Test
    fun testDataInterPolationInterpolateEnd() {
        val input = listOf(5f, 10f, null)
        val output = DataInterpolationHelper.getInterpolatedData(input)
        Assert.assertTrue(output[0] is DataInterpolationHelper.PlotInfo.Regular &&
                (output[0] as DataInterpolationHelper.PlotInfo.Regular).value == 5f &&
                output[1] is DataInterpolationHelper.PlotInfo.Regular &&
                (output[1] as DataInterpolationHelper.PlotInfo.Regular).value == 10f &&
                output[2] is DataInterpolationHelper.PlotInfo.InterpolatedEndPoint
        )
    }

    @Test
    fun testDataInterPolationInterPolateAllNull() {
        val input = listOf(null, null, null)
        val output = DataInterpolationHelper.getInterpolatedData(input)
        Assert.assertTrue(output[0] is DataInterpolationHelper.PlotInfo.InterpolatedEndPoint &&
                output[1] is DataInterpolationHelper.PlotInfo.InterpolatedEndPoint &&
                output[2] is DataInterpolationHelper.PlotInfo.InterpolatedEndPoint
        )
    }

    @Test
    fun testDataInterPolationInterPolateDoubleMiddleNulls() {
        val input = listOf(5f, null, null, 20f)
        val output = DataInterpolationHelper.getInterpolatedData(input)
        Assert.assertTrue(output[0] is DataInterpolationHelper.PlotInfo.Regular &&
                (output[0] as DataInterpolationHelper.PlotInfo.Regular).value == 5f &&
                output[1] is DataInterpolationHelper.PlotInfo.Interpolated &&
                (output[1] as DataInterpolationHelper.PlotInfo.Interpolated).value == 10f &&
                output[2] is DataInterpolationHelper.PlotInfo.Interpolated &&
                (output[2] as DataInterpolationHelper.PlotInfo.Interpolated).value == 15f &&
                output[3] is DataInterpolationHelper.PlotInfo.Regular &&
                (output[3] as DataInterpolationHelper.PlotInfo.Regular).value == 20f
        )
    }

    @Test
    fun testDataInterPolationInterPolateTripleMiddleNulls() {
        val input = listOf(5f, null, null, null, 20f)
        val output = DataInterpolationHelper.getInterpolatedData(input)
        Assert.assertTrue(output[0] is DataInterpolationHelper.PlotInfo.Regular &&
                (output[0] as DataInterpolationHelper.PlotInfo.Regular).value == 5f &&
                output[1] is DataInterpolationHelper.PlotInfo.Interpolated &&
                (output[1] as DataInterpolationHelper.PlotInfo.Interpolated).value == 8.75f &&
                output[2] is DataInterpolationHelper.PlotInfo.Interpolated &&
                (output[2] as DataInterpolationHelper.PlotInfo.Interpolated).value == 12.50f &&
                output[3] is DataInterpolationHelper.PlotInfo.Interpolated &&
                (output[3] as DataInterpolationHelper.PlotInfo.Interpolated).value == 16.25f &&
                output[4] is DataInterpolationHelper.PlotInfo.Regular &&
                (output[4] as DataInterpolationHelper.PlotInfo.Regular).value == 20f
        )
    }

}