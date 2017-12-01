package uk.gov.hmcts.cmc.domain.models.sampledata;

import uk.gov.hmcts.cmc.domain.models.ResponseData;
import uk.gov.hmcts.cmc.domain.models.legalrep.StatementOfTruth;
import uk.gov.hmcts.cmc.domain.models.party.Party;

public class SampleResponseData {

    protected ResponseData.ResponseType responseType = ResponseData.ResponseType.OWE_NONE;
    protected ResponseData.FreeMediationOption freeMediationOption = ResponseData.FreeMediationOption.YES;
    protected ResponseData.MoreTimeNeededOption moreTimeNeededOption = ResponseData.MoreTimeNeededOption.YES;
    protected Party defendantDetails = SampleParty.builder().withRepresentative(null).individual();
    protected StatementOfTruth statementOfTruth;

    public static SampleResponseData builder() {
        return new SampleResponseData();
    }

    public static ResponseData validDefaults() {
        return builder().build();
    }

    public SampleResponseData withMediation(final ResponseData.FreeMediationOption freeMediationOption) {
        this.freeMediationOption = freeMediationOption;
        return this;
    }

    public SampleResponseData withDefendantDetails(final Party sampleDefendantDetails) {
        this.defendantDetails = sampleDefendantDetails;
        return this;
    }

    public ResponseData build() {
        return new ResponseData(
            freeMediationOption, moreTimeNeededOption, defendantDetails, statementOfTruth
        );
    }

    public SampleResponseData withStatementOfTruth(final String signerName, final String signerRole) {
        this.statementOfTruth = new StatementOfTruth(signerName,signerRole);
        return this;
    }

}
