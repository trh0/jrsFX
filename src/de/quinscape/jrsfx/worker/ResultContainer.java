package de.quinscape.jrsfx.worker;

import de.quinscape.jrsfx.data.Prop;

public interface ResultContainer<T>
		extends Runnable {
	void run();

	void work();

	Prop<T> getData() throws Throwable;

}
