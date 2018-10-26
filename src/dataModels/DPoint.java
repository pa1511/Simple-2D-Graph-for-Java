package dataModels;


import java.util.Arrays;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Class encapsulating a continuous point of a desired number of dimensions.
 * </br>
 * It offers the functionality for storing and restoring coordinate values at wanted dimensions.
 * </br>
 * @author Petar
 *
 */
public class DPoint {

	
	private double[] coordinates;
	
	/**
	 * Constructor
	 * @param dimension-number of wanted dimensions
	 */
	public DPoint(@Nonnegative int dimension) {
		coordinates = new double[dimension];
	}
	
	public DPoint(double... coordinates) {
		this.coordinates = coordinates;
	}

	/**
	 * Sets the coordinates to the given values. 
	 * </br>
	 * If a null or a field containing a invalid number of coordinates is given a {@link IllegalArgumentException} is thrown.
	 * @param coordinates-new coordinate values
	 */
	public void setCoordinates(@Nonnull double[] coordinates) {
		
		if(this.coordinates.length!=coordinates.length)
			throw new IllegalArgumentException("Invalid coordinates given. The length of the given field is invalid or the field is null.");
		
		this.coordinates = coordinates;
	}
	
	/**
	 * Gets the current coordinates. The field returned is a copy of the field containing the coordinates.
	 * @return - copy of the coordinates
	 */
	public double[] getCoordinates() {
		return Arrays.copyOf(coordinates, coordinates.length);
	}

	/**
	 * Return the internal storage coordinate field. 
	 */
	public double[] getActualCoordinates() {
		return coordinates;
	}

	/**
	 * Returns the value of a coordinate at a specific dimension. If a invalid dimension is given a {@link IllegalArgumentException} is thrown.
	 * @param dimension - dimension of a coordinate
	 * @return value of the coordinate at the given dimension
	 */
	public double getCoordinateAtDimension(@Nonnegative int dimension) {
		checkDimension(dimension);
		return coordinates[dimension];
	}

	/**
	 * Sets the value of the coordinate at the given dimension.  If a invalid dimension is given a {@link IllegalArgumentException} is thrown.
	 * @param dimension - dimension of the coordinate
	 * @param value to which the coordinate at the given dimension will be set
	 */
	public void setCoordinateAtDimension(@Nonnegative int dimension, double value) {
		checkDimension(dimension);
		coordinates[dimension] = value;
	}
	
	/**
	 * Returns the number of dimensions of the {@link DPoint}
	 * @return <code>int</code> - number of dimensions
	 */
	public int dimension() {
		return coordinates.length;
	}
	
	
	@Override
	public String toString() {
		return Arrays.toString(coordinates);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(coordinates);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DPoint other = (DPoint) obj;
		if (!Arrays.equals(coordinates, other.coordinates))
			return false;
		return true;
	}

	private void checkDimension(int dimension) {
		if(dimension<0 || dimension>=coordinates.length)
			throw new IllegalArgumentException("Invalid dimension given. Given dimension: " + dimension);
	}
	
}
