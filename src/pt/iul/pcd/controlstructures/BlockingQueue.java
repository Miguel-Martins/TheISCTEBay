package pt.iul.pcd.controlstructures;

import java.util.ArrayDeque;
import java.util.Queue;

public class BlockingQueue<T> {

	private Queue<T> queue;
	private int size;
	private int elements;

	public BlockingQueue() {
		queue = new ArrayDeque<>();
		size = -1;
	}

	public BlockingQueue(int size) {
		if (size <= 0)
			throw new IllegalArgumentException("Tamanho inválido.");
		queue = new ArrayDeque<>(size);
		this.size = size;
	}

	public synchronized void offer(T e) throws InterruptedException {

		while (queue.size() == size) {
			wait();
		}

		notifyAll();
		queue.add(e);

	}

	public synchronized T poll() throws InterruptedException {
		while (queue.isEmpty()) {
			wait();
		}
		if (size != -1)
			notifyAll();
		return queue.remove();
	}

	public synchronized int size() {
		return queue.size();
	}

	public synchronized void clear() {
		queue.clear();
	}
	
	public boolean isEmpty()
	{
		return queue.isEmpty();
	}
}
