package uk.gov.hmcts.reform.managecase.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.managecase.repository.IdamRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.JUnitAssertionsShouldIncludeMessage"})
class SecurityUtilsTest {

    private static final String CASEWORKER_BEFTA_JURISDICTION_SOLICITOR = "caseworker-befta_jurisdiction-solicitor";
    private static final String JURISDICTION = "befta_jurisdiction";

    @Mock
    private AuthTokenGenerator authTokenGenerator;

    @Mock
    private IdamRepository idamRepository;

    @InjectMocks
    private SecurityUtils securityUtils;

    private List<String> roles;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        roles = new ArrayList<>();
    }

    @Test
    void hasSolicitorRoleReturnsFalseWhenRolesEmpty() {
        assertFalse(securityUtils.hasSolicitorRole(roles, ""));
    }

    @Test
    void hasSolicitorRoleReturnsTrueWhenJurisdictionPresentInRoles() {
        roles.add(CASEWORKER_BEFTA_JURISDICTION_SOLICITOR);
        assertTrue(securityUtils.hasSolicitorRole(roles, CASEWORKER_BEFTA_JURISDICTION_SOLICITOR));
    }

    @Test
    void hasSolicitorRoleReturnsTrueWithMixedCase() {
        roles.add("caseworker-befta_jurisdiction-SoliciTor");
        assertTrue(securityUtils.hasSolicitorRole(roles, JURISDICTION));
    }

    @Test
    void hasSolicitorRoleReturnsTrueWithMultipleHyphens() {
        roles.add("caseworker-befta_jurisdiction-divorce-SoliciTor");
        assertTrue(securityUtils.hasSolicitorRole(roles, "befta_jurisdiction-divorce"));
    }

    @Test
    void hasSolicitorRoleReturnsFalseWithInvalidSuffix() {
        roles.add("caseworker-befta_jurisdiction-barrister");
        assertFalse(securityUtils.hasSolicitorRole(roles, JURISDICTION));
    }

    @Test
    void hasSolicitorRoleReturnsFalseWithSolicitorSuffixTypo() {
        roles.add("caseworker-befta_jurisdiction-solicito");
        assertFalse(securityUtils.hasSolicitorRole(roles, JURISDICTION));
    }

    @Test
    void hasSolicitorRoleReturnsFalseWithSolicitorSuffixTypoPlural() {
        roles.add("caseworker-befta_jurisdiction-solicitorsurname-solicitors");
        assertFalse(securityUtils.hasSolicitorRole(roles, JURISDICTION));
    }

    @Test
    void hasSolicitorRoleReturnsTrueWithExtendedJurisdictionValue() {
        roles.add("caseworker-befta_jurisdiction-solicitorsurname-solicitor");
        assertTrue(securityUtils.hasSolicitorRole(roles, "befta_jurisdiction-solicitorsurname"));
    }

    @Test
    void hasSolicitorRoleReturnsTrueWithJurisdictionMixedCaseInRoles() {
        roles.add("caseworker-BEFTA_jurisdiction-solicitor");
        assertTrue(securityUtils.hasSolicitorRole(roles, "BEFTA_jurisdiction"));
    }

    @Test
    void hasSolicitorRoleReturnsFalseWithAdditionalSuffixAppended() {
        roles.add("caseworker-befta_jurisdiction-solicitorsurname-solicitor-role");
        assertFalse(securityUtils.hasSolicitorRole(roles, "befta_jurisdiction-solicitorsurname"));
    }
}