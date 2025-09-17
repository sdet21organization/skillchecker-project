package tests.candidates;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pages.CandidatesPage;
import tests.BaseTest;

public class CandidatesTests extends BaseTest {


    @Test
    public void checkButtonisV (){
        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage.open();
String currentURL = context.page.url();
        Assertions.assertEquals("https://skillchecker.tech/dashboard/candidates",currentURL);

    }
}
