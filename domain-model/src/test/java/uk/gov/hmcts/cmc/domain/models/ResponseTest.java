package uk.gov.hmcts.cmc.domain.models;

import org.junit.Test;
import uk.gov.hmcts.cmc.domain.models.sampledata.SampleResponse;
import uk.gov.hmcts.cmc.domain.utils.ResourceReader;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.cmc.domain.utils.BeanValidator.validate;

public class ResponseTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    public void shouldHaveNoValidationMessagesWhenResponseDataIsValid() {
        //given
        Response responseData = SampleResponse.validDefaults();
        //when
        Set<ConstraintViolation<Response>> response = validator.validate(responseData);
        //then
        assertThat(response).isEmpty();
    }

    @Test
    public void shouldHaveValidationMessagesWhenResponseDataElementsAreInValid() {
        //given
        Response response = SampleResponse.builder()
            .withDefence(null)
            .withResponseType(null)
            .build();

        //when
        Set<String> errors = validate(response);

        //then
        assertThat(errors)
            .hasSize(2)
            .contains(
                "type : may not be null",
                "defence : may not be empty"
            );
    }


    @Test
    public void shouldHaveValidationMessagesWhenDefenceDataElementIsEmpty() {
        //given
        Response response = SampleResponse.builder()
            .withDefence("")
            .withResponseType(null)
            .build();

        //when
        Set<String> errors = validate(response);

        //then
        assertThat(errors)
            .hasSize(2)
            .contains(
                "type : may not be null",
                "defence : may not be empty"
            );
    }

    @Test
    public void shouldHaveValidationMessagesWhenDefenceExceedsSizeLimit() {
        //given
        final String defence = new ResourceReader().read("/defence_exceeding_size_limit.text");

        Response response = SampleResponse.builder()
            .withDefence(defence).withResponseType(null).build();

        //when
        Set<String> errors = validate(response);

        //then
        assertThat(errors)
            .hasSize(2)
            .contains(
                "defence : size must be between 0 and 99000",
                "type : may not be null"
            );
    }
}