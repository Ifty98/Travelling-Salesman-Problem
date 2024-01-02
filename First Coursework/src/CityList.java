/*this class allows to store cities inside an array list, where the size of the array is modified
when new elements are added and removed. Also, it provides access to any element of the array by
index and city number*/
public class CityList {
	// array list of cities
	private static final int INITIAL_CAPACITY = 10;
	private City[] array;
	private int size;

	// create an empty array list with an initial capacity of 10 elements
	public CityList() {
		array = new City[INITIAL_CAPACITY];
		size = 0;
	}

	// copy an array list of cities with all its attributes
	public CityList(CityList cities) {
		int capacity = cities.getSize();
		array = new City[capacity];
		size = 0;
		for (int index = 0; index < cities.getSize(); index++) {
			add(cities.getElement(index));
		}
	}

	// add new cities to the array list giving a city object
	public void add(City element) {
		if (size == array.length) {
			// resize the array if it's full
			int newCapacity = array.length * 2;
			City[] newArray = new City[newCapacity];
			System.arraycopy(array, 0, newArray, 0, size);
			array = newArray;
		}

		array[size] = element;
		size++;
	}

	// remove a City object from the array list by index
	public void removeCity(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index not found!!");
		}
		// shift the cities in the array to fill the space left by the removed city
		for (int newIndex = index; newIndex < size - 1; newIndex++) {
			array[newIndex] = array[newIndex + 1];
		}
		array[size - 1] = null; // set the last element to null
		size--;
	}

	// remove a city from the array list using the city number
	public void removeByCityNumber(int cityNumber) {
		int index = -1;
		// search for the city with the given cityNumber
		for (int arrayIndex = 0; arrayIndex < size; arrayIndex++) {
			if (array[arrayIndex].getCityNumber() == cityNumber) {
				index = arrayIndex;
				break;
			}
		}
		// if the city with the specified cityNumber is found, remove it
		if (index != -1) {
			for (int arrayIndex = index; arrayIndex < size - 1; arrayIndex++) {
				array[arrayIndex] = array[arrayIndex + 1];
			}
			array[size - 1] = null; // set the last element to null
			size--;
		} else {
			throw new IllegalArgumentException("City with cityNumber " + cityNumber + " not found!");
		}
	}

	// get a city from the array list using the city index
	public City getElement(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index not found!!");
		}
		return array[index];
	}

	// returns the actual size of the array
	public int getSize() {
		return size;
	}

	// returns true if the array has not elements
	public boolean isEmpty() {
		return size == 0;
	}

	// calculates the total cost of the path of the selected array list of cities
	public double calculatePathLength() {
		double length = 0.0;
		for (int index = 0; index < size; index++) {
			City currentCity = array[index];
			// wrap around to the first city
			City nextCity = array[(index + 1) % size];
			// calculate the distance between the current city and the next city
			length += currentCity.calculateDistance(nextCity);
		}
		return length;
	}
}
