package nl.han.ica.datastructures.implementations;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.datastructures.IHANStack;

public class HANStack<T> implements IHANStack<T>
{
	private IHANLinkedList<T> stack = new HANLinkedList<>();

	@Override
	public void push(T value){
		this.stack.insert(0,value);
	}

	@Override
	public T pop() {
		T temp = this.stack.getFirst();

		this.stack.delete(0);

		return temp;
	}

	@Override
	public T peek() {
		return this.stack.getFirst();
	}

	@Override
	public String toString() {
		return "HANStack [stack=" + stack + "]";
	}
}
