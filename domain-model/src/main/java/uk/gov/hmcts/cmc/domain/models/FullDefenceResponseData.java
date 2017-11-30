package uk.gov.hmcts.cmc.domain.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.hmcts.cmc.domain.models.legalrep.StatementOfTruth;
import uk.gov.hmcts.cmc.domain.models.party.Party;

import java.util.Objects;
import javax.validation.constraints.Size;

import static uk.gov.hmcts.cmc.domain.utils.ToStringStyle.ourStyle;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class FullDefenceResponseData extends ResponseData {

    @NotBlank
    @Size(max = 99000)
    private final String defence;

    @JsonCreator
    public FullDefenceResponseData(
        @JsonProperty("freeMediation") FreeMediationOption freeMediation,
        @JsonProperty("moreTimeNeeded") MoreTimeNeededOption moreTimeNeeded,
        @JsonProperty("defendant") Party defendant,
        @JsonProperty("statementOfTruth") StatementOfTruth statementOfTruth,
        @JsonProperty("defence") String defence
    ) {
        super(freeMediation, moreTimeNeeded, defendant, statementOfTruth);
        this.defence = defence;
    }

    public String getDefence() {
        return defence;
    }

    @Override
    @SuppressWarnings("squid:S1067") // Its generated code for equals sonar
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final FullDefenceResponseData that = (FullDefenceResponseData) other;
        return super.equals(that) && Objects.equals(defence, that.defence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defence);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ourStyle());
    }
}
