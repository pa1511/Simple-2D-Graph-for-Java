package dataModels;


import javax.annotation.Nonnull;

/**
 * The pair class encapsulates two objects. The logical link between the two
 * elements is not defined by the class itself but is to be known by the user of
 * the class.
 * 
 * @author Petar
 *
 * @param <LeftElement>
 * @param <RightElement>
 */
public final class Pair<LeftElement, RightElement> {

	private @Nonnull LeftElement left;
	private @Nonnull RightElement right;


	/**
	 * Constructor
	 * @param elL - left element
	 * @param elR - right element
	 */
	public Pair(@Nonnull LeftElement elL,@Nonnull RightElement elR) {
		this.left = elL;
		this.right = elR;
	}

	/**
	 * Gets the left element
	 */
	public @Nonnull LeftElement left() {
		return left;
	}

	/**
	 * Sets the left element
	 */
	public void setLeft(@Nonnull LeftElement left) {
		this.left = left;
	}

	/**
	 * Gets the right element
	 */
	public @Nonnull RightElement right() {
		return right;
	}

	/**
	 * Sets the right element
	 */
	public void setRight(@Nonnull RightElement right) {
		this.right = right;
	}
	
	public @Nonnull Pair<LeftElement,RightElement> copy(){
		return new Pair<LeftElement,RightElement>(this.left,this.right);
	}
	
	public static <T1,T2>  Pair<T1, T2> of(@Nonnull T1 left, @Nonnull T2 right){
		return new Pair<T1, T2>(left, right);
	}
	
	@Override
	public String toString() {
		return "["+left.toString()+","+right.toString()+"]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result +  left.hashCode();
		result = prime * result +  right.hashCode();
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
		@SuppressWarnings("unchecked")
		Pair<LeftElement, RightElement> other = (Pair<LeftElement, RightElement>) obj;
		if (!left.equals(other.left))
			return false;
		 if (!right.equals(other.right))
			return false;
		return true;
	}

}
