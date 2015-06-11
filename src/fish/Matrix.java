package fish;

import static fish.Util.isZero;

/**
 * A class representing a mathematical Matrix.
 */
public final class Matrix {
	/**
	 * The array of elements of the Matrix.
	 */
	public final double[][] _;

	/**
	 * Constructor for Matrix using a two-dimensional array.
	 *
	 * @param _ The array of the Matrix.
	 */
	public Matrix(double[][] _) {
		this._ = _;
	}

	/**
	 * Constructs a modified Matrix based on a vector.
	 *
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
			if (!isZero(vector[i])) {
				result[i][i] = units[i] / vector[i];
			}
		}
		return new Matrix(result);
	}

	public static Matrix diagonalReciprocal(double[] vector) {
		int[] units = new int[vector.length];
		for (int i = 0; i < units.length; i++) {
			units[i] = 1;
		}
		return diagonalReciprocal(vector, units);
	}

	public double[] sum() {
		double[][] sum = new double[_.length][1];
		for (int i = 0; i < sum.length; i++) {
			sum[i][0] = 1;
		}
		Matrix result = this.x(new Matrix(sum));
		double[] vector = new double[result._.length];
		for (int i = 0; i < result._.length; i++) {
			vector[i] = result._[i][0];
		}
		return vector;
	}

	public Matrix t() {
		double[][] result = new double[_[0].length][_.length];
		for (int i = 0; i < _.length; i++) {
			for (int j = 0; j < _[0].length; j++) {
				result[j][i] = _[i][j];
			}
		}
		return new Matrix(result);
	}

	/**
	 * Multiplies this Matrix by another.
	 *
	 * @param matrix The multiplicand.
	 * @return The product.
	 */
	public Matrix x(Matrix matrix) {
		if (_[0].length != matrix._.length) {
			throw new IllegalArgumentException
					("Matrix dimensions do not match.");
		}
		double[][] product = new double[_.length][matrix._[0].length];
		for (int i = 0; i < product.length; i++) {
			for (int j = 0; j < product[0].length; j++) {
				for (int k = 0; k < _[0].length; k++) {
					product[i][j] +=
							_[i][k] * _[k][j];
				}
			}
		}
		return new Matrix(product);
	}
}
