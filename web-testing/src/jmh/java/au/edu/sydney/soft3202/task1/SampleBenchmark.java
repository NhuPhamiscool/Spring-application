package au.edu.sydney.soft3202.task1;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class SampleBenchmark {

    @Fork(value=1)
    @Warmup(iterations=1)
    @Measurement(iterations = 1)
    @Benchmark @BenchmarkMode(Mode.Throughput)

    public void addItemBenchmark(Blackhole bh) {
        ShoppingBasket sb = new ShoppingBasket();
        sb.addItem("apple", 1);
    }

    @Fork(value=1)
    @Warmup(iterations=1)
    @Measurement(iterations = 1)
    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void addNewItemNameTest(Blackhole bh) {
        ShoppingBasket sb = new ShoppingBasket();

        sb.addItemName("strawberry");
    }

    @Fork(value=1)
    @Warmup(iterations=1)
    @Measurement(iterations = 1)
    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void removeItemNameTest(Blackhole bh) {
        ShoppingBasket sb = new ShoppingBasket();

        sb.deleteItem("strawberry");
    }

    @Fork(value=1)
    @Warmup(iterations=1)
    @Measurement(iterations = 1)
    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void addNewItemTest(Blackhole bh) {
        ShoppingBasket sb = new ShoppingBasket();

        sb.addNewItem("strawberry", 1.2);
    }

    @Fork(value=1)
    @Warmup(iterations=1)
    @Measurement(iterations = 1)
    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void removeNewItemTest(Blackhole bh) {
        ShoppingBasket sb = new ShoppingBasket();

        sb.addNewItem("watermelon", 1.2);
        sb.deleteItem("watermelon");
    }

    @Fork(value=1)
    @Warmup(iterations=1)
    @Measurement(iterations = 1)
    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void removeItemWith10countsTest(Blackhole bh) {
        ShoppingBasket sb = new ShoppingBasket();

        sb.addNewItem("durian", 5.2);
        sb.addItem("durian", 10);
        sb.deleteItem("durian");
    }
}
