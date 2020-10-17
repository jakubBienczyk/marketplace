package my.project;

import lombok.NonNull;
import my.project.summary.BasketSummaryFactory;

import java.util.LinkedList;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.CEILING;

public class Checkout {

    private final List<PromotionalRule> promotionalRules;
    private final BasketSummaryFactory basketSummaryFactory;
    private final List<Product> products;

    public Checkout(@NonNull List<PromotionalRule> promotionalRules, @NonNull BasketSummaryFactory basketSummaryFactory) {
        this.promotionalRules = List.copyOf(promotionalRules);
        this.basketSummaryFactory = basketSummaryFactory;
        this.products = new LinkedList<>();
    }

    public void scan(@NonNull Product product) {
        products.add(product);
    }

    public Double total() {
        return basketSummaryFactory.createBasketSummary(products, promotionalRules)
                .totalPrice()
                .setScale(2, CEILING)
                .max(ZERO)
                .doubleValue();
    }
}
