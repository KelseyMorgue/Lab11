/**
 * Implementation of DictionaryInterface using Linear Probing
 *
 * CMPT 202, Westminster College
 * 
 * @author Kelsey Henrichsen, Alex G.
 */

public class HashDictionaryLinear<K, V> implements DictionaryInterface<K, V> {

	// initial size of hash table
	private static int DEFAULT_SIZE = 13;

	// When capacity exceeds this threshold, a new addition will trigger
	// rehashing
	private static double CAPACITY_THRESHOLD = 0.67;

	// the number of elements in the hash table
	private int numberOfElements;

	// the hash table
	private TableElement<K, V>[] dictionary;

	// the current capacity of the hash table
	// this is a prime number
	private int currentCapacity;
	
	

	/**
	 * Inner class representing an element in the hash table This consists of a
	 * [Key:Value] mapping
	 *
	 * @param <K>
	 *            Key
	 * @param <V>
	 *            Value
	 */
	@SuppressWarnings("hiding")
	private class TableElement<K, V> {
		private K key;
		private V value;
		private boolean toBeRemoved = false; // boolean to "flag" removed spots

		private TableElement(K key, V value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * Two TableElement objects are equals if they both have the same key
		 */
		@SuppressWarnings("unchecked")
		public boolean equals(Object other) {
			boolean flag = false;

			if (other instanceof TableElement) {
				TableElement<K, V> candidate = (TableElement<K, V>) other;

				if ((this.getKey()).equals(candidate.getKey()))
					flag = true;
			}

			return flag;
		}

		// gets the key
		private K getKey() {
			return key;
		}

		// gets the value
		private V getValue() {
			return value;
		}

		// sets the key
		private void setKey(K key) {
			this.key = key;
		}

		// sets the value
		private void setValue(V value) {
			this.value = value;
		}
		
		//gets the to be removed boolean
		private boolean getToBeRemoved()
		{
			return this.toBeRemoved;
		}
		
		//sets the to be removed boolean
		private void setToBeRemoved(boolean toBeRemoved)
		{
			this.toBeRemoved = toBeRemoved;
		}
	}

	public HashDictionaryLinear() {
		this(DEFAULT_SIZE);
	}

	@SuppressWarnings("unchecked")
	public HashDictionaryLinear(int size) {
		if (size < 0)
			throw new IllegalArgumentException("x");

		dictionary = (TableElement<K, V>[]) new TableElement[size];
		numberOfElements = 0;
		currentCapacity = size;
	}

	/**
	 * Returns the hash value in the range [0 .. currentCapacity-1]
	 * 
	 * @param key
	 * @return int
	 */
	private int hashValue(K key) {
		return (Math.abs(key.hashCode()) % currentCapacity);
	}

	/**
	 * This calls the appropriate hashing strategy
	 */
	public V put(K key, V value) {

		return linearProbing(key, value);
	}

	/**
	 * Private helper method that implements appropriate hashing strategy
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	private V linearProbing(K key, V value) {

		// re-hash if necessary
		ensureCapacity();

		if(contains(key) == true)
		{
			int updatedHash = hashValue(key);
			if(dictionary[updatedHash] != null && dictionary[updatedHash].getKey().equals(key) && !dictionary[updatedHash].getToBeRemoved())
			{
				dictionary[updatedHash].setValue(value);
			}
			else
			{
				while(dictionary[updatedHash] == null || !dictionary[updatedHash].getKey().equals(key))
				{
					
					updatedHash = (updatedHash + 1) % dictionary.length;
				}
				if(dictionary[updatedHash].getToBeRemoved() != true)
				{
					dictionary[updatedHash].setValue(value);
				}

			}
		}
		else
		{
			// create the new element
			TableElement<K, V> element = new TableElement<K, V>(key, value);

			// get the hash value for the specified key
			int hash = hashValue(key);

			if (dictionary[hash] == null) 
			{
				dictionary[hash] = element;
			}
			else
			{
				boolean hasInserted = false;

				//while()

				for (int j = hash + 1; j < dictionary.length && !hasInserted; j++) {
					if (dictionary[j] == null ) {
						dictionary[j] = element;
						hasInserted = true;
					}
				}
				// possible wrap around
				for (int k = 0; k < hash && !hasInserted; k++) {
					if (dictionary[k] == null ) {
						dictionary[k] = element;
						hasInserted = true;
					}
				}
			}
			numberOfElements++;
		}
		currentCapacity = dictionary.length - numberOfElements;

		return value;

	}

	// returns the specified key's value in the array
	public V get(K key) {
		if (contains(key) == true) {
			for (int i = 0; i < dictionary.length; i++) {
				if (dictionary[i] != null && dictionary[i].getKey() == key) {
					return dictionary[i].getValue();
				}
			}
		}

		return null;
	}

	// checks if the key is within the dictionary
	public boolean contains(K key) {
		// get the hash value for the specified key
		int hash = hashValue(key);

		for(int i = hash; i < dictionary.length; i++) {
			if (dictionary[i] != null && dictionary[i].getKey().equals(key)) {
				if(dictionary[i].getToBeRemoved() != true)
				{
				return true;
				}
			} 
		}
		for(int i = 0; i < hash; i++)
		{
			if (dictionary[i] != null && dictionary[i].getKey().equals(key)) {
				if(dictionary[i].getToBeRemoved() != true)
				{
				return true;
				}
			} 
		}

		return false;
	}

	// removes a given key
	public V remove(K key) {
		V temp;
		
		if(contains(key) == true)
		{
			for(int i = 0; i<dictionary.length;i++)
			{
				if(dictionary[i] != null)
				{
					if (dictionary[i].getKey() == key)
					{
						dictionary[i].setToBeRemoved( true);
						temp = dictionary[i].getValue();
						numberOfElements--;
						currentCapacity = dictionary.length - numberOfElements;
						return temp;
					}
				}
			}
		}

		return null;
	}

	// gets the size of the array
	public int size() {
		return numberOfElements;
	}

	/**
	 * returns the next prime number that is least 2 larger than the current
	 * prime number.
	 */
	private int getNextPrime(int currentPrime) {
		// first we double the size of the current prime + 1
		currentPrime *= 2;
		currentPrime += 1;

		while (!isPrime(currentPrime))
			currentPrime++;

		return currentPrime;
	}

	/**
	 * Helper method that tests if an integer value is prime.
	 * 
	 * @param candidate
	 * @return True if candidate is prime, false otherwise.
	 */
	private boolean isPrime(int candidate) {
		boolean isPrime = true;

		// numbers <= 1 or even are not prime
		if ((candidate <= 1))
			isPrime = false;
		// 2 or 3 are prime
		else if ((candidate == 2) || (candidate == 3))
			isPrime = true;
		// even numbers are not prime
		else if ((candidate % 2) == 0)
			isPrime = false;
		// an odd integer >= 5 is prime if not evenly divisible
		// by every odd integer up to its square root
		// Source: Carrano.
		else {
			for (int i = 3; i <= Math.sqrt(candidate) + 1; i += 2)
				if (candidate % i == 0) {
					isPrime = false;
					break;
				}
		}

		return isPrime;
	}

	/**
	 * re-hash the elements in the dictionary
	 */
	private void rehash() {
		// create new dictionary
		TableElement<K, V>[] newDictionary = (TableElement<K, V>[]) new TableElement[getNextPrime(dictionary.length)];
		// iterate over old dictionary
		for (int i = 0; i < dictionary.length; i++) {
			if (dictionary[i] != null) {
				int newSpace = hashValue(dictionary[i].getKey());

				if (newDictionary[newSpace] == null) {
					// add each element to the new dictionary
					newDictionary[newSpace] = dictionary[i];
				}
				// there is a collision
				else {
					boolean hasBeenInserted = false;
					for (int j = newSpace + 1; j < newDictionary.length && !hasBeenInserted; j++) {
						if (newDictionary[j] == null) {
							newDictionary[j] = dictionary[i];
							hasBeenInserted = true;
						}
					}
					// possible wrap around
					for (int k = 0; k < newSpace && !hasBeenInserted; k++) {
						if (newDictionary[k] == null) {
							newDictionary[k] = dictionary[i];
							hasBeenInserted = true;
						}
					}
				}
			}

		}

		// set new dictionary to old dictionary
		dictionary = newDictionary;
		currentCapacity = dictionary.length - numberOfElements;
		return;
	}

	/**
	 * Return the current load factor
	 * 
	 * @return
	 */
	private double getLoadFactor() {
		return numberOfElements / (double) currentCapacity;
	}

	/**
	 * Ensure there is capacity to perform an addition
	 */
	private void ensureCapacity() {
		double loadFactor = getLoadFactor();

		if (loadFactor >= CAPACITY_THRESHOLD)
			rehash();
	}

	// returns a set of all the keys
	public Set<K> keySet() {
		// new set of type K
		ArraySet<K> set = new ArraySet<K>(dictionary.length);
		// iterate over dictionary
		for (int i = 0; i < dictionary.length; i++) 
		{
			// add value
			if(dictionary[i] != null && dictionary[i].getToBeRemoved() != true)
			{
				set.add(dictionary[i].getKey());
			}

		}
		// return list
		return set;

	}

	// returns a set of all of the values
	public Set<V> valueSet() {
		// new set of type V
		ArraySet<V> set = new ArraySet<V>(dictionary.length);
		// iterate over dictionary
		for (int i = 0; i < dictionary.length; i++) {
			// add value
			if(dictionary[i] != null && dictionary[i].getToBeRemoved() != true)
			{
				set.add(dictionary[i].getValue());
			}

		}
		// return list
		return set;

	}
}
