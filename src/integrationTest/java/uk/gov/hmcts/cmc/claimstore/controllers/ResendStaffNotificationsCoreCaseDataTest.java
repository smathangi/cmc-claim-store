package uk.gov.hmcts.cmc.claimstore.controllers;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.cmc.claimstore.BaseGetTest;
import uk.gov.hmcts.cmc.claimstore.idam.models.GeneratePinResponse;
import uk.gov.hmcts.cmc.claimstore.idam.models.User;
import uk.gov.hmcts.cmc.claimstore.idam.models.UserDetails;
import uk.gov.hmcts.cmc.claimstore.services.notifications.fixtures.SampleUserDetails;
import uk.gov.hmcts.cmc.domain.models.Claim;
import uk.gov.hmcts.cmc.domain.models.sampledata.SampleClaim;
import uk.gov.hmcts.cmc.email.EmailData;
import uk.gov.hmcts.reform.sendletter.api.SendLetterApi;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.cmc.claimstore.repositories.CCDCaseApi.CASE_TYPE_ID;
import static uk.gov.hmcts.cmc.claimstore.repositories.CCDCaseApi.JURISDICTION_ID;
import static uk.gov.hmcts.cmc.claimstore.utils.ResourceLoader.listOfCaseDetails;
import static uk.gov.hmcts.cmc.claimstore.utils.ResourceLoader.listOfCaseDetailsWithDefResponse;
import static uk.gov.hmcts.cmc.claimstore.utils.ResourceLoader.listOfCaseDetailsWithDefendant;

@TestPropertySource(
    properties = {
        "document_management.api_gateway.url=false",
        "core_case_data.api.url=http://core-case-data-api"
    }
)
public class ResendStaffNotificationsCoreCaseDataTest extends BaseGetTest {

    private static final String CASE_REFERENCE = "000MC023";

    @MockBean
    protected SendLetterApi sendLetterApi;

    @Captor
    private ArgumentCaptor<EmailData> emailDataArgument;

    private static final String PAGE = "1";

    @Before
    public void setUp() {
        given(pdfServiceClient.generateFromHtml(any(byte[].class), anyMap()))
            .willReturn(new byte[]{1, 2, 3, 4});
        UserDetails userDetails = SampleUserDetails.getDefault();
        User user = new User(AUTHORISATION_TOKEN, userDetails);
        given(userService.getUserDetails(anyString())).willReturn(userDetails);
        given(userService.authenticateAnonymousCaseWorker()).willReturn(user);
        given(userService.getUser(anyString())).willReturn(user);
        given(authTokenGenerator.generate()).willReturn(SERVICE_TOKEN);
    }

    @Test
    public void shouldRespond404WhenClaimDoesNotExist() throws Exception {
        final String nonExistingClaimReference = "something";
        final String event = "claim-issue";
        givenForSearchForCitizen(CASE_REFERENCE);
        ResultActions result = makeRequest(nonExistingClaimReference, event);
        assertThat(result).isNotNull();
        result.andExpect(status().isNotFound());
    }

    @Test
    public void shouldRespond404WhenEventIsNotSupported() throws Exception {
        final String nonExistingEvent = "some-event";

        givenForSearchForCitizen(CASE_REFERENCE);
        ResultActions result = makeRequest(CASE_REFERENCE, nonExistingEvent);
        assertThat(result).isNotNull();
        result.andExpect(status().isNotFound());
    }

    @Test
    public void shouldRespond409AndNotProceedForClaimIssuedEventWhenClaimIsLinkedToDefendant() throws Exception {
        final String event = "claim-issued";

        givenForSearchForCitizenAndDef(CASE_REFERENCE);

        makeRequest(CASE_REFERENCE, event).andExpect(status().isConflict());

        verify(emailService, never()).sendEmail(any(), any());
    }

    @Test
    public void shouldRespond200AndSendNotificationsForClaimIssuedEvent() throws Exception {
        final String event = "claim-issued";
        final Claim claim = SampleClaim.builder()
            .withReferenceNumber(CASE_REFERENCE)
            .withDefendantId(null).build();

        GeneratePinResponse pinResponse = new GeneratePinResponse("pin-123", "333");
        given(userService.generatePin(anyString(), eq("ABC123"))).willReturn(pinResponse);
        given(sendLetterApi.sendLetter(any(), any())).willReturn(new SendLetterResponse(UUID.randomUUID()));
        givenForSearchForCitizen(CASE_REFERENCE);

        makeRequest(claim.getReferenceNumber(), event).andExpect(status().isOk());

        verify(emailService, times(2)).sendEmail(eq("sender@example.com"), emailDataArgument.capture());

        EmailData emailData = emailDataArgument.getValue();
        assertThat(emailData.getTo()).isEqualTo("recipient@example.com");
        assertThat(emailData.getSubject()).isEqualTo("Claim " + claim.getReferenceNumber() + " issued");
        assertThat(emailData.getMessage()).isEqualTo("Please find attached claim.");
    }

    @Test
    public void shouldRespond409AndNotProceedForMoreTimeRequestedEventWhenMoreTimeNotRequested() throws Exception {
        final String event = "more-time-requested";

        givenForSearchForCitizen(CASE_REFERENCE);

        makeRequest(CASE_REFERENCE, event).andExpect(status().isConflict());

        verify(notificationClient, never()).sendEmail(any(), any(), any(), any());
    }

    @Test
    public void shouldRespond200AndSendNotificationsForMoreTimeRequestedEvent() throws Exception {
        final String event = "more-time-requested";

        givenForSearchForCitizenAndDef(CASE_REFERENCE);

        makeRequest(CASE_REFERENCE, event).andExpect(status().isOk());

        verify(notificationClient).sendEmail(eq("staff-more-time-requested-template"),
            eq("recipient@example.com"),
            any(),
            eq("more-time-requested-notification-to-staff-" + CASE_REFERENCE));
    }

    @Test
    public void shouldRespond409AndNotProceedForResponseSubmittedEventWhenResponseNotSubmitted() throws Exception {
        final String event = "response-submitted";

        givenForSearchForCitizen(CASE_REFERENCE);

        makeRequest(CASE_REFERENCE, event).andExpect(status().isConflict());

        verify(emailService, never()).sendEmail(any(), any());
    }

    @Test
    public void shouldRespond200AndSendNotificationsForResponseSubmittedEvent() throws Exception {
        final String event = "response-submitted";

        given(coreCaseDataApi.searchForCitizen(
            eq(AUTHORISATION_TOKEN),
            eq(SERVICE_TOKEN),
            eq(USER_ID),
            eq(JURISDICTION_ID),
            eq(CASE_TYPE_ID),
            eq(ImmutableMap.of("case.referenceNumber", CASE_REFERENCE, "page", PAGE))
            )
        ).willReturn(listOfCaseDetailsWithDefResponse());

        makeRequest(CASE_REFERENCE, event).andExpect(status().isOk());

        verify(emailService).sendEmail(eq("sender@example.com"), emailDataArgument.capture());

        assertThat(emailDataArgument.getValue().getTo()).isEqualTo("recipient@example.com");
        assertThat(emailDataArgument.getValue().getSubject())
            .isEqualTo("Civil Money Claim defence submitted: John Rambo v John Smith " + CASE_REFERENCE);
        assertThat(emailDataArgument.getValue().getMessage()).contains(
            "The defendant has submitted a full defence which is attached as a PDF",
            "Email: j.smith@example.com",
            "Mobile number: 07873727165"
        );
    }

    private void givenForSearchForCitizen(String caseReference) {
        given(coreCaseDataApi.searchForCitizen(
            any(),
            any(),
            any(),
            any(),
            any(),
            eq(ImmutableMap.of("case.referenceNumber", caseReference, "page", PAGE))
            )
        ).willReturn(listOfCaseDetails());
    }

    private void givenForSearchForCitizenAndDef(String caseReference) {
        given(coreCaseDataApi.searchForCitizen(
            any(),
            any(),
            any(),
            any(),
            any(),
            eq(ImmutableMap.of("case.referenceNumber", caseReference, "page", PAGE))
            )
        ).willReturn(listOfCaseDetailsWithDefendant());
    }

    private ResultActions makeRequest(String referenceNumber, String event) throws Exception {
        return webClient
            .perform(put("/support/claim/" + referenceNumber + "/event/" + event + "/resend-staff-notifications")
                .header(HttpHeaders.AUTHORIZATION, "ABC123"));
    }
}
