package my.project.summary;

import lombok.NonNull;
import my.project.Product;
import my.project.PromotionalRule;

import java.util.List;

import static java.util.Comparator.comparingInt;

public class BasketSummaryFactory {

    public BasketSummary createBasketSummary(@NonNull List<Product> products, @NonNull List<PromotionalRule> promotionalRules) {
        BasketSummary basketSummary = new BasketSummary(products);
        promotionalRules.stream()
                .sorted(comparingInt(PromotionalRule::priority))
                .forEach(promotionalRule -> promotionalRule.applyPromotion(basketSummary));
        return basketSummary;
    }

}
