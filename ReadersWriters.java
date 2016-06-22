//Data Brezack
//Nate Verjinski
//Chris Burns

public final class ReadersWriters {
    public static void main (String args[]) {
	r_gt_w_1SC c = new r_gt_w_1SC(); 
	Reader r1 = new Reader(c,1);	Reader r2 = new Reader(c,2); 
	Reader r3 = new Reader(c,3);
	Writer w1 = new Writer(c,1);	Writer w2 = new Writer(c,2); 
	r1.start(); w1.start(); r2.start(); w2.start();	r3.start();
	try {
	 r1.join();r2.join();r3.join();w1.join();w2.join();
	} catch (InterruptedException e) {}
	
    }
}

final class Reader extends TDThread {
    private r_gt_w_1SC c;
    private int num;
    Reader (r_gt_w_1SC c, int num) {super("Reader"+num);this.c = c; this.num = num; }
    public void run () {
	c.read(num);
    }
}

final class Writer extends TDThread {
    private r_gt_w_1SC c;
    private int num;
    Writer (r_gt_w_1SC c, int num) {super("Writer"+num);this.c = c; this.num = num; }
    public void run () {		
	c.write(num);
    }
}
    
class r_gt_w_1SC extends monitorSC { 
	int readerCount = 0;			// number of active readers 
	boolean writing = false; 		// true if a writer is writing
	conditionVariable readerQ = new conditionVariable(); 
	conditionVariable writerQ = new conditionVariable();
	int signaledReaders = 0;  // number of readers signaled in endWrite

    public void read(int num) {
		startRead(); 
		//System.out.println("Reader" + num + " reading");
		try{Thread.sleep(10);}catch(InterruptedException e){}; 
		endRead(); 
    }
    public void write(int num) {
		startWrite(); 
		//System.out.println("Writer" + num + " writing");
		try{Thread.sleep(10);}catch(InterruptedException e){}; 
		endWrite();
    }
    
	public void startRead() {
		if (writing) { // readers must wait if a writer is writing
		readerQ.waitC();
		--signaledReaders;// another signaled reader has started reading
		}
		++readerCount;
		readerQ.signalC(); // continue cascaded wakeup of readers
	}

	public void endRead() {
		enterMonitor("endRead");
		--readerCount;
		if (readerCount == 0 && signaledReaders==0)	
		// signal writer if no more readers are reading and the signaledReaders have  read
		writerQ.signalC();
		exitMonitor();
	}
	public void startWrite() {
		enterMonitor("startWrite");
		// the writer waits if another writer is writing, or a reader is reading or waiting, 
		// or the writer is barging
		while (readerCount > 0 || writing || !readerQ.empty() || signaledReaders>0) 
			writerQ.waitC();
		writing = true;
		exitMonitor();
	}
	public void endWrite() {
		writing = false;
		if (!readerQ.empty()) { // priority is given to waiting readers
			signaledReaders = readerQ.length();
			readerQ.signalC();
		}
		else writerQ.signalC();
	}
	
}
