import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*system to solve symmetric TSP problem using branch and bound algorithm.
The system creates a matrix with the distances between the different cities,
creating a vertex with the first city as the initial city and from this generates
new vertices as the possible routes to find the shortest path*/
public class Main {
	/*
	 * method to extract the city number, x coordinate and y coordinate from the
	 * file and store them inside an array list
	 */
	public static CityList readFile(String fileName) {
		CityList cities = new CityList();
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				// separate the city data divided by spaces or tabs
				String[] cityData = line.split("[\\s\t]+");
				int cityDataParts = 3;
				if (cityData.length >= cityDataParts) {
					int cityNumber = Integer.parseInt(cityData[0].trim());
					int xPosition = Integer.parseInt(cityData[1].trim());
					int yPosition = Integer.parseInt(cityData[2].trim());
					// create a city object and store data for each city
					City city = new City(cityNumber, xPosition, yPosition);
					cities.add(city);
				}
			}
			bufferedReader.close();
		} catch (IOException readingFileError) {
			// catch any error if there is a problem reading the file
			readingFileError.printStackTrace();
		}
		return cities;
	}

	// method to create a matrix with the distance between all the cities
	// and setting the distance between the same city as infinity
	public static double[][] generateDistanceMatrix(CityList cities) {
		int numCities = cities.getSize();
		// dimensional array to store cities distances
		double[][] distanceMatrix = new double[numCities][numCities];
		for (int row = 0; row < numCities; row++) {
			for (int col = 0; col < numCities; col++) {
				if (row == col) {
					// set the distance between the same cities as infinity
					distanceMatrix[row][col] = Double.POSITIVE_INFINITY;
				} else {
					// calculate the distance between city i and city j
					double distance = cities.getElement(row).calculateDistance(cities.getElement(col));
					distanceMatrix[row][col] = distance;
				}
			}
		}
		return distanceMatrix;
	}

	// method to create new vertices from a given vertex
	public static Vertex[] generateVertecies(Vertex vertex) {
		CityList remainingCities = vertex.getRemainingCities();
		// for each city in the remainingCities create a new vertex
		Vertex vertexList[] = new Vertex[remainingCities.getSize()];
		for (int index = 0; index < remainingCities.getSize(); index++) {
			Vertex vertexCopy = new Vertex();
			// create a copy for the original vertex to avoid referencing the data in the
			// new vertices
			vertexCopy.createCopy(vertex);
			Vertex newVertex = new Vertex(vertexCopy.getRemainingCities().getElement(index),
					vertexCopy.getVisitedCities(), vertexCopy.getRemainingCities(), vertexCopy.getReducedMatrix(),
					vertexCopy.getOriginalMatrix(), vertexCopy.getCost());
			vertexList[index] = newVertex;
		}
		return vertexList;
	}

	/*
	 * method that takes the best possible routes found by the branch and bound
	 * algorithm, and returns the optimal route between these, checking the routes
	 * length
	 */
	public static Vertex getBestPath(VertexList allBestRoutes, Vertex initialVertex) {
		double min = Double.POSITIVE_INFINITY;
		int index = 0;
		// from the best routes, get the route with minimum cost
		for (int arrayIndex = 0; arrayIndex < allBestRoutes.getSize(); arrayIndex++) {
			CityList visitedCities = new CityList(allBestRoutes.getElement(arrayIndex).getVisitedCities());
			visitedCities.add(initialVertex.getActualCity());
			double pathCost = visitedCities.calculatePathLength();
			min = Math.min(min, pathCost);
		}
		for (int arrayIndex = 0; arrayIndex < allBestRoutes.getSize(); arrayIndex++) {
			CityList visitedCities = new CityList(allBestRoutes.getElement(arrayIndex).getVisitedCities());
			visitedCities.add(initialVertex.getActualCity());
			double pathCost = visitedCities.calculatePathLength();
			if (min == pathCost) {
				index = arrayIndex;
			}
		}
		// add to the end of the best route the initial city
		allBestRoutes.getElement(index).getVisitedCities().add(initialVertex.getActualCity());
		return allBestRoutes.getElement(index);
	}

	// method to run branch and bound algorithm taking a list of cities
	// the algorithm creates an initial vertex and from this generates new vertices
	// for all possible routes
	/*
	 * every time that new vertices are generated, it checks for the vertex with
	 * lower cost and checks if the selected vertex has complete path or not
	 */
	/*
	 * the algorithm keeps generating new vertices until it finds a vertex with a
	 * complete path and at the end finds the optimal route between all the possible
	 * candidates to be the shortest route
	 */
	public static CityList branchAndBound(CityList cities) {
		City initialCity = cities.getElement(0);
		CityList visitedCities = new CityList();
		CityList remainingCities = new CityList(cities);
		// call method to create a distance matrix for the list of cities
		double[][] reducedMatrix = generateDistanceMatrix(cities);
		// create the initial point of the route
		Vertex initialVertex = new Vertex(initialCity, visitedCities, remainingCities, reducedMatrix, reducedMatrix,
				0.0);
		Vertex currentVertex = initialVertex;
		boolean smallerVertexFound = false;
		VertexList allBestRoutes = new VertexList();
		// explore the new vertices while there are vertices with smaller cost then the
		// current vertex
		do {
			// list of vertices to store all the generated vertices
			VertexList allVertecies = new VertexList();
			// keep exploring from the same vertex while the current vertex has not a
			// complete path
			do {
				// generate vertices from the current vertex
				Vertex nextVertecies[] = generateVertecies(currentVertex);
				double min = Double.POSITIVE_INFINITY;
				// get the vertex with the minimum cost
				for (int arrayIndex = 0; arrayIndex < nextVertecies.length; arrayIndex++) {
					min = Math.min(min, nextVertecies[arrayIndex].getCost());
				}
				int index = 0;
				for (int arrayIndex = 0; arrayIndex < nextVertecies.length; arrayIndex++) {
					if (min == nextVertecies[arrayIndex].getCost()) {
						index = arrayIndex;
					} else {
						// store the rest of vertices
						allVertecies.add(nextVertecies[arrayIndex]);
					}
				}
				// convert the vertex with minimum cost to current vertex
				currentVertex = nextVertecies[index];
			} while (!currentVertex.isComplete());
			// store vertices with the minimum cost
			allBestRoutes.add(currentVertex);
			double minCost = Double.POSITIVE_INFINITY;
			// check in the list of vertices if there is a vertex with less cost then the
			// current vertex
			for (int index = 0; index < allVertecies.getSize(); index++) {
				minCost = Math.min(minCost, allVertecies.getElement(index).getCost());
			}
			int costIndex = 0;
			for (int index = 0; index < allVertecies.getSize(); index++) {
				if (minCost == allVertecies.getElement(index).getCost()) {
					costIndex = index;
				}
			}
			// if found, convert the vertex with minimum cost to the new current vertex
			if (minCost < currentVertex.getCost()) {
				smallerVertexFound = true;
				currentVertex = allVertecies.getElement(costIndex);
			} else {
				smallerVertexFound = false;
			}
		} while (smallerVertexFound);
		// call method to get the best path between the best routes found so far
		Vertex bestPath = getBestPath(allBestRoutes, initialVertex);
		return bestPath.getVisitedCities();
	}

	/*
	 * system main method that, reads a file with the cities for TSP problem and and
	 * passes the list of cities to the branch and bound algorithm to solve it
	 */
	// also this method records the time to check how much time the system takes to
	// find the best route
	public static void main(String[] args) {
		// record the start time
		long startTime = System.nanoTime();
		// enter here the file name with the list of cities
		final String fileName = "test1-23.txt";
		CityList cities = readFile(fileName); 
		CityList shortestPath = branchAndBound(cities);
		// record the end time
		long endTime = System.nanoTime();
		// calculate the time in milliseconds
		long elapsedTimeInMilliseconds = (endTime - startTime) / 1000000;
		System.out.print("Best Route: ");
		// print the best route found
		for (int index = 0; index < shortestPath.getSize(); index++) {
			System.out.print(shortestPath.getElement(index).getCityNumber());
			if (index < shortestPath.getSize() - 1) {
				System.out.print(" => ");
			}
		}
		System.out.println();
		// print the cost of the route and the time for the system to get the best route
		System.out.println("Cost: " + shortestPath.calculatePathLength());
		System.out.println("Total Time: " + elapsedTimeInMilliseconds + " ms");
	}
}
