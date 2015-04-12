package com.flickrmap.flickrmap.model;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


import java.util.Random;

import static org.junit.Assert.*;

@SmallTest
@RunWith(MockitoJUnitRunner.class)
public class TSPNearestNeighbourTest {

    private int[][] mGraph;

    @Before
    public void buildAdjacencyMatrix() {



    }

    @Test
    public void test_100_x_100_TSP() throws Exception {

        System.out.println(System.currentTimeMillis());
        Random rand = new Random();
        mGraph = new int[100][100];
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                mGraph[i][j] = i==j ? 0 : rand.nextInt(1000);
            }
        }
        System.out.println(System.currentTimeMillis());
        TSPNearestNeighbour tsp = new TSPNearestNeighbour();
        int [] result = tsp.tsp(mGraph);
        System.out.println(System.currentTimeMillis());

    }
        @Test
    public void test_known_TSP() throws Exception {

        mGraph = new int[][]{
                {0, 374, 200, 223, 108, 178, 252, 285, 240, 356},
                {374, 0, 255, 166, 433, 199, 135, 95, 136, 17},
                {200, 255, 0, 128, 277, 128, 180, 160, 131, 247},
                {223, 166, 128, 0, 430, 47, 52, 84, 40, 155},
                {108, 433, 277, 430, 0, 453, 478, 344, 389, 423},
                {178, 199, 128, 47, 453, 0, 91, 110, 64, 181},
                {252, 135, 180, 52, 478, 91, 0, 114, 83, 117},
                {285, 95, 160, 84, 344, 110, 114, 0, 47, 78},
                {240, 136, 131, 40, 389, 64, 83, 47, 0, 118},
                {356, 17, 247, 155, 423, 181, 117, 78, 118, 0}
        };

        TSPNearestNeighbour tsp = new TSPNearestNeighbour();

        int [] result = tsp.tsp(mGraph);
        // should be 1	5	3	2	9	7	4	6	8
        assertEquals(result[1],1);
        assertEquals(result[2],9);
        assertEquals(result[3],7);
        assertEquals(result[4],8);
        assertEquals(result[5],3);
        assertEquals(result[6],5);
        assertEquals(result[7],6);
        assertEquals(result[8],2);
        assertEquals(result[9],4);

    }
}