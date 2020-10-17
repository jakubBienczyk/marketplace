package my.project.rules;


import my.project.Product;
import my.project.summary.BasketSummary;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class OverTwoProductPromotionalRuleTest {

    private final Product product1 = new Product("1", "product 1", BigDecimal.valueOf(1));
    private final Product product2 = new Product("2", "product 2", BigDecimal.valueOf(2));

    @Test
    public void shouldApplyPromotionalRule() {
        // given
        BigDecimal newPrice = BigDecimal.valueOf(0.5);
        OverTwoProductPromotionalRule productPromotionalRule = new OverTwoProductPromotionalRule(product1.getCode(), newPrice);
        List<BasketSummary.Item> items = List.of(
                new BasketSummary.Item(product1),
                new BasketSummary.Item(product2),
                new BasketSummary.Item(product1),
                new BasketSummary.Item(product2),
                new BasketSummary.Item(product1)
        );
        BasketSummary basketSummary = new BasketSummary(items, ZERO);

        // when
        productPromotionalRule.applyPromotion(basketSummary);

        // then
        assertThat(basketSummary.getDiscount()).isEqualTo(ZERO);
        assertThat(basketSummary.getItems())
                .extracting("product", "discounted", "price")
                .containsExactly(
                        tuple(product1, true, newPrice),
                        tuple(product2, false, product2.getPrice()),
                        tuple(product1, true, newPrice),
                        tuple(product2, false, product2.getPrice()),
                        tuple(product1, true, newPrice)
                );
    }

    @Test
    public void shouldApplyPromotionalRuleOnlyToProductsWithHigherPrice() {
        // given
        BigDecimal newPrice = BigDecimal.valueOf(0.5);
        BigDecimal smallerPrice = BigDecimal.valueOf(0.3);
        OverTwoProductPromotionalRule productPromotionalRule = new OverTwoProductPromotionalRule(product1.getCode(), newPrice);
        List<BasketSummary.Item> items = List.of(
                new BasketSummary.Item(product1, true, smallerPrice),
                new BasketSummary.Item(product2),
                new BasketSummary.Item(product1),
                new BasketSummary.Item(product2),
                new BasketSummary.Item(product1, true, smallerPrice)
        );
        BasketSummary basketSummary = new BasketSummary(items, ZERO);

        // when
        productPromotionalRule.applyPromotion(basketSummary);

        // then
        assertThat(basketSummary.getDiscount()).isEqualTo(ZERO);
        assertThat(basketSummary.getItems())
                .extracting("product", "discounted", "price")
                .containsExactly(
                        tuple(product1, true, smallerPrice),
                        tuple(product2, false, product2.getPrice()),
                        tuple(product1, true, newPrice),
                        tuple(product2, false, product2.getPrice()),
                        tuple(product1, true, smallerPrice)
                );
    }

    @Test
    public void shouldNotApplyRuleWhenOnyOneProductExist() {
        // given
        BigDecimal newPrice = BigDecimal.valueOf(0.5);
        OverTwoProductPromotionalRule productPromotionalRule = new OverTwoProductPromotionalRule(product1.getCode(), newPrice);
        List<BasketSummary.Item> items = List.of(
                new BasketSummary.Item(product2),
                new BasketSummary.Item(product1),
                new BasketSummary.Item(product2)
        );
        BasketSummary basketSummary = new BasketSummary(items, ZERO);

        // when
        productPromotionalRule.applyPromotion(basketSummary);

        // then
        assertThat(basketSummary.getDiscount()).isEqualTo(ZERO);
        assertThat(basketSummary.getItems())
                .extracting("product", "discounted", "price")
                .containsExactly(
                        tuple(product2, false, product2.getPrice()),
                        tuple(product1, false, product1.getPrice()),
                        tuple(product2, false, product2.getPrice())
                );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNewPriceIsNegative() {
        // when & then
        new OverTwoProductPromotionalRule(product1.getCode(), BigDecimal.valueOf(-1));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenNewPriceIsNull() {
        // when & then
        new OverTwoProductPromotionalRule(product1.getCode(), null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenProductCodeIsNull() {
        // when & then
        new OverTwoProductPromotionalRule(null, BigDecimal.valueOf(1));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenBasketIsNull() {
        // given
        OverTwoProductPromotionalRule productPromotionalRule = new OverTwoProductPromotionalRule(product1.getCode(), BigDecimal.valueOf(0.5));

        // when & then
        productPromotionalRule.applyPromotion(null);
    }

}