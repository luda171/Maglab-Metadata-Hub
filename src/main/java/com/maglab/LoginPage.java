package com.maglab;

import com.giffing.wicket.spring.boot.context.scan.WicketSignInPage;
import com.maglab.forms.LoginForm;

import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
//not used currently
@WicketSignInPage
public class LoginPage extends WebPage {

    public LoginPage(PageParameters parameters) {
        super(parameters);

        if (((AbstractAuthenticatedWebSession) getSession()).isSignedIn()) {
            continueToOriginalDestination();
        }
        add(new LoginForm("loginForm"));
    }
}
