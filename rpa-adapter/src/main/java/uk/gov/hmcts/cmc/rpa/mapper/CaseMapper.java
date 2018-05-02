package uk.gov.hmcts.cmc.rpa.mapper;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.cmc.domain.models.Claim;
import uk.gov.hmcts.cmc.rpa.domain.Case;

import java.util.stream.Collectors;

import static uk.gov.hmcts.cmc.domain.utils.DatesProvider.MEDIUM_DATE_FORMAT;

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
        builder.issueDate(claim.getIssuedOn().format(MEDIUM_DATE_FORMAT));
        builder.serviceDate(claim.getIssuedOn().plusDays(5).format(MEDIUM_DATE_FORMAT));

        builder.claimants(claim.getClaimData().getClaimants().stream()
            .map(rpaPartyMapper::to)
            .collect(Collectors.toList()));

        builder.defendants(claim.getClaimData().getDefendants().stream()
            .map(rpaTheirDetailsMapper::to)
            .collect(Collectors.toList()));

        return builder.build();
    }
}