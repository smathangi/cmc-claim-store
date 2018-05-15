package uk.gov.hmcts.cmc.rpa.mapper;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.cmc.domain.models.Address;
import uk.gov.hmcts.cmc.domain.models.Claim;
import uk.gov.hmcts.cmc.domain.models.otherparty.OrganisationDetails;
import uk.gov.hmcts.cmc.domain.models.otherparty.SoleTraderDetails;
import uk.gov.hmcts.cmc.domain.models.otherparty.TheirDetails;
import uk.gov.hmcts.cmc.domain.models.party.HasContactPerson;
import uk.gov.hmcts.cmc.domain.models.party.Individual;
import uk.gov.hmcts.cmc.domain.models.party.Organisation;
import uk.gov.hmcts.cmc.domain.models.party.Party;
import uk.gov.hmcts.cmc.domain.models.party.SoleTrader;
import uk.gov.hmcts.cmc.domain.models.party.TitledParty;
import uk.gov.hmcts.cmc.rpa.DateFormatter;
import uk.gov.hmcts.cmc.rpa.mapper.json.NullAwareJsonObjectBuilder;

import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonCollectors;

import static javax.json.Json.createObjectBuilder;

@Component
@SuppressWarnings({"LineLength"})
public class SealedClaimJsonMapper extends BaseMapper {

    public JsonObject map(Claim claim) {
        return new NullAwareJsonObjectBuilder()
            .add("caseNumber", claim.getReferenceNumber())
            .add("issueDate", DateFormatter.format(claim.getIssuedOn()))
            .add("serviceDate", DateFormatter.format(claim.getIssuedOn().plusDays(5)))
            .add("courtFee", claim.getClaimData().getFeesPaidInPound())
            .add("amountWithInterest", claim.getTotalAmountTillToday().orElse(null))
            .add("claimants", mapClaimants(claim.getClaimData().getClaimants()))
            .add("defendants", mapDefendants(claim.getClaimData().getDefendants()))
            .build();
    }

    private JsonArray mapClaimants(List<Party> claimants) {
        return claimants.stream()
            .map(claimant -> new NullAwareJsonObjectBuilder()
                .add("type", claimant.getClass().getSimpleName())
                .add("title", extractOptionalFromSubclass(claimant, TitledParty.class, TitledParty::getTitle))
                .add("fullName", claimant.getName())
                .add("fullAddress", mapAddress(claimant.getAddress()))
                .add("correspondenceAddress", claimant.getCorrespondenceAddress().map(this::mapAddress).orElse(null))
                .add("phoneNumber", claimant.getMobilePhone().orElse(null))
                .add("dateOfBirth", extractFromSubclass(claimant, Individual.class, individual -> DateFormatter.format(individual.getDateOfBirth())))
                .add("businessName", extractOptionalFromSubclass(claimant, SoleTrader.class, SoleTrader::getBusinessName))
                .add("contactPerson", extractOptionalFromSubclass(claimant, HasContactPerson.class, HasContactPerson::getContactPerson))
                .add("companiesHouseNumber", extractOptionalFromSubclass(claimant, Organisation.class, Organisation::getCompaniesHouseNumber))
                .build())
            .collect(JsonCollectors.toJsonArray());
    }

    private JsonArray mapDefendants(List<TheirDetails> defendants) {
        return defendants.stream()
            .map(defendant -> new NullAwareJsonObjectBuilder()
                .add("type", defendant.getClass().getSimpleName().replace("Details", ""))
                .add("title", extractOptionalFromSubclass(defendant, TitledParty.class, TitledParty::getTitle))
                .add("fullName", defendant.getName())
                .add("fullAddress", mapAddress(defendant.getAddress()))
                .add("correspondenceAddress", defendant.getServiceAddress().map(this::mapAddress).orElse(null))
                .add("emailAddress", defendant.getEmail().orElse(null))
                .add("businessName", extractOptionalFromSubclass(defendant, SoleTraderDetails.class, SoleTraderDetails::getBusinessName))
                .add("contactPerson", extractOptionalFromSubclass(defendant, HasContactPerson.class, HasContactPerson::getContactPerson))
                .add("companiesHouseNumber", extractOptionalFromSubclass(defendant, OrganisationDetails.class, OrganisationDetails::getCompaniesHouseNumber))
                .build())
            .collect(JsonCollectors.toJsonArray());
    }

    private JsonObject mapAddress(Address address) {
        return createObjectBuilder()
            .add("line1", address.getLine1())
            .add("line2", address.getLine2())
            .add("line3", address.getLine3())
            .add("city", address.getCity())
            .add("postcode", address.getPostcode())
            .build();
    }

}
