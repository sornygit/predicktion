package sorny.domain.user;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    private UserEntity user;

    @Before
    public void setUp() throws Exception {
        userRepository.deleteAllInBatch();
        UserEntity user = new UserEntity("testUser", "test!12Pw", "plugdjusername@email.com");
        user = userRepository.save(user);
    }

    @After
    public void tearDown() throws Exception {
        userRepository.deleteAllInBatch();
    }

    @Test
    public void testCreateNewAndGetUser() throws Exception {
        UserEntity user = userService.signUp("username", "123=password", "another@email.com");

        user = userService.persist(user);

        assertEquals("username", user.getUsername());

        UserEntity saved = userService.getByUsername(user.getUsername());

        assertEquals(user, saved);
    }
}
