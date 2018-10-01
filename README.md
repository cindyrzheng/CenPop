# CenPop
CS project called "Where are the People", created using the prompt found here: https://homes.cs.washington.edu/~djg/teachingMaterials/spac/grossmanSPAC_project.html#Assignment

This program uses a text file containing 220,330 points of latitude and longitude, and the population of people around that latitude and longitude and different concurrency techniques such as ForkJoin framework and locks to first separate the US's population into a grid based on the location of the population points, and then calculate the population of a section of the grid (a rectangular "query") in the US in proportion to the total population. 

There are essentially 5 different ways that the population of the query is calculated: 
1. Sequential search through text file of census data
2. Same as 1, but uses ForkJoin framework to go through the text file of census data
3. Creates a smart grid in which additional queries after initial grid building take O(1) time to answer
4. Same as 3, but the grid is created using ForkJoin framework. 
5. Same as 3, but 4 threads create and add to a locked grid concurrently. 

