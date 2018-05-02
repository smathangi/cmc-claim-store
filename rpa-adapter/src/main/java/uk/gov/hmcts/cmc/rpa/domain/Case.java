package uk.gov.hmcts.cmc.rpa.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class Case {
    private String caseNumber;
    private String issueDate;
    private BigDecimal courtFee;
    private BigDecimal amountWithInterest;
    private List<Party> claimants;
    private List<Party> defendants;
    private String serviceDate;
}