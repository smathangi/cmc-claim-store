package uk.gov.hmcts.cmc.domain.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import uk.gov.hmcts.cmc.domain.models.legalrep.StatementOfTruth;
import uk.gov.hmcts.cmc.domain.models.party.Party;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "response")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = PartialAdmissionResponseData.class, name = "OWE_SOME_PAID_NONE"),
    }
)
public abstract class ResponseData {

    public enum FreeMediationOption {
        @JsonProperty("yes")
        YES,
        @JsonProperty("no")
        NO
    }

    public enum MoreTimeNeededOption {
        @JsonProperty("yes")
        YES,
        @JsonProperty("no")
        NO
    }

    @Valid
    @NotNull
    private final Party defendant;

    @Valid
    private final StatementOfTruth statementOfTruth;

    private final FreeMediationOption freeMediation;

    @JsonUnwrapped
    private final MoreTimeNeededOption moreTimeNeeded;

    public ResponseData(final String defence,
                        final FreeMediationOption freeMediation,
                        final MoreTimeNeededOption moreTimeNeeded,
                        final Party defendant,
                        final StatementOfTruth statementOfTruth) {
        this.freeMediation = freeMediation;
        this.moreTimeNeeded = moreTimeNeeded;
        this.defendant = defendant;
        this.statementOfTruth = statementOfTruth;
    }


    public Optional<FreeMediationOption> getFreeMediation() {
        return Optional.ofNullable(freeMediation);
    }

    public MoreTimeNeededOption getMoreTimeNeeded() {
        return moreTimeNeeded;
    }

    public Party getDefendant() {
        return defendant;
    }

    public Optional<StatementOfTruth> getStatementOfTruth() {
        return Optional.ofNullable(statementOfTruth);
    }

}
