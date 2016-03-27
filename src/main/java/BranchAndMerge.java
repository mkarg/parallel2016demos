import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class BranchAndMerge {
	public static void main(String[] args) throws InterruptedException {
		buildFiber().join();
	}

	private static CompletableFuture<Integer> buildFiber() {
		/*
		 * 2 * 3 + 2 * 5 = 16
		 */

		CompletionStage<Integer> a = CompletableFuture.completedFuture(2);

		// Branch
		CompletionStage<Integer> b = a.thenApply(i -> i * 3);
		CompletionStage<Integer> c = a.thenApply(i -> i * 5);

		// Merge
		CompletionStage<Integer> d = b.thenCombine(c, (j, k) -> j + k);

		d.thenAccept(l -> System.out.printf("Result: %d%n", l));

		return d.toCompletableFuture();
	}
}