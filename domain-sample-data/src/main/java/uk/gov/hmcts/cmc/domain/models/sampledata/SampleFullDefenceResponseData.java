package uk.gov.hmcts.cmc.domain.models.sampledata;

import uk.gov.hmcts.cmc.domain.models.FullDefenceResponseData;

public class SampleFullDefenceResponseData extends SampleResponseData {

    protected String defence = "defence string";

    public static SampleFullDefenceResponseData builder() {
        return new SampleFullDefenceResponseData();
    }

    public SampleResponseData withDefence(final String defence) {
        this.defence = defence;
        return this;
    }

    @Override
    public FullDefenceResponseData build() {
        return new FullDefenceResponseData(
            freeMediationOption, moreTimeNeededOption, defendantDetails, statementOfTruth, defence
        );
    }
}
