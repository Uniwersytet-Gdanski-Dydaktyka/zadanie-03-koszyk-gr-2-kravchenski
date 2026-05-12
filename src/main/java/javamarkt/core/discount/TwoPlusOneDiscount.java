package javamarkt.core.discount;

import javamarkt.core.Product;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class TwoPlusOneDiscount implements Discount {
    @Override
    public List<Product> apply(List<Product> products) {
        if (products.size() < 3) return products;

        List<Product> sortedByPriceAsc = products.stream()
                .sorted(Comparator.comparingDouble(Product::getDiscountPrice).thenComparing(Product::getName))
                .toList();

        int freebies = sortedByPriceAsc.size() / 3;
        if (freebies == 0) return products;

        List<Product> result = new ArrayList<>(sortedByPriceAsc.size());
        for (int i = 0; i < sortedByPriceAsc.size(); i++) {
            Product p = sortedByPriceAsc.get(i);
            if (i < freebies) result.add(p.withDiscountPrice(0.0));
            else result.add(p);
        }
        return result;
    }
}
