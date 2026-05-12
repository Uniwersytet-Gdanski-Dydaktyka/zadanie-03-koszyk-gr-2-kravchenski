package javamarkt.core;

import java.util.Objects;

public final class Product {
    private final String code;
    private final String name;
    private final double price;
    private final double discountPrice;

    public Product(String code, String name, double price) {
        this(code, name, price, price);
    }

    private Product(String code, String name, double price, double discountPrice) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code must be non-blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must be non-blank");
        }
        if (price < 0) {
            throw new IllegalArgumentException("price must be >= 0");
        }
        if (discountPrice < 0) {
            throw new IllegalArgumentException("discountPrice must be >= 0");
        }
        if (discountPrice > price) {
            throw new IllegalArgumentException("discountPrice must be <= price");
        }
        this.code = code;
        this.name = name;
        this.price = price;
        this.discountPrice = discountPrice;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public Product withDiscountPrice(double newDiscountPrice) {
        return new Product(code, name, price, newDiscountPrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Double.compare(price, product.price) == 0
                && Double.compare(discountPrice, product.discountPrice) == 0
                && code.equals(product.code)
                && name.equals(product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, price, discountPrice);
    }

    @Override
    public String toString() {
        return "Product{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", discountPrice=" + discountPrice +
                '}';
    }
}

