package com.flickrmap.flickrmap.model;

/**
 *
 * the class taht calculates the hamiltonian path that travels as 'lightly' as possible in the graph described by the adjacency matrix.
 * TSP - Traveling Salesman Problen is in NP-hard so this algorithm does not guarantee the best solution.
 * however - teh NN (Nearest Neighbor) approach is guarantied to complete in polinommial time and provide a path that is at most 1.5 time 'heavier'
 * than the lightest path.
 *
 * implementation is taken from
 * https://code.google.com/p/tspPath-ikm-java/source/browse/trunk/TSP/src/NearestNeighbor.java?r=3
 */

public class TSPNearestNeighbor implements TSPPath {

    // find the path using nearest neighbor approach. the item at '0' index is the starting node.

    @Override
    public int[] tspPath(final double[][] adjacencyMatrix) {

        int[] path = new int[ adjacencyMatrix[ 0 ].length ];

        path[ 0 ] = 0;
        int currentNode = 0;

        /**
         * until there are nodes that are not yet been visited
         */
        int i = 1;
        while (i < path.length) {
            // find next node
            int nextNode = findMin(adjacencyMatrix[ currentNode ], path);
            // if the node is not -1 (meaning if there is a node to be visited
            if (nextNode != -1) {
                // add the node to the path
                path[ i ] = nextNode;
                // update currentNode and i
                currentNode = nextNode;
                i++;
            }
        }
        return path;
    }

    /**
     * Find the nearest node that has not yet been visited
     *
     * @param row
     * @return next node to visit
     */
    private int findMin(double[] row, int[] path) {

        int nextNode = -1;
        int i = 0;
        double min = Double.MAX_VALUE;

        while (i < row.length) {
            if (isNodeInPath(path, i) == false && row[ i ] < min) {
                min = row[ i ];
                nextNode = i;
            }
            i++;
        }
        return nextNode;
    }

    /**
     * Check if the node is in the path
     *
     * @param node
     * @return true: if the node is already in the path, false otherwise
     */
    public boolean isNodeInPath(int[] path, int node) {

        for (int i = 0; i < path.length; i++) {
            if (path[ i ] == node) {
                return true;
            }
        }
        return false;
    }
}
