/*vertex class to represent nodes in branch and bound algorithm. Each node stores the visited cities,
the cities remaining to visit and the cost of the vertex*/
public class Vertex {
	private CityList visitedCities;
	private CityList remainingCities;
	private City actualCity;
	private double[][] reducedMatrix;
	private double[][] originalMatrix;
	private double lastCost;
	private double cost;

	/*
	 * constructor to create a vertex using a city, a list of visited cities, a list
	 * of remaining cities the reduced matrix from the last vertex, the original
	 * distance matrix and the cost of the last vertex
	 */
	public Vertex(City actualCity, CityList visitedCities, CityList remainingCities, double[][] reducedMatrix,
			double[][] originalMatrix, double lastCost) {
		this.actualCity = actualCity;
		this.visitedCities = visitedCities;
		this.remainingCities = remainingCities;
		this.reducedMatrix = reducedMatrix;
		this.originalMatrix = originalMatrix;
		this.lastCost = lastCost;
		this.cost = calculateBound();
	}

	// vertex constructor without arguments
	public Vertex() {

	}

	// method to create a copy of a vertex
	public void createCopy(Vertex vertex) {
		this.actualCity = vertex.getActualCity();
		this.visitedCities = new CityList(vertex.getVisitedCities());
		this.remainingCities = new CityList(vertex.getRemainingCities());
		this.reducedMatrix = copyMatrix(vertex.getReducedMatrix());
		this.originalMatrix = copyMatrix(vertex.getOriginalMatrix());
		this.lastCost = vertex.getLastCost();
		this.cost = vertex.getCost();
	}

	// method to create a copy of a matrix
	private double[][] copyMatrix(double[][] matrix) {
		int rows = matrix.length;
		int cols = matrix[0].length;
		double[][] copy = new double[rows][cols];
		for (int index = 0; index < rows; index++) {
			System.arraycopy(matrix[index], 0, copy[index], 0, cols);
		}
		return copy;
	}

	// calculate the cost of the vertex
	private double calculateBound() {
		double reducedCost = 0.0;
		double citiesDistance = 0.0;
		double cost;
		// if the list of visited cities is not empty
		if (!visitedCities.isEmpty()) {
			City lastCity = visitedCities.getElement(visitedCities.getSize() - 1);
			for (int row = 0; row < reducedMatrix.length; row++) {
				for (int col = 0; col < reducedMatrix[row].length; col++) {
					// convert the actual city row and last visited city column to infinity
					if (row == lastCity.getCityNumber() - 1 || col == actualCity.getCityNumber() - 1) {
						// get the distance between the actual city and the last visited city from the
						// original distance matrix
						if (row == lastCity.getCityNumber() - 1 && col == actualCity.getCityNumber() - 1) {
							citiesDistance += originalMatrix[row][col];
						}
						reducedMatrix[row][col] = Double.POSITIVE_INFINITY;
					}
					// convert the actual city column number and last visited city row number to
					// infinity
					if (row == actualCity.getCityNumber() - 1 && col == lastCity.getCityNumber() - 1) {
						reducedMatrix[row][col] = Double.POSITIVE_INFINITY;
					}
				}
			}
		}
		// while the matrix is not reduced, keep reducing it
		while (!isReduced()) {
			// each time the matrix is reduced store the reduction cost
			reducedCost += reduceMatrix();
		}
		// the final cost is the sum of the actual city to last visited city distance +
		// reduced cost + the cost of the last vertex
		cost = citiesDistance + reducedCost + lastCost;
		// add the actual city to the visited cities
		visitedCities.add(actualCity);
		// and remove the actual city from the remaining cities
		remainingCities.removeByCityNumber(actualCity.getCityNumber());
		return cost;
	}

	// method to reduce the rows in the matrix
	private double reduceRows() {
		double sumMinInRow = 0.0;
		// reduce rows
		for (int row = 0; row < reducedMatrix.length; row++) {
			double minInRow = Double.POSITIVE_INFINITY;
			for (int col = 0; col < reducedMatrix[row].length; col++) {
				// get the minimum value from the selected row
				if (reducedMatrix[row][col] != Double.POSITIVE_INFINITY) {
					minInRow = Math.min(minInRow, reducedMatrix[row][col]);
				}
			}
			for (int col = 0; col < reducedMatrix[row].length; col++) {
				// reduce the values in the selected row using the minimum value
				if (reducedMatrix[row][col] != Double.POSITIVE_INFINITY) {
					reducedMatrix[row][col] -= minInRow;
				}
			}
			if (minInRow == Double.POSITIVE_INFINITY) {
				minInRow = 0.0;
			}
			// store the minimum value found in the row
			sumMinInRow += minInRow;
		}
		return sumMinInRow;
	}

	// method to reduce the columns in the matrix
	private double reduceColumns() {
		double sumMinInCol = 0.0;
		// reduce columns
		for (int col = 0; col < reducedMatrix[0].length; col++) {
			double minInCol = Double.POSITIVE_INFINITY;
			for (int row = 0; row < reducedMatrix.length; row++) {
				// get the minimum value from the selected column
				if (reducedMatrix[row][col] != Double.POSITIVE_INFINITY) {
					minInCol = Math.min(minInCol, reducedMatrix[row][col]);
				}
			}
			for (int row = 0; row < reducedMatrix.length; row++) {
				// reduce the values in the selected column using the minimum value
				if (reducedMatrix[row][col] != Double.POSITIVE_INFINITY) {
					reducedMatrix[row][col] -= minInCol;
				}
			}
			if (minInCol == Double.POSITIVE_INFINITY) {
				minInCol = 0.0;
			}
			// store the minimum value found in the column
			sumMinInCol += minInCol;
		}
		return sumMinInCol;
	}

	// method to reduce the matrix
	private double reduceMatrix() {
		double sumMinInRow = reduceRows();
		double sumMinInCol = reduceColumns();

		// store the cost of the reduction done to the matrix
		double cost = sumMinInRow + sumMinInCol;
		return cost;
	}

	// method to check if the rows in the matrix are reduced
	public boolean rowsAreReduced() {
		boolean reduced = true;
		// check each row if it has zero or if all the rows has infinity values only
		for (int row = 0; row < reducedMatrix.length; row++) {
			boolean rowHasZero = false;
			boolean rowAllInfinity = true;

			// check each column in the row
			for (int col = 0; col < reducedMatrix[row].length; col++) {
				if (reducedMatrix[row][col] == 0) {
					rowHasZero = true;
				}

				if (reducedMatrix[row][col] != Double.POSITIVE_INFINITY) {
					rowAllInfinity = false;
				}
			}
			// if the row has not zero and all the row is not infinity, then return the
			// matrix is not reduced
			if (!rowHasZero && !rowAllInfinity) {
				reduced = false;
				break;
			}
		}
		return reduced;
	}

	// method to check if the columns in the matrix are reduced
	public boolean columnsAreReduced() {
		boolean reduced = true;
		for (int col = 0; col < reducedMatrix[0].length; col++) {
			boolean colHasZero = false;
			boolean colAllInfinity = true;
			// check each row in the column
			for (int row = 0; row < reducedMatrix.length; row++) {
				if (reducedMatrix[row][col] == 0) {
					colHasZero = true;
				}

				if (reducedMatrix[row][col] != Double.POSITIVE_INFINITY) {
					colAllInfinity = false;
				}
			}
			// if the column has not zero and all the column is not infinity, then return
			// the matrix is not reduced
			if (!colHasZero && !colAllInfinity) {
				reduced = false;
				break;
			}
		}
		return reduced;
	}

	// method to check if the matrix is reduced or not
	public boolean isReduced() {
		boolean reduced = rowsAreReduced();
		// if the rows are reduced then check the columns
		if (reduced) {
			reduced = columnsAreReduced();
		}
		return reduced;
	}

	// method to check if the remaining cities list is empty
	public boolean isComplete() {
		return remainingCities.isEmpty();
	}

	// getters for visitedCities, remainingCities, actualCity, reducedMatrix,
	// originalMatrix, cost and lastCost
	public CityList getVisitedCities() {
		return visitedCities;
	}

	public CityList getRemainingCities() {
		return remainingCities;
	}

	public City getActualCity() {
		return actualCity;
	}

	public double[][] getReducedMatrix() {
		return reducedMatrix;
	}

	public double[][] getOriginalMatrix() {
		return originalMatrix;
	}

	public double getCost() {
		return cost;
	}

	public double getLastCost() {
		return lastCost;
	}

}
