package uk.gov.hmcts.cmc.claimstore.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Objects;
import javax.validation.constraints.Size;

public class Address {

    @NotBlank(message = "Address Line1 should not be empty")
    @Size(max = 100, message = "Address Line1 should not be longer than {max} characters")
    private final String line1;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(max = 100, message = "Address Line2 should not be longer than {max} characters")
    private final String line2;

    @NotBlank(message = "City/town should not be empty")
    @Size(max = 100, message = "City should not be longer than {max} characters")
    private final String city;

    @NotBlank(message = "Postcode should not be empty")
    @Size(max = 8, message = "Postcode should not be longer than {max} characters")
    private final String postcode;

    public Address(final String line1,
                   final String line2,
                   final String city,
                   final String postcode) {
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.postcode = postcode;
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public String getCity() {
        return city;
    }

    public String getPostcode() {
        return postcode;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Address address = (Address) other;
        return Objects.equals(line1, address.line1)
            && Objects.equals(line2, address.line2)
            && Objects.equals(city, address.city)
            && Objects.equals(postcode, address.postcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line1, line2, city, postcode);
    }

    @Override
    public String toString() {
        return "Address{"
            + "line1='" + line1 + '\''
            + ", line2='" + line2 + '\''
            + ", city='" + city + '\''
            + ", postcode='" + postcode + '\''
            + '}';
    }
}