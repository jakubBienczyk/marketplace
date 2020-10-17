package my.project;

import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class Product {

    @NonNull
    String code;

    @NonNull
    String name;

    @NonNull
    BigDecimal price;

}
