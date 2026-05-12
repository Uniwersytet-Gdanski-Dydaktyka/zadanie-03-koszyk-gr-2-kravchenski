package javamarkt.core.discount;

import javamarkt.core.Product;

import java.util.List;

public final class PercentOverSomePriceDiscount implements Discount {
    private final double threshold;
    private final double percent;

    public PercentOverSomePriceDiscount(double threshold, double percent) {
        if (threshold < 0) throw new IllegalArgumentException("threshold must be >= 0");
        if (percent < 0 || percent > 1) throw new IllegalArgumentException("percent must be in [0,1]");
        this.threshold = threshold;
        this.percent = percent;
    }

    @Override
    public List<Product> apply(List<Product> products) {
        double sum = products.stream().mapToDouble(Product::getDiscountPrice).sum();
        if (sum <= threshold) return products;
        return products.stream()
                .map(p -> p.withDiscountPrice(p.getDiscountPrice() * (1.0 - percent)))
                .toList();
    }
}

