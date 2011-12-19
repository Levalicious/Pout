package org.getspout.api.math;

/**
 * Represents a 3d vector.
 */
public class Vector3 {
	public static Vector3 ZERO = new Vector3(0, 0, 0);
	public static Vector3 UNIT_X = new Vector3(1, 0, 0);
	public static Vector3 UNIT_Y = new Vector3(0, 1, 0);
	public static Vector3 UNIT_Z = new Vector3(0, 0, 1);
	public static Vector3 ONE = new Vector3(1, 1, 1);

	protected double x, y, z;

	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3() {
		this(0, 0, 0);
	}

	public Vector3(Vector2 vector, double z) {
		this(vector.getX(), vector.getY(), z);
	}

	public Vector3(Vector2 vector) {
		this(vector, 0);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}


	/**
	 * Adds two vectors
	 * @param that
	 * @return
	 */
	public Vector3 add(Vector3 that) {
		return Vector3.add(this, that);
	}

	/**
	 * Subtracts two vectors
	 * @param that
	 * @return
	 */
	public Vector3 subtract(Vector3 that) {
		return Vector3.subtract(this, that);
	}

	/**
	 * Scales by the scalar value
	 * @param scale
	 * @return
	 */
	public Vector3 scale(double scale) {
		return Vector3.scale(this, scale);
	}

	/**
	 * Takes the dot product of two vectors
	 * @param that
	 * @return
	 */
	public double dot(Vector3 that) {
		return Vector3.dot(this, that);
	}

	/**
	 * Takes the cross product of two vectors
	 * @param that
	 * @return
	 */
	public Vector3 cross(Vector3 that) {
		return Vector3.cross(this, that);
	}

	/**
	 * returns the squared length of the vector
	 * @return
	 */
	public double lengthSquared() {
		return Vector3.lengthSquared(this);
	}

	/**
	 * returns the length of this vector.  Note: makes use of Math.sqrt and is not cached.
	 * @return
	 */
	public double length() {
		return Vector3.length(this);
	}

	/**
	 * returns the vector with a length of 1
	 * @return
	 */
	public Vector3 normalize() {
		return Vector3.normalize(this);
	}

	/**
	 * returns the vector as [x,y,z]
	 * @return
	 */
	public double[] toArray() {
		return Vector3.toArray(this);
	}

	public Vector3 transform(Matrix transformation){
		return Vector3.transform(this, transformation);
	}

	public static double length(Vector3 a) {
		return MathHelper.sqrt(lengthSquared(a));
	}

	public static double lengthSquared(Vector3 a) {
		return Vector3.dot(a, a);
	}

	public static Vector3 normalize(Vector3 a) {
		return Vector3.scale(a, (1.f / a.length()));
	}

	public static Vector3 subtract(Vector3 a, Vector3 b) {
		return new Vector3(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
	}

	public static Vector3 add(Vector3 a, Vector3 b) {
		return new Vector3(a.getX() + b.getX(), a.getY() + b.getY(), a.getZ() + b.getZ());
	}

	public static Vector3 scale(Vector3 a, double b) {
		return new Vector3(a.getX() * b, a.getY() * b, a.getZ() * b);
	}

	public static double dot(Vector3 a, Vector3 b) {
		return (a.getX() * b.getX() + a.getY() * b.getY() + a.getZ() + b.getZ());
	}

	public static Vector3 cross(Vector3 a, Vector3 b) {
		return new Vector3(a.getY() * b.getZ() - a.getZ() * b.getY(), a.getZ() * b.getX() - a.getX() * b.getZ(), a.getX() * b.getY() - a.getY() * b.getX());
	}

	public static double[] toArray(Vector3 a) {
		return new double[]{a.getX(), a.getY(), a.getZ()};
	}
	/**
	 * Calculates and returns a new Vector3 transformed by the transformation matrix
	 * @param vector the vector to transform
	 * @param transformation the transformation matrix
	 * @return
	 */
	public static Vector3 transform(Vector3 vector, Matrix transformation){
		Matrix v3 = Matrix.createIdentity();
		v3.set(0, 0, vector.getX());
		v3.set(0, 1, vector.getY());
		v3.set(0, 2, vector.getZ());
		
		Matrix t = Matrix.multiply(transformation, v3);
		return new Vector3(t.get(0,0), t.get(0, 1), t.get(0,2));
	}
}
