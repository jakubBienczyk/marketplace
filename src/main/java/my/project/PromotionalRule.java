package my.project;

import my.project.summary.BasketSummary;

public interface PromotionalRule {

    // Rules need to have information about all of the products and discounts (this is why I put BasketSummary as a parameter)
    void applyPromotion(BasketSummary basketSummary);

    int priority();

}
