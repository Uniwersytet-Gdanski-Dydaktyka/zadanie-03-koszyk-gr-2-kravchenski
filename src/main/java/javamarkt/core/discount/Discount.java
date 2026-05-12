package javamarkt.core.discount;

import javamarkt.core.Product;

import java.util.List;

public interface Discount {
    List<Product> apply(List<Product> products);
}
