package my.project.summary;

import my.project.Product;
import my.project.PromotionalRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BasketSummaryFactoryTest {

    private final Product product1 = new Product("1", "product 1", BigDecimal.valueOf(1));
    private final Product product2 = new Product("2", "product 2", BigDecimal.valueOf(2));

    @Mock
    private PromotionalRule promotionalRule1, promotionalRule2;

    private final BasketSummaryFactory basketSummaryFactory = new BasketSummaryFactory();

    @Test
    public void shouldCreateBasketSummaryAndApplyPromotionalRules() {
        // given
        List<Product> products = List.of(product1, product2);
        List<PromotionalRule> rules = List.of(promotionalRule1, promotionalRule2);
        List<BasketSummary.Item> expectedItems = List.of(
                new BasketSummary.Item(product1, false, product1.getPrice()),
                new BasketSummary.Item(product2, false, product2.getPrice())
        );
        BasketSummary expectedBasketSummary = new BasketSummary(expectedItems, ZERO);

        // when
        BasketSummary basketSummary = basketSummaryFactory.createBasketSummary(products, rules);

        // then
        assertThat(basketSummary).isEqualTo(expectedBasketSummary);

        verify(promotionalRule1).applyPromotion(basketSummary);
        verify(promotionalRule2).applyPromotion(basketSummary);
    }

    @Test
    public void shouldApplyRulesInCorrectOrder() {
        // given
        List<Product> products = List.of(product1, product2);
        List<PromotionalRule> rules = List.of(promotionalRule1, promotionalRule2);

        // rule 1 will set discount to 1 with priority 11
        setDiscountWhenCalled(promotionalRule1, 1);
        given(promotionalRule1.priority()).willReturn(11);

        // rule 2 will set discount to 2 with priority 0
        setDiscountWhenCalled(promotionalRule2, 2);
        given(promotionalRule2.priority()).willReturn(0);

        // expected discount is 1 because rule 1 has bigger priority
        int expectedDiscount = 1;

        // when
        BasketSummary basketSummary = basketSummaryFactory.createBasketSummary(products, rules);

        // then
        assertThat(basketSummary.getDiscount()).isEqualTo(BigDecimal.valueOf(expectedDiscount));

        verify(promotionalRule1).applyPromotion(basketSummary);
        verify(promotionalRule2).applyPromotion(basketSummary);
    }

    @Test
    public void shouldCreateBasketSummaryWithoutPromotionalRules() {
        // given
        List<Product> products = List.of(product1, product2);
        List<BasketSummary.Item> expectedItems = List.of(
                new BasketSummary.Item(product1, false, product1.getPrice()),
                new BasketSummary.Item(product2, false, product2.getPrice())
        );
        BasketSummary expectedBasketSummary = new BasketSummary(expectedItems, ZERO);

        // when
        BasketSummary basketSummary = basketSummaryFactory.createBasketSummary(products, emptyList());

        // then
        assertThat(basketSummary).isEqualTo(expectedBasketSummary);
    }

    @Test
    public void shouldCreateBasketSummaryWithoutProducts() {
        // given
        List<PromotionalRule> rules = List.of(promotionalRule1, promotionalRule2);
        BasketSummary expectedBasketSummary = new BasketSummary(emptyList(), ZERO);

        // when
        BasketSummary basketSummary = basketSummaryFactory.createBasketSummary(emptyList(), rules);

        // then
        assertThat(basketSummary).isEqualTo(expectedBasketSummary);

        verify(promotionalRule1).applyPromotion(basketSummary);
        verify(promotionalRule2).applyPromotion(basketSummary);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenProductsAreNull() {
        // given
        List<PromotionalRule> rules = List.of(promotionalRule1, promotionalRule2);

        // when & then
        basketSummaryFactory.createBasketSummary(null, rules);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenRulesAreNull() {
        // given
        List<Product> products = List.of(product1, product2);

        // when & then
        basketSummaryFactory.createBasketSummary(products, null);
    }

    private void setDiscountWhenCalled(PromotionalRule promotionalRule, int discount) {
        willAnswer(a -> {
            a.getArgumentAt(0, BasketSummary.class).setDiscount(BigDecimal.valueOf(discount));
            return a;
        }).given(promotionalRule).applyPromotion(any());
    }

}