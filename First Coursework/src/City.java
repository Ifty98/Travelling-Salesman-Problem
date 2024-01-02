import java.lang.Math;

/*the following class creates an object to store the data of a city,
for example: the city number and its position in Euclidean distance*/
public class City {

	private int cityNumber;
	private int xPosition;
	private int yPosition;

	// creates an city object taking a city number and its coordinates
	public City(int cityNumber, int xPosition, int yPosition) {
		this.cityNumber = cityNumber;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}

	// calculate the distance between two cities using Euclidean distance formula
	public double calculateDistance(City goalCity) {
		int xDifference = this.xPosition - goalCity.getXposition();
		int yDifference = this.yPosition - goalCity.getYposition();
		return Math.sqrt(xDifference * xDifference + yDifference * yDifference);
	}

	// getters and setters for city Number, x coordinate and y coordinate
	public void setCityNumber(int cityNumber) {
		this.cityNumber = cityNumber;
	}

	public int getCityNumber() {
		return cityNumber;
	}

	public void setXposition(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getXposition() {
		return xPosition;
	}

	public void setYposition(int yPosition) {
		this.yPosition = yPosition;
	}

	public int getYposition() {
		return yPosition;
	}

}
