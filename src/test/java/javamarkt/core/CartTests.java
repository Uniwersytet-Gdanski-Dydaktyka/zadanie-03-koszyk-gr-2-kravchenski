package javamarkt.core;

import javamarkt.core.discount.PercentOverSomePriceDiscount;
import javamarkt.core.discount.PresentOverSomePriceDiscount;
import javamarkt.core.discount.SingleUseCouponDiscount;
import javamarkt.core.discount.TwoPlusOneDiscount;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CartTests {

    private final Product cheap = new Product("1", "Apple", 100);
    private final Product mid = new Product("2", "Banana", 200);
    private final Product expensive = new Product("3", "Tomato", 300);

    @Nested
    class Validation {
        @Test
        void productValidation() {
            assertThrows(IllegalArgumentException.class, () -> new Product(" ", "Ok", 1));
            assertThrows(IllegalArgumentException.class, () -> new Product("x", " ", 1));
            assertThrows(IllegalArgumentException.class, () -> new Product("x", "Ok", -1));
        }

        @Test
        void cartNullList_isTreatedAsEmpty() {
            assertEquals(0.0, new Cart(null).getTotalSum());
        }

        @Test
        void cartNullElement_throws() {
            List<Product> products = new ArrayList<>();
            products.add(cheap);
            products.add(null);

            assertThrows(NullPointerException.class, () -> new Cart(products));
        }
    }

    @Nested
    class TotalsAndFinding {
        @Test
        void emptyCart_returnsZero() {
            assertEquals(0.0, new Cart().getTotalSum());
        }

        @Test
        void multipleProducts_returnsSum() {
            assertEquals(600.0, new Cart(List.of(cheap, mid, expensive)).getTotalSum());
        }

        @Test
        void productWithZeroPrice_countedWithoutError() {
            Product free = new Product("0", "Freebie", 0);
            assertEquals(100.0, new Cart(List.of(cheap, free)).getTotalSum());
        }

        @Test
        void getCheapestAndMostExpensive() {
            Cart cart = new Cart(List.of(cheap, mid, expensive));
            assertEquals(cheap, cart.getCheapest());
            assertEquals(expensive, cart.getMostExpensive());
        }

        @Test
        void getCheapest_emptyCart_throwsNoSuchElement() {
            assertThrows(NoSuchElementException.class, () -> new Cart().getCheapest());
        }

        @Test
        void getMostExpensive_emptyCart_throwsNoSuchElement() {
            assertThrows(NoSuchElementException.class, () -> new Cart().getMostExpensive());
        }
    }

    @Nested
    class NProducts {
        @Test
        void getCheapestN_returnsNProductsInAscendingOrder() {
            Cart cart = new Cart(List.of(cheap, mid, expensive));
            assertEquals(List.of(cheap, mid), cart.getCheapestN(2));
        }

        @Test
        void getMostExpensiveN_returnsNProductsInDescendingOrder() {
            Cart cart = new Cart(List.of(cheap, mid, expensive));
            assertEquals(List.of(expensive, mid), cart.getMostExpensiveN(2));
        }

        @Test
        void nonPositiveN_returnsEmptyList() {
            Cart cart = new Cart(List.of(cheap, mid));
            assertEquals(List.of(), cart.getCheapestN(0));
            assertEquals(List.of(), cart.getMostExpensiveN(-1));
        }

        @Test
        void nGreaterThanCartSize_returnsAllProductsInRequestedOrder() {
            Cart cart = new Cart(List.of(mid, cheap));

            assertEquals(List.of(cheap, mid), cart.getCheapestN(10));
            assertEquals(List.of(mid, cheap), cart.getMostExpensiveN(10));
        }
    }

    @Nested
    class Sorting {
        @Test
        void defaultOrder_priceDescendingThenNameAscending() {
            Product avocado = new Product("4", "Avocado", 200);
            Product mango = new Product("5", "Mango", 200);
            Cart cart = new Cart(List.of(cheap, avocado, mango, expensive));
            List<Product> sorted = cart.getCartSorted();

            assertEquals(expensive, sorted.get(0));
            assertEquals(avocado, sorted.get(1));
            assertEquals(mango, sorted.get(2));
            assertEquals(cheap, sorted.get(3));
        }

        @Test
        void customSortOrder_sortsByNameAscending() {
            Cart cart = new Cart(List.of(expensive, cheap, mid));
            cart.setSortOrder(Comparator.comparing(Product::getName));

            List<Product> sorted = cart.getCartSorted();
            assertEquals("Apple", sorted.get(0).getName());
            assertEquals("Banana", sorted.get(1).getName());
            assertEquals("Tomato", sorted.get(2).getName());
        }

        @Test
        void setSortOrder_null_throwsIllegalArgument() {
            Cart cart = new Cart(List.of(expensive, cheap, mid));
            assertThrows(IllegalArgumentException.class, () -> cart.setSortOrder(null));
        }

        @Test
        void getCartSorted_returnsNewList_notBackedByCart() {
            Cart cart = new Cart(List.of(expensive, cheap, mid));
            List<Product> sorted = cart.getCartSorted();
            assertThrows(UnsupportedOperationException.class, sorted::clear);
            assertEquals(3, cart.getProducts().size());
        }
    }

    @Nested
    class Discounts {
        @Test
        void twoPlusOne_threeProducts_cheapestIsFree() {
            Cart cart = new Cart(List.of(cheap, mid, expensive));
            cart.addDiscount(new TwoPlusOneDiscount());
            assertEquals(500.0, cart.getTotalSum());
        }

        @Test
        void twoPlusOne_sixProducts_twoAreFree() {
            Product p4 = new Product("4", "Grape", 150);
            Product p5 = new Product("5", "Lemon", 250);
            Product p6 = new Product("6", "Mango", 350);

            Cart cart = new Cart(List.of(cheap, mid, expensive, p4, p5, p6));
            cart.addDiscount(new TwoPlusOneDiscount());

            assertEquals(1100.0, cart.getTotalSum());
        }

        @Test
        void percentOver300_appliesOnlyWhenOverThreshold() {
            Cart cart = new Cart(List.of(mid, expensive));
            cart.addDiscount(new PercentOverSomePriceDiscount(300, 0.05));
            assertEquals(475.0, cart.getTotalSum());

            Cart cart2 = new Cart(List.of(cheap, mid));
            cart2.addDiscount(new PercentOverSomePriceDiscount(300, 0.05));
            assertEquals(300.0, cart2.getTotalSum());
        }

        @Test
        void discountsOnEmptyCart_doNotFailOrChangeTotal() {
            Cart cart = new Cart();
            cart.addDiscount(new TwoPlusOneDiscount());
            cart.addDiscount(new PercentOverSomePriceDiscount(300, 0.05));
            cart.addDiscount(new PresentOverSomePriceDiscount(200, new Product("MUG", "Company Mug", 0)));
            cart.addDiscount(new SingleUseCouponDiscount("1", 0.30));

            assertEquals(List.of(), cart.getProducts());
            assertEquals(0.0, cart.getTotalSum());
        }

        @Test
        void removeDiscount_stopsApplyingIt() {
            PercentOverSomePriceDiscount discount = new PercentOverSomePriceDiscount(300, 0.05);
            Cart cart = new Cart(List.of(mid, expensive));
            cart.addDiscount(discount);

            assertEquals(475.0, cart.getTotalSum());

            cart.removeDiscount(discount);
            assertEquals(500.0, cart.getTotalSum());
        }

        @Test
        void presentOver200_addsMugOnlyWhenOverThreshold() {
            Product mug = new Product("MUG", "Company Mug", 0);
            Cart cart = new Cart(List.of(mid, cheap));
            cart.addDiscount(new PresentOverSomePriceDiscount(200, mug));
            assertEquals(3, cart.getProducts().size());
            assertEquals(300.0, cart.getTotalSum());
        }

        @Test
        void singleUseCoupon_appliesToOnlyOneSelectedProductAndIsDeterministic() {
            Product secondProductWithSameCode = new Product("2", "Blueberry", 50);
            Cart cart = new Cart(List.of(cheap, mid, secondProductWithSameCode));
            cart.addDiscount(new SingleUseCouponDiscount("2", 0.30));

            assertEquals(290.0, cart.getTotalSum());
            assertEquals(290.0, cart.getTotalSum());
        }

        @Test
        void bestOrder_checksEveryDiscountOrderAndChoosesLowestTotal() {
            Product cheapest = new Product("4", "Kiwi", 10);
            Cart cart = new Cart(List.of(cheap, mid, cheapest));
            cart.addDiscount(new TwoPlusOneDiscount());
            cart.addDiscount(new PercentOverSomePriceDiscount(300, 0.05));

            double fixed = cart.getTotalSum();
            double best = cart.getBestTotalSum();

            assertEquals(300.0, fixed);
            assertEquals(285.0, best);
        }
    }
}
