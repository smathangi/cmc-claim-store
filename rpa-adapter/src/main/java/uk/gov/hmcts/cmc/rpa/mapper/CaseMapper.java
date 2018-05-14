package uk.gov.hmcts.cmc.rpa.mapper;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.cmc.domain.models.Claim;
import uk.gov.hmcts.cmc.rpa.DateFormatter;
import uk.gov.hmcts.cmc.rpa.domain.Case;

import java.util.stream.Collectors;

@Component("rpaCaseMapper")
public class CaseMapper {
    private final PartyMapper rpaPartyMapper;
    private final TheirDetailsMapper rpaTheirDetailsMapper;

    public CaseMapper(PartyMapper rpaPartyMapper, TheirDetailsMapper rpaTheirDetailsMapper) {
        this.rpaPartyMapper = rpaPartyMapper;
        this.rpaTheirDetailsMapper = rpaTheirDetailsMapper;
    }

    public Case to(Claim claim) {
        Case.CaseBuilder builder = Case.builder();
        claim.getTotalAmountTillToday().ifPresent(builder::amountWithInterest);
        builder.caseNumber(claim.getReferenceNumber());
        builder.courtFee(claim.getClaimData().getFeesPaidInPound());
        builder.issueDate(DateFormatter.format(claim.getIssuedOn()));
        builder.serviceDate(DateFormatter.format(claim.getIssuedOn().plusDays(5)));

        builder.claimants(claim.getClaimData().getClaimants().stream()
            .map(rpaPartyMapper::to)
            .collect(Collectors.toList()));

        builder.defendants(claim.getClaimData().getDefendants().stream()
            .map(rpaTheirDetailsMapper::to)
            .collect(Collectors.toList()));

        return builder.build();
    }
}
