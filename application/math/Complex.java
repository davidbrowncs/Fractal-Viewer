
package math;

public class Complex implements Cloneable {

	private double real;
	private double imaginary;

	public Complex(double r, double i) {
		real = r;
		imaginary = i;
	}

	public Complex() {}

	public void setReal(double r) {
		real = r;
	}

	public Complex add(Complex c) {
		double r = real + c.real;
		double i = imaginary + c.imaginary;
		return new Complex(r, i);
	}

	public Complex multiply(Complex c) {
		double nReal = real * c.real - imaginary * c.imaginary;
		double nImaginary = real * c.imaginary + imaginary * c.real;
		return new Complex(nReal, nImaginary);
	}

	public Complex subtract(Complex c) {
		double i = imaginary - c.imaginary;
		double r = real - c.real;
		return new Complex(r, i);
	}

	public Complex divide(Complex c) {
		Complex conj = new Complex(c.getReal(), -c.getImaginary());
		Complex top = this.multiply(conj);
		Complex bottom = this.multiply(conj);
		return new Complex(top.getReal() / bottom.getReal(), top.getImaginary() / bottom.getReal());
	}

	// Modifies this, rather than creating a new complex number object, seems to
	// be faster
	public Complex fastPow(int n) {
		double oldReal = real;
		double oldImaginary = imaginary;
		for (int i = 1; i < n; i++) {
			double tmpReal = real;
			double tmpImaginary = imaginary;
			real = tmpReal * oldReal - tmpImaginary * oldImaginary;
			imaginary = tmpReal * oldImaginary + tmpImaginary * oldReal;
		}
		return this;
	}

	public Complex clone() {
		return new Complex(real, imaginary);
	}

	public double modulusSquared() {
		return real * real + imaginary * imaginary;
	}

	public double getImaginary() {
		return imaginary;
	}

	public void setImaginary(double imaginary) {
		this.imaginary = imaginary;
	}

	public double getReal() {
		return real;
	}
	
	public String toString() {
		return this.real + " + " + this.imaginary + "i";
	}

}
