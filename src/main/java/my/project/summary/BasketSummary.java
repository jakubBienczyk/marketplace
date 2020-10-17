package my.project.summary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import my.project.Product;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;

@Data
@AllArgsConstructor
public class BasketSummary {

    // This class contains all information about the products and discounts
    // It's a mutable class, so promotional rules can modify it

    private final List<Item> items;
    private BigDecimal discount;

    BasketSummary(@NonNull List<Product> products) {
        this.items = products.stream().map(Item::new).collect(toList());
        this.discount = ZERO;
    }

    public BigDecimal totalPrice() {
        return items.stream()
                .map(Item::getPrice)
                .reduce(BigDecimal::add)
                .map(total -> total.subtract(discount))
                .orElse(ZERO);
    }

    @Data
    @AllArgsConstructor
    public static class Item {
        private final Product product;
        private boolean discounted;
        private BigDecimal price;

        public Item(@NonNull Product product) {
            this.product = product;
            this.discounted = false;
            this.price = product.getPrice();
        }

        public void discount(@NonNull BigDecimal newPrice) {
            if (newPrice.compareTo(ZERO) < 0) {
                throw new IllegalArgumentException("New price cannot be negative");
            }
            this.discounted = true;
            this.price = newPrice;
        }
    }
}
