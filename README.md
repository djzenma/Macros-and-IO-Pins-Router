# Router
Routes the placed MACROS as well as the I/O PINS from any LEF and DEF files and writes the result in a DEF file. 


# Global and Detailed Routing
## The Problem
The problem to solve is that after placing the cells, we need to route the array of pins in each net together.

This is done on the Metal layers defined in the DEF and LEF files.

The Algorithm we will use in routing is the A* pathfinding Algorithm.

## General Structure of the Routing Packages
The packages containing the router Algorithm are the following:

The Algorithm Package contains:

A Maze class which represents an abstraction of the AStar class to facilitate the methods that will be used by the Algorithm/Main class.

The A* algorithm in the AStar class and each node in its search area is represented in the Node class. The search area is a 3d array with each height index representing a metal layer.

The Routing Package contains:

The Router class which is responsible of performing first global routing then detailed routing on every net block. (Discussed in the following section)

GBox class which is the equivalent of a mini struct version of the Algorithm/Node class to represent each node (GBox) in the global routing grid.

A Full documentation of each function in the majority of our classes can be found on the project’s github: https://github.com/djzenma/Router
