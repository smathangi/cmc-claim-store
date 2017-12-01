package uk.gov.hmcts.cmc.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.hmcts.cmc.domain.models.legalrep.StatementOfTruth;
import uk.gov.hmcts.cmc.domain.models.party.Party;
import uk.gov.hmcts.cmc.domain.models.response.DefendantPaymentPlan;
import uk.gov.hmcts.cmc.domain.models.response.Evidence;
import uk.gov.hmcts.cmc.domain.models.response.HowMuchOwed;
import uk.gov.hmcts.cmc.domain.models.response.Timeline;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static uk.gov.hmcts.cmc.domain.utils.ToStringStyle.ourStyle;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class PartialAdmissionResponseData extends ResponseData {

    @Valid
    private final Evidence evidence;

    @Valid
    @NotNull
    private final HowMuchOwed howMuchOwed;

    @Valid
    private final Timeline timeline;

    @Valid
    private final DefendantPaymentPlan defendantPaymentPlan;

    @NotBlank
    @Size(max = 99000)
    private final String impactOfDispute;

    public PartialAdmissionResponseData(
        final FreeMediationOption freeMediation,
        final MoreTimeNeededOption moreTimeNeeded,
        final Party defendant,
        final StatementOfTruth statementOfTruth,
        final Evidence evidence,
        final HowMuchOwed howMuchOwed,
        final Timeline timeline,
        final DefendantPaymentPlan defendantPaymentPlan,
        final String impactOfDispute) {
        super(freeMediation, moreTimeNeeded, defendant, statementOfTruth);
        this.evidence = evidence;
        this.howMuchOwed = howMuchOwed;
        this.timeline = timeline;
        this.defendantPaymentPlan = defendantPaymentPlan;
        this.impactOfDispute = impactOfDispute;
    }

    public String getImpactOfDispute() {
        return impactOfDispute;
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

        final PartialAdmissionResponseData that = (PartialAdmissionResponseData) other;
        return super.equals(that)
            && Objects.equals(evidence, that.evidence)
            && Objects.equals(howMuchOwed, that.howMuchOwed)
            && Objects.equals(timeline, that.timeline)
            && Objects.equals(defendantPaymentPlan, that.defendantPaymentPlan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(evidence, howMuchOwed, timeline, defendantPaymentPlan);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ourStyle());
    }

}
