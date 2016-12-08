package sorny.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sorny.domain.user.UserEntity;
import sorny.domain.user.UserService;

/**
 * Created by Magnus on 2016-12-01.
 */
@Controller
public class MainController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        UserEntity user = userService.getCurrentlyLoggedInUser();
        model.addAttribute("user", user);
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(value="username", required=false, defaultValue="admin") String username,
                        @RequestParam(value="password", required=false, defaultValue="admin") String password, Model model) {
        model.addAttribute("username", username);
        model.addAttribute("password", password);
        return "login";
    }
}
