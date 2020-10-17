package my.project.rules;

import lombok.NonNull;
import my.project.PromotionalRule;
import my.project.summary.BasketSummary;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

public class PriceOverThresholdPromotionalRule implements PromotionalRule {

    private final BigDecimal threshold;
    private final int reductionInPercents;

    public PriceOverThresholdPromotionalRule(@NonNull BigDecimal threshold, int reductionInPercents) {
        if (threshold.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative.");
        }
        if (reductionInPercents <= 0) {
            throw new IllegalArgumentException("Reduction of price must be positive");
        }
        this.threshold = threshold;
        this.reductionInPercents = reductionInPercents;
    }

    @Override
    public void applyPromotion(@NonNull BasketSummary basketSummary) {
        BigDecimal totalPrice = basketSummary.totalPrice();
        if (totalPrice.compareTo(threshold) > 0) {
            BigDecimal discount = totalPrice.multiply(BigDecimal.valueOf(reductionInPercents / 100d));
            basketSummary.setDiscount(basketSummary.getDiscount().add(discount));
        }
    }

    @Override
    public int priority() {
        return 10;
    }
}
