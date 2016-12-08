package sorny.api;

import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Magnus on 2016-12-04.
 */
public class PredictionFormBeanTest {
    @Test
    public void canParseIsoDate() throws Exception {
        PredictionFormBean formBean = new PredictionFormBean();
        formBean.setUnravelDate("2014-01-15");
        assertThat(formBean.getUnravelDateAsDate().isPresent(), is(true));
        assertThat(LocalDate.of(2014, Month.JANUARY, 15), is(formBean.getUnravelDateAsDate().get()));
    }
}
