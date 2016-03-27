import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExecutorsDemo {
	public static void main(String[] args) throws InterruptedException {
		buildFiber().join();
	}

	private static final Executor CPU = Executors.newWorkStealingPool();

	private static final Executor CLI = Executors.newSingleThreadExecutor();

	private static CompletableFuture<Integer> buildFiber() {
		/*
		 * 2 * 3 + 2 * 5 = 16
		 */

		CompletionStage<Integer> a = CompletableFuture.supplyAsync(() -> 2, CPU);

		// Branch
		CompletionStage<Integer> b = a.thenApplyAsync(i -> i * 3, CPU);
		CompletionStage<Integer> c = a.thenApplyAsync(i -> i * 5, CPU);

		// Merge
		CompletionStage<Integer> d = b.thenCombineAsync(c, (j, k) -> j + k, CPU);

		d.thenAcceptAsync(l -> System.out.printf("Result: %d%n", l), CLI);

		return d.toCompletableFuture();
	}
}