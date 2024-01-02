/*this class allows to create an array list of vertices. It can adjust the size
 of the array while adding new elements, reduce the size while removing an element
 and get an element from the array using the index*/
public class VertexList {
	// array list of vertices
	private static final int INITIAL_CAPACITY = 10;
	private Vertex[] array;
	private int size;

	// create an empty array list with an initial capacity of 10 elements
	public VertexList() {
		array = new Vertex[INITIAL_CAPACITY];
		size = 0;
	}

	// copy an array list of vertices with all its attributes
	public VertexList(VertexList vertices) {
		int capacity = vertices.getSize();
		array = new Vertex[capacity];
		size = 0;

		for (int index = 0; index < vertices.getSize(); index++) {
			add(vertices.getElement(index));
		}
	}

	// add new vertices to the array list giving a vertex object
	public void add(Vertex vertex) {
		if (size == array.length) {
			// Resize the array if it's full
			int newCapacity = array.length * 2;
			Vertex[] newArray = new Vertex[newCapacity];
			System.arraycopy(array, 0, newArray, 0, size);
			array = newArray;
		}

		array[size] = vertex;
		size++;
	}

	// remove a Vertex object from the array list by index
	public void removeVertex(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index not found!!");
		}

		// shift the vertices in the array to fill the gap left by the removed vertex
		for (int newIndex = index; newIndex < size - 1; newIndex++) {
			array[newIndex] = array[newIndex + 1];
		}

		array[size - 1] = null; // set the last element to null

		size--;
	}

	// get a vertex from the array list using the vertex index
	public Vertex getElement(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index not found!!");
		}
		return array[index];
	}

	// returns the actual size of the array
	public int getSize() {
		return size;
	}
}
