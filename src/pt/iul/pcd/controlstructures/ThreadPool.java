package pt.iul.pcd.controlstructures;


public class ThreadPool {
	
	private BlockingQueue<Runnable> tasks = new BlockingQueue<Runnable>();
	
	
	public ThreadPool(int n) {
		Worker[] workers = new Worker[n];
		for(int i = 0; i!=n; i++) {
			Worker a = new Worker();
			workers[i]=a;
			a.start();
		}
	}
	
	public void submit(Runnable task) throws InterruptedException{
		tasks.offer(task);
	}
	
	
	private class Worker extends Thread{
			
		@Override
		public void run() {
			
			while(!this.isInterrupted()) {
				try {
					tasks.poll().run();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
}
