package javamarkt.core;

import javamarkt.core.discount.Discount;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class Cart {
    private final List<Product> products;
    private final List<Discount> discounts;
    private Comparator<Product> sortOrder;

    public Cart() {
        this(List.of());
    }

    public Cart(List<Product> products) {
        this.products = validateAndCopy(products);
        this.discounts = new ArrayList<>();
        this.sortOrder = defaultSortOrder();
    }

    public List<Product> getProducts() {
        return List.copyOf(applyDiscounts(products, discounts));
    }

    public void addDiscount(Discount discount) {
        if (discount == null) throw new IllegalArgumentException("discount must not be null");
        discounts.add(discount);
    }

    public void removeDiscount(Discount discount) {
        discounts.remove(discount);
    }

    public List<Discount> getDiscounts() {
        return List.copyOf(discounts);
    }

    public void setSortOrder(Comparator<Product> sortOrder) {
        if (sortOrder == null) throw new IllegalArgumentException("sortOrder must not be null");
        this.sortOrder = sortOrder;
    }

    public Comparator<Product> getSortOrder() {
        return sortOrder;
    }

    public Product getCheapest() {
        return products.stream()
                .min(Comparator.comparingDouble(Product::getPrice))
                .orElseThrow(NoSuchElementException::new);
    }

    public Product getMostExpensive() {
        return products.stream()
                .max(Comparator.comparingDouble(Product::getPrice))
                .orElseThrow(NoSuchElementException::new);
    }

    public List<Product> getCheapestN(int n) {
        if (n <= 0) return List.of();
        return products.stream()
                .sorted(Comparator.comparingDouble(Product::getPrice).thenComparing(Product::getName))
                .limit(n)
                .toList();
    }

    public List<Product> getMostExpensiveN(int n) {
        if (n <= 0) return List.of();
        return products.stream()
                .sorted(Comparator.comparingDouble(Product::getPrice).reversed().thenComparing(Product::getName))
                .limit(n)
                .toList();
    }

    public List<Product> getCartSorted() {
        return products.stream().sorted(sortOrder).toList();
    }

    public double getTotalSum() {
        return applyDiscounts(products, discounts).stream().mapToDouble(Product::getDiscountPrice).sum();
    }

    public double getBestTotalSum() {
        if (discounts.isEmpty()) return getTotalSum();
        return permutations(discounts).stream()
                .mapToDouble(order -> applyDiscounts(products, order).stream().mapToDouble(Product::getDiscountPrice).sum())
                .min()
                .orElse(getTotalSum());
    }

    private static List<Product> applyDiscounts(List<Product> baseProducts, List<Discount> order) {
        List<Product> current = baseProducts.stream().map(p -> p.withDiscountPrice(p.getPrice())).toList();
        for (Discount discount : order) {
            current = discount.apply(current);
        }
        return current;
    }

    private static Comparator<Product> defaultSortOrder() {
        return Comparator.comparingDouble(Product::getPrice).reversed().thenComparing(Product::getName);
    }

    private static List<Product> validateAndCopy(List<Product> input) {
        if (input == null) return List.of();
        for (Product p : input) {
            Objects.requireNonNull(p, "products must not contain null");
        }
        return List.copyOf(input);
    }

    private static List<List<Discount>> permutations(List<Discount> discounts) {
        if (discounts.size() <= 1) return List.of(List.copyOf(discounts));

        List<List<Discount>> result = new ArrayList<>();
        for (int i = 0; i < discounts.size(); i++) {
            Discount head = discounts.get(i);
            List<Discount> rest = new ArrayList<>(discounts);
            rest.remove(i);
            for (List<Discount> tail : permutations(rest)) {
                List<Discount> perm = new ArrayList<>(discounts.size());
                perm.add(head);
                perm.addAll(tail);
                result.add(perm);
            }
        }
        return result;
    }
}
