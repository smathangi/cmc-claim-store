package uk.gov.hmcts.cmc.domain.models.response;

import org.hibernate.validator.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.Objects;

import javax.validation.constraints.Size;


public class HowMuchOwed {

    @NotBlank
    private final BigDecimal amount;

    @NotBlank
    @Size(max = 255, message = "must be at most {max} characters")
    private final String text;

    public HowMuchOwed(BigDecimal amount, String text) {
        this.amount = amount;
        this.text = text;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        HowMuchOwed that = (HowMuchOwed) obj;

        return Objects.equals(this.amount, that.text) && Objects.equals(this.amount, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, text);
    }

}