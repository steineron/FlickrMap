package com.flickrmap.flickrmap.model;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(MockitoJUnitRunner.class)
public class TSPNearestNeighbor2Test {

    private double[][] mGraph;

    private TSPPath mTSPPath;

    private boolean pathStartsAtZero(int[] path) {

        return path != null && path[0] == 0;
    }

    // checks that the path is valid nad is a Hemiltonian path - traverses all vertices only once
    private boolean pathIsHemilton(int path[]) {

        boolean is = path != null;
        if (is) {
            int visits[] = new int[path.length];
            try {
                for (int i = 0; i < path.length; i++) {
                    if (visits[path[i]] == 1) {
                        //path repeats a visit
                        is = false;
                        break;
                    }
                    visits[path[i]] = 1;
                }
            }
            catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                // any out -of -bounds exception means the path is not valid
                is = false;
            }
            finally {
                // test that all nodes have been visited
                for (int i = 0; i < visits.length; i++) {
                    is = is && visits[i] == 1;
                }
            }
        }
        return is;
    }

    @Before
    public void initTSP() {

        mTSPPath = new TSPNearestNeighbor2();
    }

    @Test
    public void test_100_x_100_TSP_is_executable_and_completes() throws Exception {

        Random rand = new Random();
        int nodes = 100;
        mGraph = new double[nodes][nodes];
        for (int i = 0; i < nodes; i++) {
            for (int j = 0; j < nodes; j++) {
                mGraph[i][j] = i == j ?
                        0 :
                        rand.nextDouble() * (1000);
            }
        }
        System.out.println(System.currentTimeMillis());
        int[] result = mTSPPath.tspPath(mGraph);
        System.out.println(System.currentTimeMillis());
        System.out.println("\n");
        assertNotNull(result);
        assertEquals(nodes, result.length);
        assertTrue(pathStartsAtZero(result));
        assertTrue(pathIsHemilton(result));

    }

    @Test
    public void test_100_x_100_fully_connected_TSP() throws Exception {

        Random rand = new Random();
        int nodes = 100;
        mGraph = new double[nodes][nodes];
        for (int i = 0; i < nodes; i++) {
            for (int j = 0; j < nodes; j++) {
                mGraph[i][j] = i == j ?
                        0 :
                        rand.nextDouble() * (1000);
                mGraph[j][i] = mGraph[i][j];
            }
        }
        System.out.println(System.currentTimeMillis());
        int[] result = mTSPPath.tspPath(mGraph);
        System.out.println("\n");
        System.out.println(System.currentTimeMillis());
        assertNotNull(result);
        assertEquals(nodes, result.length);
        assertTrue(pathStartsAtZero(result));
        assertTrue(pathIsHemilton(result));

    }

    @Test
    public void test_known_TSP() throws Exception {

        mGraph = new double[][]{
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


        int[] result = mTSPPath.tspPath(mGraph);
        assertNotNull(result);
        assertEquals(10, result.length);
        // should be0	4	2	3	8	7	9	1	6	5
        assertEquals(result[0], 0);
        assertEquals(result[1], 4);
        assertEquals(result[2], 2);
        assertEquals(result[3], 3);
        assertEquals(result[4], 8);
        assertEquals(result[5], 7);
        assertEquals(result[6], 9);
        assertEquals(result[7], 1);
        assertEquals(result[8], 6);
        assertEquals(result[9], 5);
        assertTrue(pathStartsAtZero(result));
        assertTrue(pathIsHemilton(result));

    }

    @Test
    public void test_ordered_TSP() throws Exception {

        mGraph = new double[][]{
                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                {9, 0, 1, 2, 3, 4, 5, 6, 7, 8},
                {8, 9, 0, 1, 2, 3, 4, 5, 6, 7},
                {7, 8, 9, 0, 1, 2, 3, 4, 5, 6},
                {6, 7, 8, 9, 0, 1, 2, 3, 4, 5},
                {5, 6, 7, 8, 9, 0, 1, 2, 3, 4},
                {4, 5, 6, 7, 8, 9, 0, 1, 2, 3},
                {3, 4, 5, 6, 7, 8, 9, 0, 1, 2},
                {2, 3, 4, 5, 6, 7, 8, 9, 0, 1},
                {1, 2, 3, 4, 5, 6, 7, 8, 9, 0}
        };


        int[] result = mTSPPath.tspPath(mGraph);
        assertNotNull(result);
        assertEquals(10, result.length);
        assertTrue(pathStartsAtZero(result));
        assertTrue(pathIsHemilton(result));
        // should be 0 1 2 3 4 5 6 7 8 9
        for (int i = 0; i < result.length; i++) {

            assertEquals(result[i], i);
        }


    }
}