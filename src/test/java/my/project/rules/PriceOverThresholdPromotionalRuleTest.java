package my.project.rules;

import my.project.summary.BasketSummary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PriceOverThresholdPromotionalRuleTest {

    @Mock
    private BasketSummary basketSummary;

    @Test
    public void shouldApplyPromotionWhenTotalValueIsOverThreshold() {
        // given
        PriceOverThresholdPromotionalRule promotionalRule = new PriceOverThresholdPromotionalRule(BigDecimal.valueOf(60), 10);
        given(basketSummary.totalPrice()).willReturn(BigDecimal.valueOf(123));
        given(basketSummary.getDiscount()).willReturn(BigDecimal.valueOf(13));
        BigDecimal expectedDiscount = BigDecimal.valueOf(123 * 0.1 + 13);

        // when
        promotionalRule.applyPromotion(basketSummary);

        // then
        verify(basketSummary).setDiscount(expectedDiscount);
    }

    @Test
    public void shouldNotApplyPromotionWhenTotalValueIsBelowThreshold() {
        // given
        PriceOverThresholdPromotionalRule promotionalRule = new PriceOverThresholdPromotionalRule(BigDecimal.valueOf(60), 10);
        given(basketSummary.totalPrice()).willReturn(BigDecimal.valueOf(12));

        // when
        promotionalRule.applyPromotion(basketSummary);

        // then
        verify(basketSummary, never()).setDiscount(BigDecimal.valueOf(12.3));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenThresholdIsNull() {
        // when & then
        new PriceOverThresholdPromotionalRule(null, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenThresholdIsNegative() {
        // when & then
        new PriceOverThresholdPromotionalRule(BigDecimal.valueOf(-1), 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenReductionOfPriceIsNegative() {
        // when & then
        new PriceOverThresholdPromotionalRule(BigDecimal.valueOf(60), -1);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenBasketSummaryIsNull() {
        // given
        PriceOverThresholdPromotionalRule promotionalRule = new PriceOverThresholdPromotionalRule(BigDecimal.valueOf(60), 10);

        // when & then
        promotionalRule.applyPromotion(null);
    }

}