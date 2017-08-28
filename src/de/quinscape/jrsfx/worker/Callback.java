package de.quinscape.jrsfx.worker;

public interface Callback<T> {

	public void callback(CallbackTask<T> callbackTask);

}
