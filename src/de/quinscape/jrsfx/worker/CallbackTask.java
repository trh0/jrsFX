package de.quinscape.jrsfx.worker;

import de.quinscape.jrsfx.data.Prop;
import de.quinscape.jrsfx.util.ApplicationIO;

public abstract class CallbackTask<T>
		implements ResultContainer<T> {

	public CallbackTask(Callback<T> destination) {

		this.destination = destination;
	}

	private Callback<T> destination;

	protected volatile Prop<T> data;

	@Override
	public void run() {
		work();
		destination.callback(this);
	}

	@Override
	public Prop<T> getData() {
		try {
			return data;
		}
		finally {
			data = null;
			try {
				this.finalize();

			}
			catch (Throwable e) {
				ApplicationIO.toErrorStream(e);
			}
		}
	}

}
