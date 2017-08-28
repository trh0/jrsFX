package de.quinscape.jrsfx.data;


public final class Prop<T> {

	public Prop(T value) {
		this.value = value;
	}

	protected final T value;
        
        public T getValue(){
        return this.value;
        }
}
