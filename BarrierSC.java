//Data Brezack
//Nate Verjinski
//Chris Burns

public class BarrierSC {
	public static void main (String args[]) {
		SCBarrier b = new SCBarrier(3);
		WorkerThread w1 = new WorkerThread (b, 1);
		WorkerThread w2= new WorkerThread (b, 2);
		WorkerThread w3 = new WorkerThread (b, 3);
		w1.start(); w2.start(); w3.start();
		try {
			w1.join();
			w2.join();
			w3.join();
		}
 		catch (InterruptedException e) {}
	}
}
class WorkerThread extends TDThread {
	private int ID;
	private SCBarrier b;
	public WorkerThread(SCBarrier b, int ID) {
		super("WorkerThread"+ID);
		this.ID = ID;this.b = b;
	}
	public void run() {
		for (int i=0;i<2; i++) {
			//System.out.println("Worker"+ID+" did work");
			b.waitB(ID);
		}
	}
}

class SCBarrier extends monitorSC {

	// declarations here
	// create a condition variable and a counter
	private conditionVariable condition = new conditionVariable();
	private int count = 0;
	
	private int n; // number of threads
	
	public SCBarrier (int n) {
		super("SCBarrierMonitor");
		this.n = n;
	}
	
	public void waitB(int ID) {
	   enterMonitor("waitB");
	   exerciseEvent("Thread "+ID+" beginWaitB");	// ignore these calls, for now.
	   
		// code here
		if(count < n-1){
			count ++;
			condition.waitC();
		}
		else{
			count = 0;
			//for(int i=0;i<n-1;i++){
				condition.signalCall();
			//}
		}

	   exerciseEvent("Thread "+ID+" endWaitB"); 	// ignore these calls, for now.
	   exitMonitor();
	}
}
