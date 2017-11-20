package uk.gov.hmcts.cmc.claimstore.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.cmc.claimstore.documents.CountyCourtJudgmentPdfService;
import uk.gov.hmcts.cmc.claimstore.documents.DefendantResponseCopyService;
import uk.gov.hmcts.cmc.domain.models.Claim;

@Service
public class DocumentsService {
    private final SealedClaimDocumentService sealedClaimDocumentService;
    private final ClaimService claimService;
    private final DefendantResponseCopyService defendantResponseCopyService;
    private final CountyCourtJudgmentPdfService countyCourtJudgmentPdfService;

    @Autowired
    public DocumentsService(
        final SealedClaimDocumentService sealedClaimDocumentService,
        final ClaimService claimService,
        final DefendantResponseCopyService defendantResponseCopyService,
        final CountyCourtJudgmentPdfService countyCourtJudgmentPdfService
    ) {

        this.sealedClaimDocumentService = sealedClaimDocumentService;
        this.claimService = claimService;
        this.defendantResponseCopyService = defendantResponseCopyService;
        this.countyCourtJudgmentPdfService = countyCourtJudgmentPdfService;
    }

    public byte[] generateDefendantResponseCopy(final String claimExternalId) {
        final Claim claim = claimService.getClaimByExternalId(claimExternalId);
        return defendantResponseCopyService.createPdf(claim);
    }

    public byte[] generateLegalSealedClaim(final String claimExternalId) {
        return sealedClaimDocumentService.generateLegalSealedClaim(claimExternalId);
    }

    public byte[] generateCountyCourtJudgement(final String claimExternalId) {
        final Claim claim = claimService.getClaimByExternalId(claimExternalId);
        return countyCourtJudgmentPdfService.createPdf(claim);
    }
}
