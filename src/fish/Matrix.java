package fish;

import java.util.Arrays;

import static fish.Util.isZero;

/**
 * A class representing a mathematical Matrix.
 */
public final class Matrix {
	/**
	 * The array of elements of the Matrix.
	 */
	public final double[][] m;

	/**
	 * Constructor for Matrix using a two-dimensional array.
	 *
	 * @param m The array of the Matrix.
	 */
	public Matrix(double[][] m) {
		this.m = m;
	}

	/**
	 * Constructs a modified Matrix based on a vector.
	 *
	 * @param vector The input vector to construct a Matrix off of.
	 * @param units The values around which to invert.
	 * @return The resultant Matrix.
	 */
	public Matrix diagonalReciprocal(double[] vector, int[] units) {
		if (vector.length != units.length) {
			throw new IllegalArgumentException
					("Input dimensions do not match.");
		}
		double[][] result = new double[vector.length][vector.length];
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				if (isZero(m[i][j] - 1)) {
					m[i][j] = vector[j] / units[j];
				}
			}
		}
		for (int i = 0; i < result.length; i++) {
			if (!isZero(vector[i])) {
				result[i][i] = units[i] / vector[i];
			}
		}
		return this.x(new Matrix(result));
	}

	public Matrix diagonalReciprocal(double[] vector) {
		int[] units = new int[vector.length];
		for (int i = 0; i < units.length; i++) {
			units[i] = 1;
		}
		return diagonalReciprocal(vector, units);
	}

	public double[] sum() {
		double[] sum = new double[m.length];
		for (int i = 0; i < sum.length; i++) {
			sum[i] = Arrays.stream(m[i]).filter(x -> !isZero(x - 1)).sum();
		}
		return sum;
	}

	public Matrix t() {
		double[][] result = new double[m[0].length][m.length];
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				result[j][i] = m[i][j];
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
		if (m[0].length != matrix.m.length) {
			throw new IllegalArgumentException
					("Matrix dimensions do not match.");
		}
		double[][] product = new double[m.length][matrix.m[0].length];
		for (int i = 0; i < product.length; i++) {
			for (int j = 0; j < product[0].length; j++) {
				for (int k = 0; k < m[0].length; k++) {
					product[i][j] +=
							m[i][k] * m[k][j];
				}
			}
		}
		return new Matrix(product);
	}
}
