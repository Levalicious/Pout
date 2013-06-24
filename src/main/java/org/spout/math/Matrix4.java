package org.spout.math;

import java.io.Serializable;

public class Matrix4 implements Serializable, Cloneable {
	private static final long serialVersionUID = 1;
	public static final Matrix4 IDENTITY = new ImmutableIdentityMatrix4();
	private float m00, m01, m02, m03;
	private float m10, m11, m12, m13;
	private float m20, m21, m22, m23;
	private float m30, m31, m32, m33;

	public Matrix4() {
		setIdentity();
	}

	public Matrix4(Matrix2 m) {
		this(
				m.get(0, 0), m.get(0, 1), 0, 0,
				m.get(1, 0), m.get(1, 1), 0, 0,
				0, 0, 0, 0,
				0, 0, 0, 0);
	}

	public Matrix4(Matrix3 m) {
		this(
				m.get(0, 0), m.get(0, 1), m.get(0, 2), 0,
				m.get(1, 0), m.get(1, 1), m.get(1, 2), 0,
				m.get(2, 0), m.get(2, 1), m.get(2, 2), 0,
				0, 0, 0, 0);
	}

	public Matrix4(Matrix4 m) {
		this(
				m.m00, m.m01, m.m02, m.m03,
				m.m10, m.m11, m.m12, m.m13,
				m.m20, m.m21, m.m22, m.m23,
				m.m30, m.m31, m.m32, m.m33);
	}

	private Matrix4(
			float m00, float m01, float m02, float m03,
			float m10, float m11, float m12, float m13,
			float m20, float m21, float m22, float m23,
			float m30, float m31, float m32, float m33) {
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m30 = m30;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
	}

	public float get(int row, int col) {
		switch (row) {
			case 0:
				switch (col) {
					case 0:
						return m00;
					case 1:
						return m01;
					case 2:
						return m02;
					case 3:
						return m03;
				}
			case 1:
				switch (col) {
					case 0:
						return m10;
					case 1:
						return m11;
					case 2:
						return m12;
					case 3:
						return m13;
				}
			case 2:
				switch (col) {
					case 0:
						return m20;
					case 1:
						return m21;
					case 2:
						return m22;
					case 3:
						return m23;
				}
			case 3:
				switch (col) {
					case 0:
						return m30;
					case 1:
						return m31;
					case 2:
						return m32;
					case 3:
						return m33;
				}
		}
		throw new IllegalArgumentException(
				(row < 0 || row > 2 ? "row must be greater than zero and smaller than 3. " : "") +
						(col < 0 || col > 2 ? "col must be greater than zero and smaller than 3." : ""));
	}

	public void set(int row, int col, double val) {
		set(row, col, (float) val);
	}

	public void set(int row, int col, float val) {
		switch (row) {
			case 0:
				switch (col) {
					case 0:
						m00 = val;
						return;
					case 1:
						m01 = val;
						return;
					case 2:
						m02 = val;
						return;
					case 3:
						m03 = val;
						return;
				}
			case 1:
				switch (col) {
					case 0:
						m10 = val;
						return;
					case 1:
						m11 = val;
						return;
					case 2:
						m12 = val;
						return;
					case 3:
						m13 = val;
						return;
				}
			case 2:
				switch (col) {
					case 0:
						m20 = val;
						return;
					case 1:
						m21 = val;
						return;
					case 2:
						m22 = val;
						return;
					case 3:
						m23 = val;
						return;
				}
			case 3:
				switch (col) {
					case 0:
						m30 = val;
						return;
					case 1:
						m31 = val;
						return;
					case 2:
						m32 = val;
						return;
					case 3:
						m33 = val;
						return;
				}
		}
		throw new IllegalArgumentException(
				(row < 0 || row > 2 ? "row must be greater than zero and smaller than 3. " : "") +
						(col < 0 || col > 2 ? "col must be greater than zero and smaller than 3." : ""));
	}

	public final void setIdentity() {
		m00 = 1;
		m01 = 0;
		m02 = 0;
		m03 = 0;
		m10 = 0;
		m11 = 1;
		m12 = 0;
		m13 = 0;
		m20 = 0;
		m21 = 0;
		m22 = 1;
		m23 = 0;
		m30 = 0;
		m31 = 0;
		m32 = 0;
		m33 = 1;
	}

	public void setZero() {
		m00 = 0;
		m01 = 0;
		m02 = 0;
		m03 = 0;
		m10 = 0;
		m11 = 0;
		m12 = 0;
		m13 = 0;
		m20 = 0;
		m21 = 0;
		m22 = 0;
		m23 = 0;
		m30 = 0;
		m31 = 0;
		m32 = 0;
		m33 = 0;
	}

	public Matrix4 add(Matrix4 m) {
		return new Matrix4(
				m00 + m.m00, m01 + m.m01, m02 + m.m02, m03 + m.m03,
				m10 + m.m10, m11 + m.m11, m12 + m.m12, m13 + m.m13,
				m20 + m.m20, m21 + m.m21, m22 + m.m22, m23 + m.m23,
				m30 + m.m30, m31 + m.m31, m32 + m.m32, m33 + m.m33);
	}

	public Matrix4 sub(Matrix4 m) {
		return new Matrix4(
				m00 - m.m00, m01 - m.m01, m02 - m.m02, m03 - m.m03,
				m10 - m.m10, m11 - m.m11, m12 - m.m12, m13 - m.m13,
				m20 - m.m20, m21 - m.m21, m22 - m.m22, m23 - m.m23,
				m30 - m.m30, m31 - m.m31, m32 - m.m32, m33 - m.m33);
	}

	public Matrix4 mul(float a) {
		return new Matrix4(
				m00 * a, m01 * a, m02 * a, m03 * a,
				m10 * a, m11 * a, m12 * a, m13 * a,
				m20 * a, m21 * a, m22 * a, m23 * a,
				m30 * a, m31 * a, m32 * a, m33 * a);
	}

	public Matrix4 mul(Matrix4 m) {
		return new Matrix4(
				m00 * m.m00 + m01 * m.m10 + m02 * m.m20 + m03 * m.m30,
				m00 * m.m01 + m01 * m.m11 + m02 * m.m21 + m03 * m.m31,
				m00 * m.m02 + m01 * m.m12 + m02 * m.m22 + m03 * m.m32,
				m00 * m.m03 + m01 * m.m13 + m02 * m.m23 + m03 * m.m33,
				m10 * m.m00 + m11 * m.m10 + m12 * m.m20 + m13 * m.m30,
				m10 * m.m01 + m11 * m.m11 + m12 * m.m21 + m13 * m.m31,
				m10 * m.m02 + m11 * m.m12 + m12 * m.m22 + m13 * m.m32,
				m10 * m.m03 + m11 * m.m13 + m12 * m.m23 + m13 * m.m33,
				m20 * m.m00 + m21 * m.m10 + m22 * m.m20 + m23 * m.m30,
				m20 * m.m01 + m21 * m.m11 + m22 * m.m21 + m23 * m.m31,
				m20 * m.m02 + m21 * m.m12 + m22 * m.m22 + m23 * m.m32,
				m20 * m.m03 + m21 * m.m13 + m22 * m.m23 + m23 * m.m33,
				m30 * m.m00 + m31 * m.m10 + m32 * m.m20 + m33 * m.m30,
				m30 * m.m01 + m31 * m.m11 + m32 * m.m21 + m33 * m.m31,
				m30 * m.m02 + m31 * m.m12 + m32 * m.m22 + m33 * m.m32,
				m30 * m.m03 + m31 * m.m13 + m32 * m.m23 + m33 * m.m33);
	}

	public Matrix4 div(float a) {
		return new Matrix4(
				m00 / a, m01 / a, m02 / a, m03 / a,
				m10 / a, m11 / a, m12 / a, m13 / a,
				m20 / a, m21 / a, m22 / a, m23 / a,
				m30 / a, m31 / a, m32 / a, m33 / a);
	}

	public Matrix4 translate(Vector3 v) {
		return translate(v.getX(), v.getY(), v.getZ());
	}

	public Matrix4 translate(float x, float y, float z) {
		return createTranslation(x, y, z).mul(this);
	}

	public Matrix4 scale(double scale) {
		return scale((float) scale);
	}

	public Matrix4 scale(float scale) {
		return scale(scale, scale, scale, scale);
	}

	public Matrix4 scale(Vector4 v) {
		return scale(v.getX(), v.getY(), v.getZ(), v.getW());
	}

	public Matrix4 scale(float x, float y, float z, float w) {
		return createScaling(x, y, z, w).mul(this);
	}

	public Matrix4 rotate(Complex rot) {
		return createRotation(rot).mul(this);
	}

	public Matrix4 rotate(Quaternion rot) {
		return createRotation(rot).mul(this);
	}

	public Vector4 transform(Vector4 v) {
		return transform(v.getX(), v.getY(), v.getZ(), v.getW());
	}

	public Vector4 transform(float x, float y, float z, float w) {
		return new Vector4(
				m00 * x + m01 * y + m02 * z + m03 * w,
				m10 * x + m11 * y + m12 * z + m13 * w,
				m20 * x + m21 * y + m22 * z + m23 * w,
				m30 * x + m31 * y + m32 * z + m33 * w);
	}

	public Matrix4 floor() {
		return new Matrix4(
				GenericMath.floor(m00), GenericMath.floor(m01), GenericMath.floor(m02), GenericMath.floor(m03),
				GenericMath.floor(m10), GenericMath.floor(m11), GenericMath.floor(m12), GenericMath.floor(m13),
				GenericMath.floor(m20), GenericMath.floor(m21), GenericMath.floor(m22), GenericMath.floor(m23),
				GenericMath.floor(m30), GenericMath.floor(m31), GenericMath.floor(m32), GenericMath.floor(m33));
	}

	public Matrix4 ceil() {
		return new Matrix4(
				(float) Math.ceil(m00), (float) Math.ceil(m01), (float) Math.ceil(m02), (float) Math.ceil(m03),
				(float) Math.ceil(m10), (float) Math.ceil(m11), (float) Math.ceil(m12), (float) Math.ceil(m13),
				(float) Math.ceil(m20), (float) Math.ceil(m21), (float) Math.ceil(m22), (float) Math.ceil(m23),
				(float) Math.ceil(m30), (float) Math.ceil(m31), (float) Math.ceil(m32), (float) Math.ceil(m33));
	}

	public Matrix4 round() {
		return new Matrix4(
				Math.round(m00), Math.round(m01), Math.round(m02), Math.round(m03),
				Math.round(m10), Math.round(m11), Math.round(m12), Math.round(m13),
				Math.round(m20), Math.round(m21), Math.round(m22), Math.round(m23),
				Math.round(m30), Math.round(m31), Math.round(m32), Math.round(m33));
	}

	public Matrix4 abs() {
		return new Matrix4(
				Math.abs(m00), Math.abs(m01), Math.abs(m02), Math.abs(m03),
				Math.abs(m10), Math.abs(m11), Math.abs(m12), Math.abs(m13),
				Math.abs(m20), Math.abs(m21), Math.abs(m22), Math.abs(m23),
				Math.abs(m30), Math.abs(m31), Math.abs(m32), Math.abs(m33));
	}

	public Matrix4 negate() {
		return new Matrix4(
				-m00, -m01, -m02, -m03,
				-m10, -m11, -m12, -m13,
				-m20, -m21, -m22, -m23,
				-m30, -m31, -m32, -m33);
	}

	public Matrix4 transpose() {
		return new Matrix4(
				m00, m10, m20, m30,
				m01, m11, m21, m31,
				m02, m12, m22, m32,
				m03, m13, m23, m33);
	}

	public float trace() {
		return m00 + m11 + m22 + m33;
	}

	public float determinant() {
		return m00 * (m11 * m22 * m33 + m21 * m32 * m13 + m31 * m12 * m23 - m31 * m22 * m13 - m11 * m32 * m23 - m21 * m12 * m33)
				- m10 * (m01 * m22 * m33 + m21 * m32 * m03 + m31 * m02 * m23 - m31 * m22 * m03 - m01 * m32 * m23 - m21 * m02 * m33)
				+ m20 * (m01 * m12 * m33 + m11 * m32 * m03 + m31 * m02 * m13 - m31 * m12 * m03 - m01 * m32 * m13 - m11 * m02 * m33)
				- m30 * (m01 * m12 * m23 + m11 * m22 * m03 + m21 * m02 * m13 - m21 * m12 * m03 - m01 * m22 * m13 - m11 * m02 * m23);
	}

	public Matrix4 invert() {
		final float det = determinant();
		if (det == 0) {
			return null;
		}
		return new Matrix4(
				det3(m11, m21, m31, m12, m22, m32, m13, m23, m33) / det, -det3(m10, m20, m30, m12, m22, m32, m13, m23, m33) / det,
				det3(m10, m20, m30, m11, m21, m31, m13, m23, m33) / det, -det3(m10, m20, m30, m11, m21, m31, m12, m22, m32) / det,
				-det3(m01, m21, m31, m02, m22, m32, m03, m23, m33) / det, det3(m00, m20, m30, m02, m22, m32, m03, m23, m33) / det,
				-det3(m00, m20, m30, m01, m21, m31, m03, m23, m33) / det, det3(m00, m20, m30, m01, m21, m31, m02, m22, m32) / det,
				det3(m01, m11, m31, m02, m12, m32, m03, m13, m33) / det, -det3(m00, m10, m30, m02, m12, m32, m03, m13, m33) / det,
				det3(m00, m10, m30, m01, m11, m31, m03, m13, m33) / det, -det3(m00, m10, m30, m01, m11, m31, m02, m12, m32) / det,
				-det3(m01, m11, m21, m02, m12, m22, m03, m13, m23) / det, det3(m00, m10, m20, m02, m12, m22, m03, m13, m23) / det,
				-det3(m00, m10, m30, m01, m11, m31, m02, m12, m32) / det, det3(m00, m10, m20, m01, m11, m21, m02, m12, m22) / det
		);
	}

	public float[] toArray() {
		return new float[]{
				m00, m01, m02, m03,
				m10, m11, m12, m13,
				m20, m21, m22, m23,
				m30, m31, m32, m33
		};
	}

	@Override
	public String toString() {
		return m00 + " " + m01 + " " + m02 + " " + m03 + "\n"
				+ m10 + " " + m11 + " " + m12 + " " + m13 + "\n"
				+ m20 + " " + m21 + " " + m22 + " " + m23 + "\n"
				+ m30 + " " + m31 + " " + m32 + " " + m33 + "\n";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Matrix4)) {
			return false;
		}
		final Matrix4 matrix4 = (Matrix4) o;
		if (Float.compare(matrix4.m00, m00) != 0) {
			return false;
		}
		if (Float.compare(matrix4.m01, m01) != 0) {
			return false;
		}
		if (Float.compare(matrix4.m02, m02) != 0) {
			return false;
		}
		if (Float.compare(matrix4.m03, m03) != 0) {
			return false;
		}
		if (Float.compare(matrix4.m10, m10) != 0) {
			return false;
		}
		if (Float.compare(matrix4.m11, m11) != 0) {
			return false;
		}
		if (Float.compare(matrix4.m12, m12) != 0) {
			return false;
		}
		if (Float.compare(matrix4.m13, m13) != 0) {
			return false;
		}
		if (Float.compare(matrix4.m20, m20) != 0) {
			return false;
		}
		if (Float.compare(matrix4.m21, m21) != 0) {
			return false;
		}
		if (Float.compare(matrix4.m22, m22) != 0) {
			return false;
		}
		if (Float.compare(matrix4.m23, m23) != 0) {
			return false;
		}
		if (Float.compare(matrix4.m30, m30) != 0) {
			return false;
		}
		if (Float.compare(matrix4.m31, m31) != 0) {
			return false;
		}
		if (Float.compare(matrix4.m32, m32) != 0) {
			return false;
		}
		if (Float.compare(matrix4.m33, m33) != 0) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = (m00 != +0.0f ? Float.floatToIntBits(m00) : 0);
		result = 31 * result + (m01 != +0.0f ? Float.floatToIntBits(m01) : 0);
		result = 31 * result + (m02 != +0.0f ? Float.floatToIntBits(m02) : 0);
		result = 31 * result + (m03 != +0.0f ? Float.floatToIntBits(m03) : 0);
		result = 31 * result + (m10 != +0.0f ? Float.floatToIntBits(m10) : 0);
		result = 31 * result + (m11 != +0.0f ? Float.floatToIntBits(m11) : 0);
		result = 31 * result + (m12 != +0.0f ? Float.floatToIntBits(m12) : 0);
		result = 31 * result + (m13 != +0.0f ? Float.floatToIntBits(m13) : 0);
		result = 31 * result + (m20 != +0.0f ? Float.floatToIntBits(m20) : 0);
		result = 31 * result + (m21 != +0.0f ? Float.floatToIntBits(m21) : 0);
		result = 31 * result + (m22 != +0.0f ? Float.floatToIntBits(m22) : 0);
		result = 31 * result + (m23 != +0.0f ? Float.floatToIntBits(m23) : 0);
		result = 31 * result + (m30 != +0.0f ? Float.floatToIntBits(m30) : 0);
		result = 31 * result + (m31 != +0.0f ? Float.floatToIntBits(m31) : 0);
		result = 31 * result + (m32 != +0.0f ? Float.floatToIntBits(m32) : 0);
		result = 31 * result + (m33 != +0.0f ? Float.floatToIntBits(m33) : 0);
		return result;
	}

	@Override
	public Matrix4 clone() {
		return new Matrix4(this);
	}

	public static Matrix4 createScaling(double scale) {
		return createScaling((float) scale);
	}

	public static Matrix4 createScaling(float scale) {
		return createScaling(scale, scale, scale, scale);
	}

	public static Matrix4 createScaling(Vector4 v) {
		return createScaling(v.getX(), v.getY(), v.getZ(), v.getW());
	}

	public static Matrix4 createScaling(float x, float y, float z, float w) {
		return new Matrix4(
				x, 0, 0, 0,
				0, y, 0, 0,
				0, 0, z, 0,
				0, 0, 0, w);
	}

	public static Matrix4 createTranslation(Vector3 v) {
		return createTranslation(v.getX(), v.getY(), v.getZ());
	}

	public static Matrix4 createTranslation(float x, float y, float z) {
		return new Matrix4(
				1, 0, 0, x,
				0, 1, 0, y,
				0, 0, 1, z,
				0, 0, 0, 1);
	}

	public static Matrix4 createRotation(Complex rot) {
		rot = rot.normalize();
		return new Matrix4(
				rot.getX(), -rot.getY(), 0, 0,
				rot.getY(), rot.getX(), 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	public static Matrix4 createRotation(Quaternion rot) {
		rot = rot.normalize();
		return new Matrix4(
				1 - 2 * rot.getY() * rot.getY() - 2 * rot.getZ() * rot.getZ(),
				2 * rot.getX() * rot.getY() - 2 * rot.getW() * rot.getZ(),
				2 * rot.getX() * rot.getZ() + 2 * rot.getW() * rot.getY(), 0,
				2 * rot.getX() * rot.getY() + 2 * rot.getW() * rot.getZ(),
				1 - 2 * rot.getX() * rot.getX() - 2 * rot.getZ() * rot.getZ(),
				2 * rot.getY() * rot.getZ() - 2 * rot.getW() * rot.getX(), 0,
				2 * rot.getX() * rot.getZ() - 2 * rot.getW() * rot.getY(),
				2 * rot.getY() * rot.getZ() + 2 * rot.getX() * rot.getW(),
				1 - 2 * rot.getX() * rot.getX() - 2 * rot.getY() * rot.getY(), 0,
				0, 0, 0, 1);
	}

	private static class ImmutableIdentityMatrix4 extends Matrix4 {
		@Override
		public void set(int row, int col, float val) {
			throw new UnsupportedOperationException("You may not alter this matrix");
		}

		@Override
		public void setZero() {
			throw new UnsupportedOperationException("You may not alter this matrix");
		}
	}

	private static float det3(float m00, float m01, float m02,
							  float m10, float m11, float m12,
							  float m20, float m21, float m22) {
		return m00 * (m11 * m22 - m12 * m21) - m01 * (m10 * m22 - m12 * m20) + m02 * (m10 * m21 - m11 * m20);
	}
}
