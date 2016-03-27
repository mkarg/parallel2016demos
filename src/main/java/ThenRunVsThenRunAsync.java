import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThenRunVsThenRunAsync {
	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 3; i++)
			startFiber(i);
		Thread.sleep(1000);
	}

	/*
	 * Type of executor defines parallelism of thenRun().
	 * 
	 * Single Thread -> No Parallelism.
	 */
	private static final Executor CPU = Executors.newSingleThreadExecutor();

	private static void startFiber(int i) {
		CompletableFuture.runAsync(() -> System.out.printf("Fiber %d Stage %d%n", i, 1), CPU)
				.thenRunAsync(() -> System.out.printf("Fiber %d Stage %d%n", i, 2), CPU)
				.thenRunAsync(() -> System.out.printf("Fiber %d Stage %d%n", i, 3), CPU)
				.thenRunAsync(() -> System.out.printf("Fiber %d Stage %d%n", i, 4), CPU)
				.thenRunAsync(() -> System.out.printf("Fiber %d Stage %d%n", i, 5), CPU)
				.thenRun(() -> System.out.printf("Fiber %d Stage %d%n", i, 6))
				.thenRun(() -> System.out.printf("Fiber %d Stage %d%n", i, 7))
				.thenRun(() -> System.out.printf("Fiber %d Stage %d%n", i, 8))
				.thenRun(() -> System.out.printf("Fiber %d Stage %d%n", i, 9))
				.thenRun(() -> System.out.printf("Fiber %d Stage %d%n", i, 10));
	}
}