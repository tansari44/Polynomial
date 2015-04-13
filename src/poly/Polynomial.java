package poly;

import java.io.*;
import java.util.StringTokenizer;

/**
 * This class implements a term of a polynomial.
 * 
 * @author runb-cs112
 *
 */
class Term {
	/**
	 * Coefficient of term.
	 */
	public float coeff;
	
	/**
	 * Degree of term.
	 */
	public int degree;
	
	/**
	 * Initializes an instance with given coefficient and degree.
	 * 
	 * @param coeff Coefficient
	 * @param degree Degree
	 */
	public Term(float coeff, int degree) {
		this.coeff = coeff;
		this.degree = degree;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		return other != null &&
		other instanceof Term &&
		coeff == ((Term)other).coeff &&
		degree == ((Term)other).degree;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (degree == 0) {
			return coeff + "";
		} else if (degree == 1) {
			return coeff + "x";
		} else {
			return coeff + "x^" + degree;
		}
	}
}

/**
 * This class implements a linked list node that contains a Term instance.
 * 
 * @author runb-cs112
 *
 */
class Node {
	
	/**
	 * Term instance. 
	 */
	Term term;
	
	/**
	 * Next node in linked list. 
	 */
	Node next;
	
	/**
	 * Initializes this node with a term with given coefficient and degree,
	 * pointing to the given next node.
	 * 
	 * @param coeff Coefficient of term
	 * @param degree Degree of term
	 * @param next Next node
	 */
	public Node(float coeff, int degree, Node next) {
		term = new Term(coeff, degree);
		this.next = next;
	}
}

/**
 * This class implements a polynomial.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Pointer to the front of the linked list that stores the polynomial. 
	 */ 
	Node poly;
	
	/** 
	 * Initializes this polynomial to empty, i.e. there are no terms.
	 *
	 */
	public Polynomial() {
		poly = null;
	}
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param br BufferedReader from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 */
	public Polynomial(BufferedReader br) throws IOException {
		String line;
		StringTokenizer tokenizer;
		float coeff;
		int degree;
		
		poly = null;
		
		while ((line = br.readLine()) != null) {
			tokenizer = new StringTokenizer(line);
			coeff = Float.parseFloat(tokenizer.nextToken());
			degree = Integer.parseInt(tokenizer.nextToken());
			poly = new Node(coeff, degree, poly);
		}
	}
	
	
	/**
	 * Returns the polynomial obtained by adding the given polynomial p
	 * to this polynomial - DOES NOT change this polynomial
	 * 
	 * @param p Polynomial to be added
	 * @return A new polynomial which is the sum of this polynomial and p.
	 */
	public Polynomial add(Polynomial p) {
		Node sum = null;
		Node tempCurr = p.poly;
		Node curr = this.poly;
		while(curr!=null){
			sum = numAdd(curr, sum);
			curr = curr.next;
		}
		while(tempCurr != null){
			sum = numAdd(tempCurr,sum);
			tempCurr = tempCurr.next;
		}
		Polynomial tempSum = new Polynomial();
		tempSum.poly = sum;
		return tempSum;	
	}
	
	/**
	 * Returns the polynomial obtained by multiplying the given polynomial p
	 * with this polynomial - DOES NOT change this polynomial
	 * 
	 * @param p Polynomial with which this polynomial is to be multiplied
	 * @return A new polynomial which is the product of this polynomial and p.
	 */
	public Polynomial multiply(Polynomial p) {
		Node term1 = this.poly;
		Node term2 = p.poly;
		Node temp = null;
		Node total = null;
		Polynomial product = new Polynomial();

		while(term1 != null){
			while(term2 != null){
				temp = new Node(term1.term.coeff * term2.term.coeff,
						 term1.term.degree + term2.term.degree, null);
				term2 = term2.next;
				total = numAdd(temp, total);
			}
			term1 = term1.next;
			term2 = p.poly;
		}
		product.poly = total;
		return product;
	}
	
	/**
	 * Evaluates this polynomial at the given value of x
	 * 
	 * @param x Value at which this polynomial is to be evaluated
	 * @return Value of this polynomial at x
	 */
	public float evaluate(float x) {
		Node curr = poly;
		float eval = 0;
		while (curr != null){
			eval+=curr.term.coeff*((float)Math.pow(x, curr.term.degree));
			curr = curr.next;
		}
		return eval;
	}
	private Node numAdd(Node num, Node group){
		Node temp = new Node(num.term.coeff, num.term.degree, null);

		if(group == null){
			return temp;
		}
		else{			
		Node curr = group;
		Node prev = null;

		while(curr != null)
			if(temp.term.degree > curr.term.degree){					
				if(curr.next == null){
					curr.next=temp;
					return group;
				}
				prev = curr;
				curr = curr.next;
			}
			else if(temp.term.degree == curr.term.degree){
				if(curr.term.coeff + temp.term.coeff==0){
					if(prev == null)
						return curr.next;
					else{
						prev.next = curr.next;
						return group;}
				}
				if(prev == null)
					group = new Node(curr.term.coeff + temp.term.coeff, curr.term.degree, curr.next);
				else{
					prev.next= new Node(curr.term.coeff + temp.term.coeff, curr.term.degree, curr.next);
				}
				return group;				
			}
			else if(temp.term.degree < curr.term.degree){				
				temp.next = curr;
				prev.next = temp;
				return group;
			}	
		}		
		return null;	
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String retval;
		
		if (poly == null) {
			return "0";
		} else {
			retval = poly.term.toString();
			for (Node current = poly.next ;
			current != null ;
			current = current.next) {
				retval = current.term.toString() + " + " + retval;
			}
			return retval;
		}
	}
}
