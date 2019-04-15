package me.xwbz.flowable;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collection;

@WebAppConfiguration
public abstract class MockBaseTest extends BaseTest {

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext context;

//    private static final String token = "b3ab83bb4e804d0da17e681b657d88ca";

    @Before
    public void setUp() {
        Collection<Logger> current = LoggerContext.getContext().getLoggers();
        current.forEach(l -> l.setLevel(Level.DEBUG));
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    public abstract String getPath();

//    public MockHttpServletRequestBuilder setToken(MockHttpServletRequestBuilder builder, String token){
//        return builder.header(BapProperties.TOKEN_HEADER_KEY, token);
//    }
//
//    public MockHttpServletRequestBuilder addToken(MockHttpServletRequestBuilder builder){
//        return setToken(builder, token);
//    }
}
