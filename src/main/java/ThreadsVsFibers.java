import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ThreadsVsFibers {
	public static void main(String[] args) throws InterruptedException {
		int n = 10000;
		while (true) {
			measure(() -> withThreads(n, ThreadsVsFibers::compute, ThreadsVsFibers::store), "Threads");
			measure(() -> withFibers(n, ThreadsVsFibers::compute, ThreadsVsFibers::store), "Fibers");
		}
	}

	private static int compute() {
		int s = 0;
		for (int i = 0; i < 100; i++)
			s += i;
		return s;
	}

	private static void store(int s) {
		try {
			Path p = Files.createTempFile(null, null);
			BufferedWriter w = Files.newBufferedWriter(p);
			w.write(String.valueOf(s));
			w.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	private static void measure(Runnable code, String name) {
		long t0 = System.nanoTime();
		code.run();
		long t1 = System.nanoTime();
		System.out.printf("%s: %dms%n", name, (t1 - t0) / 1000000);
	}

	private static void withThreads(int n, Supplier<Integer> compute, final Consumer<Integer> store) {
		try {
			Thread[] threads = new Thread[n];
			for (int i = 0; i < n; i++) {
				Thread t = new Thread(() -> {
					int s = compute.get();
					store.accept(s);
				});
				threads[i] = t;
				t.start();
			}

			for (int i = 0; i < n; i++)
				threads[i].join();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	private static final Executor CPU = Executors.newWorkStealingPool();

	private static final Executor DISK = Executors.newSingleThreadExecutor();

	private static void withFibers(int n, Supplier<Integer> compute, Consumer<Integer> store) {
		try {
			CompletableFuture<?>[] fibers = new CompletableFuture<?>[n];
			for (int i = 0; i < n; i++) {
				CompletableFuture<?> f = CompletableFuture.supplyAsync(compute, CPU).thenAcceptAsync(store, DISK);
				fibers[i] = f;
			}

			CompletableFuture.allOf(fibers).join();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}