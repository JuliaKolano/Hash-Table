package ci583.htable;

// normally there wouldn't be so many comments, but I wanted to show my
// understanding of all the methods for the assessment, so there are
// explanations to the right of most of the lines of code

import java.util.*;
public class Hashtable<V> {
	private static final int DOUBLE_HASH_MAX = 8; //used in the doubleHash method.
	private Object[] arr; //an array of Pair objects, where each pair contains the key and value stored in the hashtable.
	private int max; //the size of arr. This should be a prime number.
	private int itemCount; //the number of items stored in arr.
	private final double maxLoad = 0.6; //the maximum load factor.
	public enum PROBE_TYPE {
		LINEAR_PROBE, QUADRATIC_PROBE, DOUBLE_HASH
	}
	private final PROBE_TYPE probeType; //the type of probe to use when dealing with collisions

	public Hashtable(int initialCapacity, PROBE_TYPE pt) {
		this.max = nextPrime(initialCapacity);  //setting the length of the array to a prime number
		this.probeType = pt;                    //setting the probe type of the hashtable
		this.arr = new Object[max];
	}
	public Hashtable(int initialCapacity) {          //Doing the same thing as the previous constructor
		this.max = nextPrime(initialCapacity);       //Except when probe type is not specified it sets
		this.probeType = PROBE_TYPE.LINEAR_PROBE;    //it by default to the linear probe
		this.arr = new Object[max];
	}
	public void put(String key, V value) {
		if (key == null) {                              // if a key is not provided throw an error
			throw new IllegalArgumentException();
		}
		if (getLoadFactor() >= maxLoad) {               // if the array is i more than 60% full,
			resize();                                   // increase the size of the array
		}
		int hashedKey = hash(key);                      // use the hash function to get a numerical value of the key
		Pair pair = new Pair(key, value);               // create a key value pair
		if (!hasKey(key)) {                             // check if the key was stored already
			itemCount++;
		}
		int index = findEmptyOrSameKey(hashedKey, key, 1); //get an unoccupied position
		arr[index] = pair;                                         //store the pair at that location
	}
	public Optional<V> get(String key) {
		if (!key.isEmpty()) {                            // check if the key has been provided
			return (find(hash(key), key, 1));   // get the value that is linked to the key
		} else {
			return Optional.empty();                    // otherwise receive an empty Optional object
		}
	}
	public boolean hasKey(String key) {
		if (get(key).isPresent()) {                   // check if the key is associated with a value
			return true;
		} else {
			return false;
		}
	}
	public Collection<String> getKeys() {
		ArrayList<String> collection = new ArrayList<>();       // create an array list for the keys
		for (Object object : arr) {                             // loop through all the pair objects in the hashtable
			Pair pairObject = (Pair) object;                    // create a reference to a single pair object
			if (object != null) {                               // if the object exists in the hashtable
				collection.add(pairObject.key);                 // add its key to the array list
			}
		}
		return collection;
	}
	public double getLoadFactor() {
		double doubleMax = max;                  // Changing it to a double so that the return value is a double
		return itemCount / doubleMax;            // Ratio of number of items stored to the size of the array
	}
	public int getCapacity() {        
		return max;        }          // the capacity of the hashtable that has to be prime
	private Optional<V> find(int startPos, String key, int stepNum) {
		Pair pairObject = (Pair) arr[startPos]; //create a reference to a pair stored in the hashtable at this position
		if (pairObject == null) {               //check if the pair doesn't exist at this position
			return Optional.empty();            //receive an empty optional object
		} else if (pairObject.key.equals(key)) { //check if the key of the object is the same as the key provided
			return Optional.of(pairObject.value); //return the Optional of a value associated with that key
		} else {                                  //otherwise find a different location that the method can start the
			int nextPos = getNextLocation(startPos, key, ++stepNum); //search using a bigger stepNum value
			return find(nextPos, key, stepNum);       //then call this method recursively using the new position
		}
	}
	private int findEmptyOrSameKey(int startPos, String key, int stepNum) {
		Pair pairObject = (Pair) arr[startPos]; //create a reference to a pair stored in the hashtable at this position
		if (pairObject == null) {         //check if the pair doesn't exist meaning the space at this positions is empty
			return startPos;              //in this case receive this position in the hashtable
		} else if (pairObject.key.equals(key)) {  //check if the key of the object is the same as the key provided
			return startPos;                      //in this case also receive this position
		} else {        //otherwise the space in this hashtable is occupied by an object with a different key
			int nextPos = getNextLocation(startPos, key, ++stepNum); //so find a new location using a bigger stepNum value
			return findEmptyOrSameKey(nextPos, key, stepNum);   //and call the method recursively using the new location
		}
	}
	private int getNextLocation(int startPos, String key, int stepNum) {
		int step = startPos;
		switch (probeType) {
		case LINEAR_PROBE:
			step++;
			break;
		case DOUBLE_HASH:
			step += doubleHash(key);
			break;
		case QUADRATIC_PROBE:
			step += stepNum * stepNum;
			break;
		default:
			break;
		}
		return step % max;
	}
	private int doubleHash(String key) {
		//a different hash function to be utilised when the double hash probe is being used
		return (hash(key) % DOUBLE_HASH_MAX) + 1;
		//returns a smaller number than the other hash function by returning
		//its remainder and adding 1 to it.
	}
	private int hash(String key) {
		//first hash method I used obtained from https://studentcentral.brighton.ac.uk/ultra/courses/_121230_1/cl/outline
//		int hashVal = key.charAt(0);              //gets the first character from the string provided
//		for (int i = 0; i < key.length(); i++) {  //loops through all the characters is the string
//			int c = key.charAt(i);                //gets the next character in the string
//			hashVal = (hashVal * 27 + c) % max;   //generates a large number based on the ASCII values of the characters
//		}                                         //and then returns the remainder of dividing the sum by
//		return hashVal;                           //the size of the array to reduce collisions

		//second hash method I used from https://algs4.cs.princeton.edu/34hash/
		return (key.hashCode() & 0x7fffffff) % max;  // '& 0x7fffffff' part gets rid of the negative numbers that could be
	}                                                //generated otherwise, and the '% max' part reduces collisions
	private boolean isPrime(int n) {
		//first isPrime method I used from https://studentcentral.brighton.ac.uk/ultra/courses/_121230_1/cl/outline
//		if (n <= 2) {                   //any positive number less than or equal to two will be prime,
//			return true;                //if we assume that 1 is prime as well
//		}
//		if (n % 2 == 0) {               //if a number is even, it's divisible by two, so it's not prime
//			return false;
//		}
//		for (int i = 3; i * i < n; i += 2) {  //i = 3 because n <= 2 was already checked and i * i because
//			if (n % i == 0) {                 //the square root of n will be in the middle of its prime factors
//				return false;                 //then if n is divisible by any number i, it cannot be prime,
//			}
//		}
//		return true;                          //otherwise it must be prime
		//second isPrime method I used from https://www.programiz.com/java-programming/examples/prime-number
		for (int i = 2; i <= n / 2; ++i) {    //looping for only half the number because numbers bigger than a half of
			if (n % i == 0) {                 //that number do not divide into it evenly.
				return false;                 //if any of the other numbers divide into it evenly, then it can't be prime
			}
		}
		return true;                          //otherwise it must be prime
	}
	private int nextPrime(int n) {
		if (!isPrime(n)) {
			while (!isPrime(n)) {        //if n is not prime add 1 to n until it turns prime
				n = n + 1;               //using iteration
			}
		}
		return n;                        //return n if it's prime or when it becomes prime
	}
	private void resize() {             
		max = nextPrime(2 * max);      //find a prime number that is at least double of the original hashtable size
		itemCount = 0;                    //reset the amount of the items stored in the original hashtable
		Object[] arrayCopy = arr;         //create a copy of the original hashtable
		arr = new Object[max];            //create a new hashtable with a bigger capacity
		for (int i = 0; i < arrayCopy.length; i++) { //
			if (arrayCopy[i] != null) {
				Pair object = (Pair) arrayCopy[i];
				put(object.key, object.value);
			}
		}
	}
	private class Pair {
		private final String key;
		private final V value;

		Pair(String key, V value) {
			this.key = key;
			this.value = value;
		}
	}

}