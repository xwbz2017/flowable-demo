package me.xwbz.flowable;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = FlowableApplication.class)
@Transactional
public abstract class BaseTest {

    @Before
    public void setUp() {
    }
}
