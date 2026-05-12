package javamarkt.core.discount;

import javamarkt.core.Product;

import java.util.ArrayList;
import java.util.List;

public final class PresentOverSomePriceDiscount implements Discount {
    private final double threshold;
    private final Product present;

    public PresentOverSomePriceDiscount(double threshold, Product present) {
        if (threshold < 0) throw new IllegalArgumentException("threshold must be >= 0");
        if (present == null) throw new IllegalArgumentException("present must not be null");
        if (present.getPrice() != 0.0) throw new IllegalArgumentException("present price must be 0");
        this.threshold = threshold;
        this.present = present;
    }

    @Override
    public List<Product> apply(List<Product> products) {
        double sum = products.stream().mapToDouble(Product::getDiscountPrice).sum();
        if (sum <= threshold) return products;
        boolean alreadyPresent = products.stream().anyMatch(p -> p.getCode().equals(present.getCode()));
        if (alreadyPresent) return products;

        List<Product> result = new ArrayList<>(products.size() + 1);
        result.addAll(products);
        result.add(present);
        return result;
    }
}
