package javamarkt.core.discount;

import javamarkt.core.Product;

import java.util.ArrayList;
import java.util.List;

public final class SingleUseCouponDiscount implements Discount {
    private final String productCode;
    private final double percent;

    public SingleUseCouponDiscount(String productCode, double percent) {
        if (productCode == null || productCode.isBlank()) throw new IllegalArgumentException("productCode must be non-blank");
        if (percent < 0 || percent > 1) throw new IllegalArgumentException("percent must be in [0,1]");
        this.productCode = productCode;
        this.percent = percent;
    }

    @Override
    public List<Product> apply(List<Product> products) {
        List<Product> result = new ArrayList<>(products.size());
        boolean applied = false;
        for (Product product : products) {
            if (!applied && product.getCode().equals(productCode)) {
                result.add(product.withDiscountPrice(product.getDiscountPrice() * (1.0 - percent)));
                applied = true;
            } else {
                result.add(product);
            }
        }
        return applied ? List.copyOf(result) : products;
    }
}
