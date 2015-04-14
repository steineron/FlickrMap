package com.flickrmap.flickrmap.model;

import java.util.Stack;

/**
 * Copied from http://www.sanfoundry.com/java-program-implement-traveling-salesman-problem-using-nearest-neighbour-algorithm/
 */
public class TSPNearestNeighbour {

    private int numberOfNodes;

    private Stack<Integer> stack;

    public TSPNearestNeighbour() {

        stack = new Stack<Integer>();
    }

    public int[] tsp(double adjacencyMatrix[][]) {

        numberOfNodes = adjacencyMatrix[ 0 ].length;
        int[] visited = new int[ numberOfNodes ];
        int[] path = new int[ numberOfNodes ];
        int visits = 0;
        visited[ 0 ] = 1;
        stack.push(0);
        int element, dst = 0, i;
        double min = Double.MAX_VALUE;
        boolean minFlag = false;
        System.out.print(0 + "\t");
        path[ visits ] = 0;
        visits++;

        while (!stack.isEmpty()) {
            element = stack.peek();
            i = 0; // could be element in this instance of TSP
            min = Integer.MAX_VALUE;
            while (i < numberOfNodes) {
                if (adjacencyMatrix[ element ][ i ] >= 0.0 && visited[ i ] == 0) {
                    if (min >= adjacencyMatrix[ element ][ i ]) {
                        min = adjacencyMatrix[ element ][ i ];
                        dst = i;
                        minFlag = true;
                    }
                }
                i++;
            }
            if (minFlag) {
                visited[ dst ] = 1;
                stack.push(dst);
                System.out.print((dst) + "\t");

                path[ visits ] = dst;
                visits++;
                minFlag = false;
                continue;
            }
            stack.pop();
        }
        System.out.println("\nTSP path:");
        for (int j = 0; j < path.length; j++) {
            System.out.print((path[ j ]) + "\t");
        }
        System.out.println("\nTSP completed!");
        return path;
    }
}
