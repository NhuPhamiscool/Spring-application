package au.edu.sydney.soft3202.task1;

import net.jqwik.api.Property;
import net.jqwik.api.Arbitrary;
import java.util.List;
import java.util.Map.Entry;
import net.jqwik.api.*;
import net.jqwik.api.constraints.Positive;
import net.jqwik.api.constraints.IntRange;
import java.util.ArrayList;
import java.util.Arrays;

import net.jqwik.api.constraints.NotEmpty;

import static org.assertj.core.api.Assertions.assertThat;

public class ShoppingBasketPropertyTest {

    @Property
    void getValueEmptyPropertyTest() {
        ShoppingBasket sb = new ShoppingBasket();
        assertThat(sb.getValue()).isEqualTo(null);
    }

    @Provide
    Arbitrary<String> correctNames() {
        return Arbitraries.of("apple", "orange", "pear", "banana");
    }

    @Property
    void addItemPropertyTest(@ForAll("correctNames") String item, @ForAll @Positive int count) {
        ShoppingBasket sb = new ShoppingBasket();
        sb.addItem(item, count);
        Double itemCost = 0.0;

        List<Entry<String, Double>> costList = sb.getAllItemCost();
        for(Entry<String, Double> entry: costList){
            if(entry.getKey().equals(item)) {
                itemCost = entry.getValue();
            }
        }
        assertThat(sb.getValue()).isEqualTo(count*itemCost);
    }

    @Property
    void removeAllTest(@ForAll("correctNames") String item, @ForAll @Positive int count) {
        ShoppingBasket sb = new ShoppingBasket();
        sb.addItem(item, count);
        Double itemCost = 0.0;

        List<Entry<String, Double>> costList = sb.getAllItemCost();
        for(Entry<String, Double> entry: costList){
            if(entry.getKey().equals(item)) {
                itemCost = entry.getValue();
            }
        }
        sb.clear();
        assertThat(sb.getValue()).isEqualTo(null);
    }

    @Property
    void BonusAddArbitraryItemNameTest(@ForAll String item, @ForAll @Positive double cost) {
        ShoppingBasket sb = new ShoppingBasket();
        sb.addNewItem(item, cost);
        
        assertThat(Arrays.asList(sb.names).contains(item.toLowerCase())).isEqualTo(true);
    }

    @Property
    void BonusDeleteArbitraryItemNameTest(@ForAll String item, @ForAll @Positive double cost) {
        ShoppingBasket sb = new ShoppingBasket();
        sb.addNewItem(item, cost);
        sb.deleteItem(item);

        assertThat(Arrays.asList(sb.names).contains(item.toLowerCase())).isEqualTo(false);
    }

    @Property
    void BonusCountArbitraryTest(@ForAll @NotEmpty String item, @ForAll @Positive double cost, @ForAll @IntRange(min = 1, max = Integer.MAX_VALUE) int count) {
        ShoppingBasket sb = new ShoppingBasket();
        sb.addNewItem(item, cost);
        int toCompare = sb.items.get(item.toLowerCase()) + count;
        sb.addItem(item, count);

        assertThat(sb.items.get(item.toLowerCase())).isEqualTo(toCompare);
    
    }

    @Property
    void BonusRemoveCountArbitraryTest(@ForAll String item, @ForAll @Positive double cost,
    @ForAll @IntRange(min = 1, max = Integer.MAX_VALUE) int countToRemove) {
        ShoppingBasket sb = new ShoppingBasket();
        sb.addNewItem(item, cost);
        sb.addItem(item, Integer.MAX_VALUE);
        int toCompare = sb.items.get(item.toLowerCase()) - countToRemove;
        sb.removeItem(item, countToRemove);

        assertThat(sb.items.get(item.toLowerCase())).isEqualTo(toCompare);
    }
       
}
