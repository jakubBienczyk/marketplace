package my.project.rules;

import lombok.NonNull;
import my.project.PromotionalRule;
import my.project.summary.BasketSummary;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

public class OverTwoProductPromotionalRule implements PromotionalRule {

    private static final int MIN_NUMBER_OF_PRODUCTS_TO_APPLY_PROMOTION = 2;

    private final String productCode;
    private final BigDecimal newPrice;

    public OverTwoProductPromotionalRule(@NonNull String productCode, @NonNull BigDecimal newPrice) {
        if (newPrice.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException("New price cannot be negative.");
        }
        this.productCode = productCode;
        this.newPrice = newPrice;
    }

    @Override
    public void applyPromotion(@NonNull BasketSummary basketSummary) {
        long numberOfTravelCardHoldersInBasket = getNumberOfTravelCardHoldersInBasket(basketSummary);
        if (numberOfTravelCardHoldersInBasket >= MIN_NUMBER_OF_PRODUCTS_TO_APPLY_PROMOTION) {
            basketSummary.getItems().forEach(this::applyPromotion);
        }
    }

    @Override
    public int priority() {
        return 1;
    }

    private long getNumberOfTravelCardHoldersInBasket(BasketSummary basketSummary) {
        return basketSummary.getItems().stream()
                .filter(this::isTravelCardHolder)
                .count();
    }

    private void applyPromotion(BasketSummary.Item item) {
        if (isTravelCardHolder(item) && newPrice.compareTo(item.getPrice()) < 0) {
            item.discount(newPrice);
        }
    }

    private boolean isTravelCardHolder(BasketSummary.Item item) {
        return productCode.equals(item.getProduct().getCode());
    }

}
