package javamarkt.core.discount;

import javamarkt.core.Product;

import java.util.List;
import java.util.Optional;

public final class SingleUseCouponDiscount implements Discount {
    private final String productCode;
    private final double percent;
    private boolean used;

    public SingleUseCouponDiscount(String productCode, double percent) {
        if (productCode == null || productCode.isBlank()) throw new IllegalArgumentException("productCode must be non-blank");
        if (percent < 0 || percent > 1) throw new IllegalArgumentException("percent must be in [0,1]");
        this.productCode = productCode;
        this.percent = percent;
        this.used = false;
    }

    public boolean isUsed() {
        return used;
    }

    @Override
    public List<Product> apply(List<Product> products) {
        Optional<Product> target = products.stream().filter(p -> p.getCode().equals(productCode)).findFirst();
        if (target.isEmpty()) return products;
        if (!used) used = true;
        return products.stream()
                .map(p -> p.getCode().equals(productCode)
                        ? p.withDiscountPrice(p.getDiscountPrice() * (1.0 - percent))
                        : p)
                .toList();
    }
}
