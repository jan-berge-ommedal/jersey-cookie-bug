
import com.github.tomakehurst.wiremock.http.Cookie;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPattern.everything;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CookieFormatTest {

    private static final String TEST_COOKIE_1 = "test-cookie-1";
    private static final String TEST_COOKIE_2 = "test-cookie-1";
    private static final String TEST_COOKIE_VALUE = "test-value";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(0);

    @Test
    public void smoketest() {
        givenThat(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("ok!"))
        );


        JerseyClient jerseyClient = new JerseyClientBuilder().build();
        jerseyClient.target("http://localhost:" + wireMockRule.port())
                .request()
                .cookie(TEST_COOKIE_1, TEST_COOKIE_VALUE)
                .cookie(TEST_COOKIE_2, TEST_COOKIE_VALUE)
                .get(String.class);

        List<LoggedRequest> requests = wireMockRule.findRequestsMatching(everything()).getRequests();
        assertThat(requests.size(), equalTo(1));
        LoggedRequest loggedRequest = requests.get(0);

        Cookie cookie1 = loggedRequest.getCookies().get(TEST_COOKIE_1);
        assertThat(cookie1, notNullValue());
        assertThat(cookie1.getValue(), equalTo(TEST_COOKIE_VALUE));

        Cookie cookie2 = loggedRequest.getCookies().get(TEST_COOKIE_2);
        assertThat(cookie2, notNullValue());
        assertThat(cookie2.getValue(), equalTo(TEST_COOKIE_VALUE));
    }

}