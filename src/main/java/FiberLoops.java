import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FiberLoops {
	private static final Executor CLI = Executors.newSingleThreadExecutor();

	public static void main(String[] args) {
		runFiber();
	}

	private static void runFiber() {
		Stream<CompletionStage<Integer>> stream = buildStream().mapToObj(CompletableFuture::completedFuture);
		stream.map(FiberLoops::square).reduce(FiberLoops::sum).map(FiberLoops::println);
	}

	private static IntStream buildStream() {
		return IntStream.range(0, 10);
	}

	private static CompletionStage<Integer> square(CompletionStage<Integer> stage) {
		return stage.thenApply(i -> i * 2);
	}

	private static CompletionStage<Integer> sum(CompletionStage<Integer> stage1, CompletionStage<Integer> stage2) {
		return stage1.thenCombine(stage2, (i, j) -> i + j);
	}

	private static CompletionStage<Void> println(CompletionStage<Integer> stage) {
		return stage.thenAcceptAsync(System.out::println, CLI);
	}
}