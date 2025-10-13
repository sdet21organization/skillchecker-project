package tests.candidates;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pages.CandidatesPage;
import tests.BaseTest;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
@DisplayName("Search candidates functionality")
public class CandidatesSearchTests extends BaseTest {

    Faker fakerData = new Faker();

    static Stream<String> searchFields() {
        return Stream.of("email", "name", "position");
    }

    @ParameterizedTest(name = "Check candidates search by {0}")
    @MethodSource("searchFields")
    @DisplayName("Check candidates search by different fields")
    public void checkCandidatesSearchByDifferentFields(String searchType) {

        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage.open();

        String name = fakerData.name().fullName();
        String email = fakerData.internet().safeEmailAddress();
        String position =  fakerData.job().position() + LocalDateTime.now();

        candidatesPage.clickAddCandidateButton();
        candidatesPage.fillName(name);
        candidatesPage.fillEmail(email);
        candidatesPage.fillPosition(position);
        candidatesPage.clickModalButtonAddCandidate();

        Map<String, String> candidateData = Map.of("name", name, "email", email, "position", position);

        String searchValue = candidateData.get(searchType);
        candidatesPage.searchCandidateBy(searchValue);

        Map<String, Supplier<String>> tableValues = Map.of(
                "name", () -> candidatesPage.candidatesTableNames.textContent(),
                "email", () -> candidatesPage.candidatesTableEmails.textContent(),
                "position", () -> candidatesPage.candidatesTablePosition.textContent()
        );

        String actualValue = tableValues.get(searchType).get();

        assertEquals(
                candidateData.get(searchType), actualValue, "Candidate isn't found on page when searching by " + searchType
        );
    }
}
