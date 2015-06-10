package fish;

/**
 * A class representing a mathematical Matrix.
 */
public final class Matrix {
	/**
	 * The array of elements of the Matrix.
	 */
	public final double[][] matrix;

	/**
	 * Constructor for Matrix using a two-dimensional array.
	 * @param matrix The array of the Matrix.
	 */
	public Matrix(double[][] matrix) {
		this.matrix = matrix;
	}

	/**
	 * Constructs a modified Matrix based on a vector.
	 * @param vector The input vector to custruct a Matrix off of.
	 * @param units The values around which to invert.
	 * @return The resultant Matrix.
	 */
	public static Matrix diagonalReciprocal(double[] vector, int[] units) {
		if (vector.length != units.length) {
			throw new IllegalArgumentException
					("Input dimensions do not match.");
		}
		double[][] result = new double[vector.length][vector.length];
		for (int i = 0; i < result.length; i++) {
			result[i][i] = units[i] / vector[i];
		}
		return new Matrix(result);
	}

	/**
	 * Multiplies two Matrix's together.
	 * @param matrix1 The first multiplicand.
	 * @param matrix2 The second multiplicand.
	 * @return The product of the Matrix's.
	 */
	public static Matrix x(Matrix matrix1, Matrix matrix2) {
		if (matrix1.matrix[0].length != matrix2.matrix.length) {
			throw new IllegalArgumentException
					("Matrix dimensions do not match.");
		}
		double[][] product =
				new double[matrix1.matrix.length][matrix2.matrix[0].length];
		for (int i = 0; i < product.length; i++) {
			for (int j = 0; j < product[0].length; j++) {
				for (int k = 0; k < matrix1.matrix[0].length; k++) {
					product[i][j] +=
							matrix1.matrix[i][k] * matrix1.matrix[k][j];
				}
			}
		}
		return new Matrix(product);
	}
}
