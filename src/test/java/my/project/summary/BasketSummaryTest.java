package my.project.summary;


import my.project.Product;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class BasketSummaryTest {

    private final Product product1 = new Product("1", "product 1", BigDecimal.valueOf(1));
    private final Product product2 = new Product("2", "product 2", BigDecimal.valueOf(2));

    @Test
    public void shouldCreateBasketSummaryFromProducts() {
        // given
        List<Product> products = List.of(product1, product2);

        // when
        BasketSummary basketSummary = new BasketSummary(products);

        // then
        assertThat(basketSummary.getDiscount()).isEqualTo(ZERO);
        assertThat(basketSummary.getItems())
                .extracting("product", "discounted", "price")
                .containsExactly(
                        tuple(product1, false, product1.getPrice()),
                        tuple(product2, false, product2.getPrice())
                );
    }

    @Test
    public void shouldCalculateTotalPrice() {
        // given
        List<BasketSummary.Item> items = List.of(
                new BasketSummary.Item(product1, false, product1.getPrice()),
                new BasketSummary.Item(product2, false, product2.getPrice()),
                new BasketSummary.Item(product1, true, BigDecimal.valueOf(0.5))
        );
        BasketSummary basketSummary = new BasketSummary(items, BigDecimal.valueOf(1.25));

        // when
        BigDecimal totalPrice = basketSummary.totalPrice();

        // then
        assertThat(totalPrice).isEqualTo(BigDecimal.valueOf(1 + 2 + 0.5 - 1.25));
    }

    @Test
    public void shouldSetNewPriceOfItem() {
        // given
        BasketSummary.Item item = new BasketSummary.Item(product1, false, product1.getPrice());

        // when
        item.discount(BigDecimal.valueOf(0.5));

        // then
        assertThat(item.getPrice()).isEqualTo(BigDecimal.valueOf(0.5));
        assertThat(item.isDiscounted()).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenProductsAreNull() {
        // when & then
        new BasketSummary(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenProductIsNull() {
        // when & then
        new BasketSummary(List.of(product1, null, product2));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenNewPriceIsNull() {
        // given
        BasketSummary.Item item = new BasketSummary.Item(product1, false, product1.getPrice());

        // when & then
        item.discount(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNewPriceIsNegative() {
        // given
        BasketSummary.Item item = new BasketSummary.Item(product1, false, product1.getPrice());

        // when & then
        item.discount(BigDecimal.valueOf(-1));
    }

}