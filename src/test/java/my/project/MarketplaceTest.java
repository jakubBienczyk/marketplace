package my.project;

import my.project.rules.PriceOverThresholdPromotionalRule;
import my.project.rules.OverTwoProductPromotionalRule;
import my.project.summary.BasketSummaryFactory;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MarketplaceTest {

    private final Product travelCardHolder = new Product("001", "Travel Card Holder", BigDecimal.valueOf(9.25));
    private final Product cufflinks = new Product("002", "Personalised cufflinks", BigDecimal.valueOf(45));
    private final Product kidsShirt = new Product("003", " Kids T-shirt", BigDecimal.valueOf(19.95));

    private final PriceOverThresholdPromotionalRule priceOver60PromotionalRule = new PriceOverThresholdPromotionalRule(BigDecimal.valueOf(60), 10);
    private final OverTwoProductPromotionalRule travelCardHolderPromotionalRule = new OverTwoProductPromotionalRule("001", BigDecimal.valueOf(8.50));

    private final BasketSummaryFactory basketSummaryFactory = new BasketSummaryFactory();

    @Test
    public void shouldApplyOver60PromotionalRule() {
        // given
        List<PromotionalRule> promotionalRules = List.of(travelCardHolderPromotionalRule, priceOver60PromotionalRule);
        Checkout checkout = new Checkout(promotionalRules, basketSummaryFactory);

        // when
        checkout.scan(travelCardHolder);
        checkout.scan(cufflinks);
        checkout.scan(kidsShirt);

        // then
        assertThat(checkout.total()).isEqualTo(66.78);
    }

    @Test
    public void shouldApplyTravelCardHolderPromotionalRule() {
        // given
        List<PromotionalRule> promotionalRules = List.of(travelCardHolderPromotionalRule, priceOver60PromotionalRule);
        Checkout checkout = new Checkout(promotionalRules, basketSummaryFactory);

        // when
        checkout.scan(travelCardHolder);
        checkout.scan(kidsShirt);
        checkout.scan(travelCardHolder);

        // then
        assertThat(checkout.total()).isEqualTo(36.95);
    }

    @Test
    public void shouldApplyBothPromotionalRules() {
        // given
        List<PromotionalRule> promotionalRules = List.of(travelCardHolderPromotionalRule, priceOver60PromotionalRule);
        Checkout checkout = new Checkout(promotionalRules, basketSummaryFactory);

        // when
        checkout.scan(travelCardHolder);
        checkout.scan(travelCardHolder);
        checkout.scan(cufflinks);
        checkout.scan(kidsShirt);

        // then
        assertThat(checkout.total()).isEqualTo(73.76);
    }
}
