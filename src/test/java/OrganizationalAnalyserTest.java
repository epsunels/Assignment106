import com.company.OrganizationalAnalyser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrganizationalAnalyserTest {

    @Test
    public void testReadDataFromFile_FileNotFound(){
        Throwable exception = assertThrows (IllegalArgumentException.class,()->{
            new OrganizationalAnalyser().analyseOrganizationalInputFile ("non_existent_file.csv");
        });
        assertEquals ("File does not exists: non_existent_file.csv",exception.getMessage());
    }


    @Test
    public void testReadDataFromFile_HeaderNotFound(){
        Throwable exception = assertThrows (RuntimeException.class,()->{
            new OrganizationalAnalyser().analyseOrganizationalInputFile ("src/test/resources/HeaderNotFound.csv");
        });
        assertEquals ("Required headers not found in the input",exception.getMessage());
    }

    @Test
    public void testReadDataFromFile_MoreThan1000Lines(){
        Throwable exception = assertThrows (RuntimeException.class,()->{
            new OrganizationalAnalyser().analyseOrganizationalInputFile ("src/test/resources/LargeFile.csv");
        });
        assertEquals ("More than 1000 lines in the input file",exception.getMessage());
    }

    @Test
    public void testReadDataFromFile_MissingField(){
        Throwable exception = assertThrows (RuntimeException.class,()->{
            new OrganizationalAnalyser().analyseOrganizationalInputFile ("src/test/resources/MissingField.csv");
        });
        assertEquals ("Missing field for the employee at line 2",exception.getMessage());
    }

    @Test
    public void testReadDataFromFile_MultipleCEO(){
        Throwable exception = assertThrows (RuntimeException.class,()->{
            new OrganizationalAnalyser().analyseOrganizationalInputFile ("src/test/resources/MultipleCEO.csv");
        });
        assertEquals ("There cant be more than one ceo in the given file!",exception.getMessage());
    }

    @Test
    public void testAnalyseOrganizationalInputFile() throws IOException {
        String filePath = "src/test/resources/Employees.csv";
        OrganizationalAnalyser analyser = new OrganizationalAnalyser();
        OrganizationalAnalyser.ReportingResult result = analyser.analyseOrganizationalInputFile(filePath);

        List<String> expectedSalaryReports = Arrays.asList(
                "John Doe earns 37000 more than the threshold",
                "Alice Hasacat earns 29000 more than the threshold",
                "Martin Chekov earns 20000 more than the threshold",
                "Alice1 Hasacat1 earns 25000 more than the threshold",
                "Alice2 Hasacat2 earns 25000 more than the threshold",
                "Alice3 Hasacat3 earns 25000 more than the threshold"
        );

        List<String> expectedReportingItems = Arrays.asList(
                "Long reporting line for Alice2 Hasacat2 by 1 managers.",
                "Long reporting line for Alice3 Hasacat3 by 2 managers.",
                "Long reporting line for Alice4 Hasacat4 by 3 managers."
        );
        Collections.sort(expectedSalaryReports);
        Collections.sort(expectedReportingItems);

        List<String> actualSalaryReports = new ArrayList<>(result.salaryReports());
        List<String> actualReportingItems = new ArrayList<>(result.reportingItems());
        Collections.sort(actualSalaryReports);
        Collections.sort(actualReportingItems);

        assertEquals(expectedSalaryReports,actualSalaryReports);
        assertEquals(expectedReportingItems,actualReportingItems);

    }


}
