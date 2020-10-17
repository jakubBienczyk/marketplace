# Marketplace

## Implementation

I've slightly changed the interface of checkout:
```
BasketSummaryFactory basketSummaryFactory = new BasketSummaryFactory();
Checkout co = new Checkout(promotionalRules, basketSummaryFactory);
co.scan(product1);
co.scan(product2);
Double price = co.total();
```

### Checkout

`Checkout` collects products in a list. When called for the total price, 
it uses `BasketSummaryFactory` to create basket summary which is then used to get the price.

### Basket summary

`BasketSummary` is a mutable class that contains all scanned products with information about discounts. 
Discounts are "applied" by promotional rules - they modify the state of basket summary. 

### Promotional rules

`PromotionalRule` has two methods:
* applyPromotion
* priority

While calculating total price, rules are applied in ascending order (by priority).
Each rule operates on `BasketSummary` and changes its status. 
Examples of the rules are in the `rules` package.


## Usage

Examples of usage and tests from the assignment are in `MarketplaceTest` class.

 
