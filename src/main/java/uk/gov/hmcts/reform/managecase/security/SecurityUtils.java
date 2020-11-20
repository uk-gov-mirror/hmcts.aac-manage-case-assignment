package uk.gov.hmcts.reform.managecase.security;

import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;
import uk.gov.hmcts.reform.managecase.repository.IdamRepository;

import javax.inject.Named;
import java.util.List;
import java.util.Locale;

@Named
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class SecurityUtils {

    private static final String SOLICITOR_ROLE = "caseworker-%s-solicitor";

    public static final String BEARER = "Bearer ";
    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    private final AuthTokenGenerator authTokenGenerator;
    private final IdamRepository idamRepository;

    @Autowired
    public SecurityUtils(final AuthTokenGenerator authTokenGenerator, IdamRepository idamRepository) {
        this.authTokenGenerator = authTokenGenerator;
        this.idamRepository = idamRepository;
    }

    public String getS2SToken() {
        return authTokenGenerator.generate();
    }

    public String getSystemUserToken() {
        return idamRepository.getSystemUserAccessToken();
    }

    public String getUserToken() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getTokenValue();
    }

    public String getUserBearerToken() {
        return BEARER + getUserToken();
    }

    public UserInfo getUserInfo() {
        return idamRepository.getUserInfo(getUserBearerToken());
    }

    public String getServiceNameFromS2SToken(String serviceAuthenticationToken) {
        // NB: this grabs the service name straight from the token under the assumption
        // that the S2S token has already been verified elsewhere
        return JWT.decode(removeBearerFromToken(serviceAuthenticationToken)).getSubject();
    }

    private String removeBearerFromToken(String token) {
        return token.startsWith(BEARER) ? token.substring(BEARER.length()) : token;
    }

    public boolean hasSolicitorRole(List<String> roles, String jurisdiction) {
        String solicitorRole = String.format(SOLICITOR_ROLE, jurisdiction).toLowerCase(Locale.getDefault());
        return roles.stream().anyMatch(role -> role.toLowerCase(Locale.getDefault()).startsWith(solicitorRole));
    }
}
