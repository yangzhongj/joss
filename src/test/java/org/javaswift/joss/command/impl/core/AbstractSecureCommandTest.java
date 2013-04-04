package org.javaswift.joss.command.impl.core;

import org.javaswift.joss.client.impl.AccountImpl;
import org.javaswift.joss.command.impl.container.CreateContainerCommandImpl;
import org.javaswift.joss.command.impl.identity.AuthenticationCommandImpl;
import org.javaswift.joss.command.shared.identity.access.AccessImpl;
import org.javaswift.joss.exception.UnauthorizedException;
import org.javaswift.joss.headers.Token;
import org.apache.http.Header;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AbstractSecureCommandTest extends BaseCommandTest {

    @Mock
    AuthenticationCommandImpl authCommand;

    @Before
    public void setup() throws IOException {
        super.setup();
    }

    @Test
    public void checkAuthenticationToken() {
        when(statusLine.getStatusCode()).thenReturn(201);
        CreateContainerCommandImpl command =
            new CreateContainerCommandImpl(this.account, httpClient, defaultAccess, account.getContainer("containerName"));
        Header[] xAuth = command.request.getHeaders(Token.X_AUTH_TOKEN);
        assertEquals(1, xAuth.length);
        assertEquals("cafebabe", xAuth[0].getValue());
    }

    @Test
    public void reauthenticateSuccess() {
        when(statusLine.getStatusCode()).thenReturn(401).thenReturn(201);
        when(authCommand.call()).thenReturn(new AccessImpl());
        this.account = new AccountImpl(authCommand, httpClient, defaultAccess, null, true);
        new CreateContainerCommandImpl(this.account, httpClient, defaultAccess, account.getContainer("containerName")).call();
    }

    @Test(expected = UnauthorizedException.class)
    public void reauthenticateNotAllowed() {
        when(statusLine.getStatusCode()).thenReturn(401);
        this.account.setAllowReauthenticate(false);
        new CreateContainerCommandImpl(this.account, httpClient, defaultAccess, account.getContainer("containerName")).call();
    }

    @Test(expected = UnauthorizedException.class)
    public void reauthenticateFailTwice() {
        when(statusLine.getStatusCode()).thenReturn(401);
        when(authCommand.call()).thenReturn(new AccessImpl());
        this.account = new AccountImpl(authCommand, httpClient, defaultAccess, null, true);
        new CreateContainerCommandImpl(this.account, httpClient, defaultAccess, account.getContainer("containerName")).call();
    }
}
