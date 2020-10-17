package my.project;

import my.project.summary.BasketSummary;
import my.project.summary.BasketSummaryFactory;
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

@RunWith(MockitoJUnitRunner.class)
public class CheckoutTest {

    private final Product product1 = new Product("1", "product 1", BigDecimal.valueOf(1));
    private final Product product2 = new Product("2", "product 2", BigDecimal.valueOf(2));

    @Mock
    private PromotionalRule promotionalRule1, promotionalRule2;

    @Mock
    private BasketSummary basketSummary;

    @Mock
    private BasketSummaryFactory basketSummaryFactory;

    @Test
    public void shouldScanProductsAndReturnTotalPrice() {
        // given
        double expectedTotal = 12.34;
        given(basketSummary.totalPrice()).willReturn(BigDecimal.valueOf(expectedTotal));
        given(basketSummaryFactory.createBasketSummary(List.of(product1, product2), List.of(promotionalRule1, promotionalRule2)))
                .willReturn(basketSummary);
        Checkout checkout = new Checkout(List.of(promotionalRule1, promotionalRule2), basketSummaryFactory);

        // when
        checkout.scan(product1);
        checkout.scan(product2);
        Double total = checkout.total();

        // then
        assertThat(total).isEqualTo(expectedTotal);
    }

    @Test
    public void shouldScanProductsAndReturnZeroWhenTotalPriceIsNegative() {
        // given
        given(basketSummary.totalPrice()).willReturn(BigDecimal.valueOf(-123));
        given(basketSummaryFactory.createBasketSummary(List.of(product1, product2), List.of(promotionalRule1, promotionalRule2)))
                .willReturn(basketSummary);
        Checkout checkout = new Checkout(List.of(promotionalRule1, promotionalRule2), basketSummaryFactory);

        // when
        checkout.scan(product1);
        checkout.scan(product2);
        Double total = checkout.total();

        // then
        assertThat(total).isEqualTo(0);
    }

    @Test
    public void shouldScanProductsAndReturnTotalPriceScaledToTwoDigits() {
        // given
        double expectedTotal = 12.34;
        double totalValue = 12.33567;
        given(basketSummary.totalPrice()).willReturn(BigDecimal.valueOf(totalValue));
        given(basketSummaryFactory.createBasketSummary(List.of(product1, product2), List.of(promotionalRule1, promotionalRule2)))
                .willReturn(basketSummary);
        Checkout checkout = new Checkout(List.of(promotionalRule1, promotionalRule2), basketSummaryFactory);

        // when
        checkout.scan(product1);
        checkout.scan(product2);
        Double total = checkout.total();

        // then
        assertThat(total).isEqualTo(expectedTotal);
    }

    @Test
    public void shouldReturnZeroWhenNoProductsWereScan() {
        // given
        given(basketSummary.totalPrice()).willReturn(ZERO);
        given(basketSummaryFactory.createBasketSummary(emptyList(), List.of(promotionalRule1, promotionalRule2)))
                .willReturn(basketSummary);
        Checkout checkout = new Checkout(List.of(promotionalRule1, promotionalRule2), basketSummaryFactory);

        // when
        Double total = checkout.total();

        // then
        assertThat(total).isEqualTo(0);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenPromotionalRulesAreNull() {
        // when & then
        new Checkout(null, basketSummaryFactory);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenBasketSummaryFactoryIsNull() {
        // when & then
        new Checkout(List.of(promotionalRule1, promotionalRule2), null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenProductToScanIsNull() {
        // given
        Checkout checkout = new Checkout(List.of(promotionalRule1, promotionalRule2), basketSummaryFactory);

        // when & then
        checkout.scan(null);
    }

}