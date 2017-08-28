package de.quinscape.jrsfx.data;


public class Mapping<K, V> {
	
	public Mapping(K k, V v) {
		this.key = k;
		this.value = v;
	}
	
	private final K key;
	private final V value;
        
        public K getKey(){
        return this.key;
        }
        public V getValue(){
        return this.value;
        }
	
}
