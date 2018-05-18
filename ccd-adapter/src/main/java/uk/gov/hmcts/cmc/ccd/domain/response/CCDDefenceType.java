package uk.gov.hmcts.cmc.ccd.domain.response;

public enum CCDDefenceType {
    DISPUTE("dispute"),
    ALREADY_PAID("already paid"),
    PART_ADMISSION("I reject part of the claim"),
    FULL_ADMISSION("I admit all of the claim");

    private String value;

    CCDDefenceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
